import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;

import java.util.Random;

public class TemperatureSensorAgent extends Agent {

    private int temperature;
    private ChangeTemperature changeTemperature;

    public TemperatureSensorAgent(int startingTemperature) {
        this.temperature = startingTemperature;
    }

    public TemperatureSensorAgent() {
        this.temperature = 25;
    }

    public int getTemperature() {
        return temperature;
    }

    public void setTemperature(int temperature) {
        this.temperature = temperature;
    }


    @Override
    protected void setup() {
        super.setup();
        System.out.println(this.getLocalName() + ": Starting TemperatureSensorAgent");
        changeTemperature = new ChangeTemperature(this, 250);
        this.addBehaviour(changeTemperature);
        this.addBehaviour(new SendTemperature(this, 2000));
        this.addBehaviour(new ReceiveMessage());
    }

    private class ChangeTemperature extends TickerBehaviour {

        public ChangeTemperature(Agent a, long period) {
            super(a, period);
        }

        @Override
        protected void onTick() {
            Random random = new Random();
            int tempchange = random.nextInt(3) - 1;
            temperature += tempchange;
            //System.out.println("Temperature is now "+temperature);


        }
    }

    private class ReceiveMessage extends CyclicBehaviour {

        @Override
        public void action() {
            ACLMessage msg = receive();
            if (msg != null) {
                if (msg.getPerformative() == ACLMessage.REQUEST) {
                    if (msg.getContent().equals("cooling")) {
                        System.out.println(myAgent.getLocalName() + ": Turning on cooling system.");
                        myAgent.removeBehaviour(changeTemperature);
                        myAgent.addBehaviour(new CoolingSystem(myAgent, 500));
                    } else if (msg.getContent().equals("heating")) {
                        System.out.println(myAgent.getLocalName() + ": Turning on heating system.");
                        myAgent.removeBehaviour(changeTemperature);
                        myAgent.addBehaviour(new HeatingSystem(myAgent, 500));
                    }


                }
            } else {
                block();
            }
        }


    }

    private class SendTemperature extends TickerBehaviour {

        public SendTemperature(Agent a, long period) {
            super(a, period);
        }

        @Override
        protected void onTick() {
            AID receiver = new AID();
            receiver.setLocalName("MonitorAgent");
            ACLMessage mensagem = new ACLMessage(ACLMessage.INFORM);
            mensagem.setContent("" + temperature);
            mensagem.addReceiver(receiver);
            myAgent.send(mensagem);
            System.out.println(myAgent.getLocalName() + ": Temperature is now " + temperature);


        }
    }


    private class HeatingSystem extends TickerBehaviour {


        public HeatingSystem(Agent a, long period) {
            super(a, period);
        }


        @Override
        protected void onTick() {
            if (temperature < 25) {
                temperature++;

            } else {
                System.out.println(myAgent.getLocalName() + ":turning off heating system.");
                myAgent.addBehaviour(changeTemperature);
                this.stop();
            }
        }
    }


    private class CoolingSystem extends TickerBehaviour {

        public CoolingSystem(Agent a, long period) {
            super(a, period);
        }

        @Override
        protected void onTick() {
            if (temperature > 25) {
                temperature--;

            } else {
                System.out.println(myAgent.getLocalName() + ":turning off cooling system.");
                myAgent.addBehaviour(changeTemperature);
                this.stop();
            }
        }
    }

}
