package example;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import base.BaseAgent;
import jade.core.AID;
import jade.core.ContainerID;
import jade.core.behaviours.WakerBehaviour;
import jade.lang.acl.ACLMessage;

/**
 * Example agent class.
 * 
 * @author Andrei Olaru
 */
public class ExampleAgent extends BaseAgent {
	/**
	 * The serial UID.
	 */
	private static final long serialVersionUID = -3967827884981805793L;

	/**
	 * Text area for displaying the KB.
	 */
	final JTextArea KBTextArea = new JTextArea(5, 20);

	@Override
	protected void setup() {
		super.setup();

		kb.add("firstKey", "firstValue", 5); // 5 seconds
		kb.add("firstKey", "secondValue", 10); // 10 seconds
		kb.add("secondKey", "value", 15); // 15 seconds
	}

	@Override
	protected void generateWindowComponents(JPanel panel) {
		// message entry field
		final JTextField tf = new JTextField("message here", 20);
		panel.add(tf);

		// message send button
		JButton send = new JButton("Send");
		send.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ACLMessage message = new ACLMessage(ACLMessage.INFORM);
				message.addReceiver(new AID("echo-AO", AID.ISLOCALNAME));
				message.setLanguage("English");
				message.setProtocol("TEST");
				message.setContent(tf.getText());
				send(message);
			}
		});
		panel.add(send);

		// add received label
		super.generateWindowComponents(panel);

		// container entry field
		final JTextField tfc = new JTextField("container name here", 20);
		panel.add(tfc);

		// move button
		JButton move = new JButton("Move");
		move.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				doMove(new ContainerID(tfc.getText(), null));
			}
		});
		panel.add(move);

		// KB entry field
		final JTextField kbEntry = new JTextField("key:value", 20);
		panel.add(kbEntry);

		// move button
		JButton kbAdd = new JButton("Add to KB for 10 seconds");
		kbAdd.addActionListener(new ActionListener() {
			@SuppressWarnings("synthetic-access")
			@Override
			public void actionPerformed(ActionEvent e) {
				String[] keyVal = kbEntry.getText().split(":");
				kb.add(keyVal[0].trim(), keyVal[1].trim(), 10);
				refreshKBTextArea();
			}
		});
		panel.add(kbAdd);

		// KB display field
		panel.add(KBTextArea);

		JButton refreshKB = new JButton("RefreshKB");
		refreshKB.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				refreshKBTextArea();
			}
		});
		panel.add(refreshKB);
	}

	/**
	 * Refreshes then display of the text area and also prints the KB to the console.
	 */
	protected void refreshKBTextArea() {
		System.out.println("time: " + System.currentTimeMillis() + "\n" + getKBContent());
		KBTextArea.setText("time: " + System.currentTimeMillis() + "\n" + getKBContent());
	}

	/**
	 * @return a representation of the content of the Knowledge Base.
	 */
	protected String getKBContent() {
		String kbPrint = "";
		Collection<String> keySet = kb.getKeys();
		for (String key : keySet)
			for (String value : kb.get(key))
				kbPrint += key + " : " + value + "\n";
		return kbPrint;
	}

	@Override
	protected void afterMove() {
		super.afterMove();

		// if not home, return soon
		if (!home)
			addBehaviour(new WakerBehaviour(this, 5000) {

				/**
				 * The serial UID.
				 */
				private static final long serialVersionUID = -2727280062580975894L;

				@SuppressWarnings("synthetic-access")
				@Override
				public void onWake() {
					doMove(new ContainerID(homeContainer, null));
				}
			});
	}
}
