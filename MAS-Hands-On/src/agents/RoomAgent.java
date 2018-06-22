package agents;

import base.BaseAgent;
import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import javax.swing.*;

import static agents.Constants.*;
import static jade.lang.acl.ACLMessage.INFORM;
import static jade.lang.acl.ACLMessage.QUERY_IF;
import static jade.lang.acl.ACLMessage.QUERY_REF;
import static jade.lang.acl.MessageTemplate.MatchPerformative;
import static jade.lang.acl.MessageTemplate.MatchProtocol;

public class RoomAgent extends BaseAgent {
    private static final long serialVersionUID = -7184405101725139883L;

    private final JLabel usersLabel = new JLabel("Users");
    private final JLabel devicesLabel = new JLabel("Devices");

    private final JTextArea usersTextArea = new JTextArea(5, 20);
    private final JTextArea devicesTextArea = new JTextArea(5, 20);

    @Override
    protected void setup() {
        super.setup();

        addRegistrationBehaviour();
        addListeners();
    }

    private void addListeners() {
        // recv registration
        addBehaviour(new Behaviour() {
            private static final long serialVersionUID = -2405879908457018780L;

            @Override
            public void action() {
                ACLMessage recv = receive(MessageTemplate.and(
                        MatchPerformative(INFORM),
                        MatchProtocol(REG_PROTOCOL)
                ));

                if (recv != null) {
                    String[] content = recv.getContent().split(":");
                    kb.add(content[0], content[1], Integer.getInteger(content[2]));
                    System.out.println("Added: "
                            + content[0] + ":" + content[1]
                            + " expires in: " + Integer.getInteger(content[2]));

                    updateTextAreas();
                }
            }

            @Override
            public boolean done() {
                return false;
            }
        });

        // recv query
        addBehaviour(new Behaviour() {
            private static final long serialVersionUID = 7470776982151308125L;

            @Override
            public void action() {
                ACLMessage recv = receive(MessageTemplate.and(
                        MatchPerformative(QUERY_REF),
                        MatchProtocol(QUERY_PROTOCOL)));

                if (recv != null) {
                    String[] content = recv.getContent().split(":");

                    if (kb.get(content[1]) != null) {
                        System.out.println("Receive query " + recv.getContent() + " [FOUND]");

                        StringBuilder sb = new StringBuilder();
                        for (String s : kb.get(content[1])) {
                            sb.append(s).append(":");
                        }

                        ACLMessage ans = recv.createReply();
                        ans.setContent(sb.toString());

                        send(ans);
                    } else {
                        System.out.println("Receive query " + recv.getContent() + " [FORWARDED]");

                        ACLMessage forward = new ACLMessage(QUERY_REF);
                        forward.setProtocol(QUERY_PROTOCOL);
                        forward.addReceiver(new AID(BUILDING_AGENT_NAME, AID.ISLOCALNAME));
                        forward.setContent(recv.getContent());

                        send(forward);
                    }
                }
            }

            @Override
            public boolean done() {
                return false;
            }
        });
    }

    private void addRegistrationBehaviour() {
        addBehaviour(new TickerBehaviour(this, 5000L) {
            private static final long serialVersionUID = -6489149178402316870L;

            @SuppressWarnings("Duplicates")
            @Override
            protected void onTick() {
                ACLMessage msg = new ACLMessage(INFORM);
                msg.setProtocol(REG_PROTOCOL);
                msg.addReceiver(new AID(BUILDING_AGENT_NAME, AID.ISLOCALNAME));
                msg.setContent("user:" + getLocalName() + ":6");

                send(msg);
            }
        });
    }

    private void updateTextAreas() {
        usersTextArea.removeAll();
        devicesTextArea.removeAll();

        if (kb.get("users") != null) {
            for (String s : kb.get("users")) {
                usersTextArea.append(s + "\n");
            }
        }

        if (kb.get("devices") != null) {
            for (String s : kb.get("devices")) {
                devicesTextArea.append(s + "\n");
            }
        }
    }

    @Override
    protected void generateWindowComponents(JPanel panel) {
        usersLabel.setLabelFor(usersTextArea);
        devicesLabel.setLabelFor(devicesTextArea);

        usersTextArea.setEditable(false);
        devicesTextArea.setEditable(false);

        panel.add(usersLabel);
        panel.add(usersTextArea);

        panel.add(devicesLabel);
        panel.add(devicesTextArea);
    }
}
