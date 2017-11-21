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
import jade.core.behaviours.*;
import jade.lang.acl.ACLMessage;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.SearchConstraints;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.util.Logger;

public class YellowAgent extends Agent {

    private DFAgentDescription[] neighbours;
    private DFAgentDescription dfd;
    private ServiceDescription sd;
    public Agent superthis;

    private class WaitPingAndReplyBehaviour extends CyclicBehaviour {

		public WaitPingAndReplyBehaviour(Agent a) {
			super(a);
		}

		public void action() {
			ACLMessage  msg = myAgent.receive();

			if (msg != null) 
            {
                if (neighbours.length != 2)
                {
                    SearchConstraints sc = new SearchConstraints();
                    sc.setMaxResults(new Long(2));
                    try {
                        DFAgentDescription template = new DFAgentDescription();
                        ServiceDescription serv = new ServiceDescription();
                        serv.setType("YellowAgent");
                        template.addServices(serv);
                        neighbours = DFService.search(superthis, template, sc);
                    } catch (FIPAException fe) {
                        fe.printStackTrace();
                    }
                    for (int i = 0 ; i < neighbours.length ; ++i)
                        System.out.println("Here is : "+neighbours[i].getName());
                }

				ACLMessage reply = msg.createReply();
                reply.setPerformative(ACLMessage.REFUSE);
                String content = msg.getContent();

                if (content.indexOf("ping") != -1 && msg.getPerformative() == ACLMessage.REQUEST)
                {
                    reply.setContent("pong");
                    reply.setPerformative(ACLMessage.INFORM);
                }

				send(reply);
			}
			else
				block();
		}
	}

	protected void setup() {
		// Registration with the DF 
        superthis = this;
        neighbours = new DFAgentDescription[3];
		dfd = new DFAgentDescription();
		sd = new ServiceDescription();   
		sd.setType("YellowAgent"); 
		sd.setName(getName());
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
