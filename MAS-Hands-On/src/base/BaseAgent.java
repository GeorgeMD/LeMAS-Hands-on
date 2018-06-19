package base;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.wrapper.ControllerException;

/**
 * Example agent class.
 * 
 * @author Andrei Olaru
 */
public class BaseAgent extends Agent
{
	/**
	 * The serial UID.
	 */
	private static final long	serialVersionUID	= -3967827884981805793L;
	
	/////////////// GUI
	/**
	 * The window displayed by the agent.
	 */
	transient protected JFrame	window;
	
	/**
	 * The text label where to write the message received.
	 */
	transient protected JLabel	receiveLabel;
	
	////////////// Mobility
	/**
	 * Indicates whether the agent is on its home container or not.
	 */
	protected boolean			home				= true;
	
	/**
	 * The name of the agent's home container.
	 */
	protected String			homeContainer;
	
	////////////// KnowledgeBase
	/**
	 * The knowledge base.
	 */
	protected KB				kb					= null;
	
	@Override
	protected void setup()
	{
		super.setup();
		
		// initializes KB
		kb = new KB(1000);
		
		// initializes home container
		try
		{
			homeContainer = getContainerController().getContainerName();
		} catch(ControllerException e1)
		{
			e1.printStackTrace();
		}
		
		initialize();
		
		// behavior: print any message that is received.
		addBehaviour(new Behaviour() {
			/**
			 * The serial UID.
			 */
			private static final long serialVersionUID = -7515874546564955378L;
			
			@Override
			public void action()
			{
				ACLMessage received = receive(); // receive any message
				if(received != null)
				{
					System.out.println("Received: " + received.getContent());
					if(receiveLabel != null) // print to window
						receiveLabel.setText("Received: " + received.getContent());
				}
				else
					block();
			}
			
			@Override
			public boolean done()
			{
				return false; // will run forever
			}
		});
	}
	
	/**
	 * Creates the agent's window and writes to the output if the agent is on the home container or not.
	 */
	protected void initialize()
	{
		// print container information
		String currentContainer = null;
		try
		{
			currentContainer = getContainerController().getContainerName();
		} catch(ControllerException e1)
		{
			e1.printStackTrace();
		}
		home = homeContainer.equals(currentContainer);
		System.out.println("Agent " + getLocalName() + " is " + (home ? "" : "not ") + "home. Current container is "
				+ currentContainer + "." + (home ? "" : " Home container is " + homeContainer + "."));
		
		// generate window
		window = new JFrame(getLocalName());
		JPanel panel = new JPanel();
		panel.setLayout(new FlowLayout());
		
		generateWindowComponents(panel);
		
		window.getContentPane().add(panel, BorderLayout.CENTER);
		window.setSize(500, 300);
		window.setLocation(300, 300);
		window.setVisible(true);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	/**
	 * Adds the necessary components to the panel in the window.
	 * 
	 * @param panel
	 *            - the {@link JPanel} to add components to.
	 */
	protected void generateWindowComponents(JPanel panel)
	{
		// received message field
		receiveLabel = new JLabel("Received: -");
		panel.add(receiveLabel);
	}
	
	@Override
	protected void beforeMove()
	{
		// destroy window, if any
		if(window != null)
		{
			window.dispose();
			window = null;
		}
		
		super.beforeMove();
	}
	
	@Override
	protected void afterMove()
	{
		super.afterMove();
		
		// get container information and create window
		initialize();
	}
	
	@Override
	public void doDelete()
	{
		// destroy window, if any
		if(window != null)
		{
			window.dispose();
			window = null;
		}
		super.doDelete();
	}
}
