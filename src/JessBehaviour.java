import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jess.JessException;
import jess.Rete;

import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;

class JessBehaviour extends CyclicBehaviour {
    // maximum number of passes that a run of Jess can execute before giving control to the agent
    private static final int MAX_JESS_PASSES = 1;
    // the Jess engine
    private jess.Rete jess;

    public Rete getJess() {
        return jess;
    }

    JessBehaviour(Agent agent, String jessFile) {
        super(agent);
        // create a Jess engine
        jess = new jess.Rete();
        jess.addUserfunction(new JessSend(agent));
        // load the Jess file
        try {
            // open the Jess file
            FileReader fr = new FileReader(jessFile);
            // create a parser for the file
            jess.Jesp j = new jess.Jesp(fr, jess);
            // parse the input file into the engine
            try {
                jess.executeCommand("(deftemplate MyAgent (slot name))");
                jess.executeCommand(
                        "(deffacts MyAgent \"All facts about this agent\" (MyAgent (name " + myAgent.getName() + ")))");
                j.parse(false);
                //jess.eval(" (deffacts Me " +" (i-am " + agent.getName() + ") )");
            } catch (jess.JessException je) {
                je.printStackTrace();
            }
            fr.close();
        } catch (IOException ioe) {
            System.err.println("Error loading Jess file - engine is empty");
        }
    }

    public void action() {
        // to count the number of Jess passes
        int executedPasses = -1;
        // run jess
        try {
            // run a maximum number of steps
            executedPasses = jess.run(MAX_JESS_PASSES);
        } catch (JessException je) {
            je.printStackTrace();
        }
        // if the engine stopped, block this behaviour
        if (executedPasses < MAX_JESS_PASSES)
            block();
        // the behaviour shall be unblocked by a call to restart()
    }

    boolean addFact(String jessFact) {
        // assert the fact into the Jess engine
        try {
            jess.executeCommand(jessFact);
        } catch (JessException je) {
            return false;
        }
        // if blocked, wake up!
        if (!isRunnable()) restart();
        // message asserted
        return true;
    }

    boolean newMsg(ACLMessage msg) {
        //System.out.println("Adding Message.");
        String jf = ACL2JessString(msg); // use msg to assemble a Jess construct
        // "feed" Jess engine
        return addFact(jf);
    }

    public String ACL2JessString(ACLMessage msg) {
        String fact;

        if (msg == null) {
            return "";
        }

        // I create a string that asserts the template fact
        fact = "(assert (ACLMessage (communicative-act " +
                ACLMessage.getPerformative(msg.getPerformative());

        if (msg.getSender() != null) {
            fact = fact + ") (sender " + msg.getSender().getName();
        }

        Iterator i = msg.getAllReceiver();

        if (i.hasNext()) {
            fact = fact + ") (receiver ";

            while (i.hasNext()) {
                AID aid = (AID) i.next();
                fact = fact + aid.getName();
            }
        }

        if (!isEmpty(msg.getReplyWith())) {
            fact = fact + ") (reply-with " + msg.getReplyWith();
        }

        if (!isEmpty(msg.getInReplyTo())) {
            fact = fact + ") (in-reply-to " + msg.getInReplyTo();
        }

        //if (!isEmpty(msg.getEnvelope()))     fact=fact+") (envelope " + msg.getEnvelope();
        if (!isEmpty(msg.getConversationId())) {
            fact = fact + ") (conversation-id " + msg.getConversationId();
        }

        if (!isEmpty(msg.getProtocol())) {
            fact = fact + ") (protocol " + msg.getProtocol();
        }

        if (!isEmpty(msg.getLanguage())) {
            fact = fact + ") (language " + msg.getLanguage();
        }

        if (!isEmpty(msg.getOntology())) {
            fact = fact + ") (ontology " + msg.getOntology();
        }

        if (msg.getContent() != null) {
            fact = fact + ") (content " + quote(msg.getContent());
        }

        if (!isEmpty(msg.getEncoding())) {
            fact = fact + ") (encoding " + msg.getEncoding();
        }

        i = msg.getAllReplyTo();

        if (i.hasNext()) {
            fact = fact + ") (reply-to ";

            while (i.hasNext()) {
                AID aid = (AID) i.next();
                fact = fact + aid.getName();
            }
        }

        if (msg.getReplyByDate() != null) {
            fact = fact + ") (reply-by " + msg.getReplyByDate().getTime();
        }

        fact = fact + ")))";

        return fact;
    }

    private boolean isEmpty(String string) {
        return (string == null) || string.equals("");
    }

    private String quote(java.lang.String str) {
        //replace all chars " in \ "
        return "\"" + stringReplace(str, '"', "\\\"") + "\"";
    }

    private String stringReplace(String str, char oldChar, String s) {
        int len = str.length();
        int i = 0;
        int j = 0;
        int k = 0;
        char[] val = new char[len];
        str.getChars(0, len, val, 0); // put chars into val

        char[] buf = new char[len * s.length()];

        while (i < len) {
            if (val[i] == oldChar) {
                s.getChars(0, s.length(), buf, j);
                j += s.length();
            } else {
                buf[j] = val[i];
                j++;
            }

            i++;
        }

        return new String(buf, 0, j);
    }


}
