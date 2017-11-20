import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import jess.Filter;
import jess.Value;

import java.util.HashMap;
import java.util.Iterator;

public class MonitorAgent extends Agent {


    public JessBehaviour jb;

    @Override
    protected void setup() {
        super.setup();
        System.out.println("Starting MonitorAgent");
        JessBehaviour jessBeh = new JessBehaviour(this, "src/tempRules.clp");
        this.jb=jessBeh;
        this.addBehaviour(jessBeh);
        this.addBehaviour(new Receiver(this, jessBeh));
        this.addBehaviour(new Metrics(this, 1000));
    }


    class Receiver extends CyclicBehaviour {

        private JessBehaviour jessBeh;

        Receiver(Agent agent, JessBehaviour jessBeh) {
            super(agent);
            this.jessBeh = jessBeh;
        }


        @Override
        public void action() {
            ACLMessage msg = receive();
            if (msg != null) {
                // put into Jess engine
                if (jessBeh.newMsg(msg)) {
                    System.out.println("Message Added");

                } else {
                    System.out.println("Error");
                }

            } else
                block();

        }
    }

    private class Metrics extends TickerBehaviour {

        public Metrics(Agent a, long period) {
            super(a, period);
        }

        @Override
        protected void onTick() {
          //Iterator<HashMap> teste= jb.getJess().getClass(new Filter.ByClass(HashMap.class));

            System.out.println();


        }
    }


}


