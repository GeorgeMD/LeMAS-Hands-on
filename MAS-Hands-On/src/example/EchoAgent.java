package example;

import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

/**
 * Simple agent that gives a reply to every message sent to it. If the message has the ontology "TEST", a more detailed
 * reply is given.
 * 
 * @author Andrei Olaru
 */
public class EchoAgent extends Agent
{
	/**
	 * The serial UID.
	 */
	private static final long serialVersionUID = 8598341291247503496L;
	
	@Override
	protected void setup()
	{
		// always call first
		super.setup();
		
		addBehaviour(new Behaviour() {
			/**
			 * The serial UID.
			 */
			private static final long serialVersionUID = 6046710491872203283L;
			
			@Override
			public void action()
			{
				ACLMessage received = receive(MessageTemplate.MatchProtocol("TEST"));
				if(received != null)
				{
					ACLMessage reply = received.createReply();
					System.out.println("Received from " + received.getSender().getLocalName()
							+ " a message with language " + received.getLanguage() + " and a content "
							+ received.getContent().length() + "-characters long.");
					reply.setContent("Received a message with language " + received.getLanguage() + " and a content "
							+ received.getContent().length() + "-characters long.");
					send(reply);
				}
				else
				{
					received = receive(); // receive anything
					if(received != null)
					{
						ACLMessage reply = received.createReply();
						System.out.println("Received some message. Protocol was not TEST.");
						reply.setContent("Use protocol TEST for more details.");
						send(reply);
					}
					else
					{
						block();
					}
				}
			}
			
			@Override
			public boolean done()
			{
				return false; // never end
			}
		});
	}
}
