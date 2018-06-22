package agents;

import base.BaseAgent;
import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import javax.swing.*;

import static jade.lang.acl.MessageTemplate.MatchPerformative;
import static jade.lang.acl.MessageTemplate.MatchProtocol;

import static jade.lang.acl.ACLMessage.INFORM;
import static jade.lang.acl.ACLMessage.QUERY_REF;
import static jade.lang.acl.ACLMessage.QUERY_IF;

import static agents.Constants.REG_PROTOCOL;
import static agents.Constants.QUERY_PROTOCOL;
import static agents.Constants.QUERY_ANSWER_PROTOCOL;

public class BuildingAgent extends BaseAgent {
    private static final long serialVersionUID = 2422724108688381944L;

    private final JTextArea kbTextArea = new JTextArea(5, 20);
    private final JLabel kbLabel = new JLabel("KB Values");

    @Override
    protected void setup() {
        super.setup();

        addListeners();
    }

    private void addListeners() {
        addBehaviour(new Behaviour() {
            private static final long serialVersionUID = -8997834156253864852L;

            @Override
            public void action() {
                ACLMessage recv = receive(MessageTemplate.and(
                        MatchPerformative(INFORM),
                        MatchProtocol(REG_PROTOCOL)
                ));

                if (recv != null) {
                    String[] content = recv.getContent().split(":");
                    kb.add(content[0], content[1], Integer.parseInt(content[2]));
                    System.out.println("Added: "
                            + content[0] + ":" + content[1]
                            + " expires in: " + Integer.parseInt(content[2]));

                    updateKBTextArea();
                }
            }

            @Override
            public boolean done() {
                return false;
            }
        });

        addBehaviour(new Behaviour() {
            private static final long serialVersionUID = -4393705673314763276L;

            @Override
            public void action() {
                ACLMessage recv = receive(MessageTemplate.and(
                        MatchPerformative(QUERY_REF),
                        MatchProtocol(QUERY_PROTOCOL)));

                if (recv != null) {
                    String[] content = recv.getContent().split(":");
                    String answer;

                    if (kb.get(content[1]) != null) {
                        System.out.println("Receive query " + recv.getContent() + " [OK]");

                        StringBuilder sb = new StringBuilder();
                        for (String s : kb.get(content[1])) {
                            sb.append(s).append(":");
                        }

                        answer = sb.toString();
                    } else {
                        System.out.println("Receive query " + recv.getContent() + " [NOT FOUND]");
                        answer = "No information";
                    }

                    ACLMessage ans = new ACLMessage(QUERY_IF);
                    ans.addReceiver(new AID(content[0], AID.ISLOCALNAME));
                    ans.setProtocol(QUERY_ANSWER_PROTOCOL);
                    ans.setContent(answer);

                    send(ans);
                }
            }

            @Override
            public boolean done() {
                return false;
            }
        });
    }

    @Override
    protected void generateWindowComponents(JPanel panel) {
        kbLabel.setLabelFor(kbTextArea);
        panel.add(kbLabel);
        panel.add(kbTextArea);
    }

    private void updateKBTextArea() {
        kbTextArea.removeAll();

        StringBuilder sb = new StringBuilder();
        for (String key : kb.getKeys()) {
            for (String value : kb.get(key)) {
                sb.append(key).append(":").append(value).append("\n");
            }
        }

        kbTextArea.setText(sb.toString());
    }
}
