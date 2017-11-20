import jade.core.AID;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jess.*;

import java.util.Date;
import java.util.Objects;

public class JessSend implements jess.Userfunction {
    private Agent myAgent;

    public JessSend(Agent a) {
        myAgent = a;
    }

    // Function name to be used in Jess
    public String getName() {
        return ("send");
    }

    // Called when (send ...) is executed at Jess
    public Value call(ValueVector vv, Context context) throws JessException {
        // get function arguments
        Fact f = vv.get(1).factValue(context);

        //Create Message
        //int perf = ACLMessage.getInteger(vv.get(0).stringValue(context));
        ACLMessage msg = JessFact2ACL(context, f);

        // send the message
        myAgent.send(msg);

        return Funcall.TRUE;
    }

    public ACLMessage JessFact2ACL(Context context, jess.ValueVector vv)
            throws jess.JessException {
        //System.out.println("JessFact2ACL " + vv.toString());
        int perf = ACLMessage.getInteger(vv.get(0).stringValue(context));
        ACLMessage msg = new ACLMessage(perf);

        //System.out.println("******** Sender ********* " + vv.get(0).toString());

        /*
        for (int i = 0; i < vv.size() ; i++) {
            System.out.println("i="+i+": "+vv.get(i).toString());

        }
        */

        if (!Objects.equals(vv.get(1).stringValue(context), "nil")) {
            AID sender = new AID();
            sender.setName(vv.get(1).stringValue(context));
            msg.setSender(sender);
        }

        if (!Objects.equals(vv.get(2).toString(), "nil")) {
            ValueVector l = vv.get(2).listValue(context);

            for (int i = 0; i < l.size(); i++) {
                AID receiver = new AID();
                receiver.setName(l.get(i).stringValue(context));
                msg.addReceiver(receiver);
            }
        }

        if (!Objects.equals(vv.get(3).stringValue(context), "nil")) {
            msg.setReplyWith(vv.get(3).stringValue(context));
        }

        if (!Objects.equals(vv.get(4).stringValue(context), "nil")) {
            msg.setInReplyTo(vv.get(4).stringValue(context));
        }

        //if (vv.get(5).stringValue(context) != "nil")
        //  msg.setEnvelope(vv.get(5).stringValue(context));
        if (!Objects.equals(vv.get(6).stringValue(context), "nil")) {
            msg.setConversationId(vv.get(6).stringValue(context));
        }

        if (!Objects.equals(vv.get(7).stringValue(context), "nil")) {
            msg.setProtocol(vv.get(7).stringValue(context));
        }

        if (!Objects.equals(vv.get(8).stringValue(context), "nil")) {
            msg.setLanguage(vv.get(8).stringValue(context));
        }

        if (!Objects.equals(vv.get(9).stringValue(context), "nil")) {
            msg.setOntology(vv.get(9).stringValue(context));
        }

        if (!Objects.equals(vv.get(10).stringValue(context), "nil")) {
            //FIXME undo replace chars of JessBehaviour.java. Needs to be done better
            msg.setContent(unquote(vv.get(10).stringValue(context)));
        }

        if (!Objects.equals(vv.get(11).stringValue(context), "nil")) {
            msg.setEncoding(vv.get(11).stringValue(context));
        }

        //System.err.println("JessFact2ACL type is "+vv.get(15).type());
        if (!Objects.equals(vv.get(12).toString(), "nil")) {
            ValueVector l = vv.get(12).listValue(context);

            for (int i = 0; i < l.size(); i++) {
                AID replyto = new AID();
                replyto.setName(l.get(i).stringValue(context));
                msg.addReplyTo(replyto);
            }
        }

        if (!Objects.equals(vv.get(13).stringValue(context), "nil")) {
            try {
                msg.setReplyByDate(new Date(Long.parseLong(vv.get(13)
                        .stringValue(context))));
            } catch (Exception e) { /* do not care */
            }
        }

        return msg;
    }


    private String unquote(String str) {
        String t1 = str.trim();

        if (t1.startsWith("\"")) {
            t1 = t1.substring(1);
        }

        if (t1.endsWith("\"")) {
            t1 = t1.substring(0, t1.length() - 1);
        }

        int len = t1.length();
        int i = 0;
        int j = 0;
        int k = 0;
        char[] val = new char[len];
        t1.getChars(0, len, val, 0); // put chars into val

        char[] buf = new char[len];

        boolean maybe = false;

        while (i < len) {
            if (maybe) {
                if (val[i] == '\"') {
                    j--;
                }

                buf[j] = val[i];
                maybe = false;
                i++;
                j++;
            } else {
                if (val[i] == '\\') {
                    maybe = true;
                }

                buf[j] = val[i];
                i++;
                j++;
            }
        }

        return new String(buf, 0, j);
    }


} // end JessSend class