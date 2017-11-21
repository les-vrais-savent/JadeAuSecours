/*****************************************************************
JADE - Java Agent DEvelopment Framework is a framework to develop 
multi-agent systems in compliance with the FIPA specifications.
Copyright (C) 2000 CSELT S.p.A. 

GNU Lesser General Public License

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation, 
version 2.1 of the License. 

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the
Free Software Foundation, Inc., 59 Temple Place - Suite 330,
Boston, MA  02111-1307, USA.
 *****************************************************************/

import jade.core.*;
import java.util.Random;
import jade.core.behaviours.*;
import jade.lang.acl.ACLMessage;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.util.Logger;

public class BombAgent extends Agent {

    private String agentsName[] = {"bomb1", "bomb2", "bomb3"};

	private class WaitPingAndReplyBehaviour extends CyclicBehaviour {

		public WaitPingAndReplyBehaviour(Agent a) {
			super(a);
		}

		public void action() {
			ACLMessage msg = myAgent.receive();
			if(msg != null) 
            {
				ACLMessage new_msg = new ACLMessage(ACLMessage.INFORM);
                int bombNumber = Integer.parseInt(msg.getContent());

                if (bombNumber > 0)
                {
                    new_msg.setContent(String.valueOf(bombNumber-1));
                    Random rand = new Random();
                    int randomAgent = 0;
                    do
                    {
                        randomAgent = rand.nextInt(3);
                    } while(getLocalName() == agentsName[randomAgent]);
                    new_msg.addReceiver(new AID(agentsName[randomAgent], AID.ISLOCALNAME));
                    System.out.println(getLocalName() + " " + agentsName[randomAgent]);
				    send(new_msg);
                }
                else
                    doDelete();
			}
			else
				block();
		}
	}


	protected void setup() {
		// Registration with the DF 
		DFAgentDescription dfd = new DFAgentDescription();
		ServiceDescription sd = new ServiceDescription();   
		sd.setType("BombAgent"); 
		sd.setName(getName());
		sd.setOwnership("TILAB");
		dfd.setName(getAID());
		dfd.addServices(sd);
		try
        {
			DFService.register(this,dfd);
			WaitPingAndReplyBehaviour PingBehaviour = new WaitPingAndReplyBehaviour(this);
			addBehaviour(PingBehaviour);
		} 
        catch (FIPAException e)
        {
			doDelete();
		}
	}
}
