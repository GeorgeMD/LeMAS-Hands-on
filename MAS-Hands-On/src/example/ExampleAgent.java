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
public class ExampleAgent extends BaseAgent
{
	/**
	 * The serial UID.
	 */
	private static final long serialVersionUID = -3967827884981805793L;
	
	@Override
	protected void setup()
	{
		super.setup();
		
		kb.add("firstKey", "firstValue", 5); // 5 seconds
		kb.add("firstKey", "secondValue", 10); // 10 seconds
		kb.add("secondKey", "value", 15); // 15 seconds
	}
	
	@Override
	protected void generateWindowComponents(JPanel panel)
	{
		// message entry field
		final JTextField tf = new JTextField("message here", 20);
		panel.add(tf);
		
		// message send button
		JButton send = new JButton("Send");
		send.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e)
			{
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
			public void actionPerformed(ActionEvent e)
			{
				doMove(new ContainerID(tfc.getText(), null));
			}
		});
		panel.add(move);
		
		final JTextArea KBTextArea = new JTextArea(5, 20);
		panel.add(KBTextArea);
		
		JButton refreshKB = new JButton("RefreshKB");
		refreshKB.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e)
			{
				System.out.println("time: " + System.currentTimeMillis() + "\n" + getKBContent());
				KBTextArea.setText("time: " + System.currentTimeMillis() + "\n" + getKBContent());
			}
		});
		panel.add(refreshKB);
	}
	
	/**
	 * @return a representation of the content of the Knowledge Base. 
	 */
	protected String getKBContent()
	{
		String kbPrint = "";
		Collection<String> keySet = kb.getKeys();
		for(String key : keySet)
			for(String value : kb.get(key))
				kbPrint += key + " : " + value + "\n";
		return kbPrint;
	}
	
	@Override
	protected void afterMove()
	{
		super.afterMove();
		
		// if not home, return soon
		if(!home)
			addBehaviour(new WakerBehaviour(this, 5000) {
				
				/**
				 * The serial UID.
				 */
				private static final long serialVersionUID = -2727280062580975894L;
				
				@SuppressWarnings("synthetic-access")
				@Override
				public void onWake()
				{
					doMove(new ContainerID(homeContainer, null));
				}
			});
	}
}
