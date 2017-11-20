


(deftemplate ACLMessage (slot communicative-act) (slot sender) (multislot receiver)
              (slot reply-with) (slot in-reply-to) (slot envelope)
              (slot conversation-id) (slot protocol)
              (slot language) (slot ontology) (slot content)
              (slot encoding) (multislot reply-to) (slot reply-by))

;(defrule incomming-msg
;    (ACLMessage (sender ?s) (receiver ?r) (content ?c) (communicative-act ?ca))
;    =>
;    (printout t "Just received a message from " ?s " to " ?r  " with content " ?c " " ?ca crlf))

(defrule igiveup
?me <- (i-am ?agent)
=>
 (printout ?me ?agent))


(defrule cooling
 ?m <- (ACLMessage (communicative-act INFORM) (sender ?s) (content ?c) (receiver ?r) {content > 30})
 (MyAgent (name ?n))
 =>
 (assert (ACLMessage (communicative-act REQUEST) (sender ?n) (receiver ?s) (content cooling) ))
 (if (call ?cooling containsKey ?s) then (printout t "Incrementing C " ?s crlf) (call ?cooling put ?s (+ 1 (call ?cooling get ?s))) else (call ?cooling put ?s 1) (printout t "Added C " ?s crlf) )

 (retract ?m)
)

(defrule heating
 ?m <- (ACLMessage (communicative-act INFORM) (sender ?s) (content ?c) (receiver ?r) {content < 20})
 (MyAgent (name ?n))
 =>
; (send (assert (ACLMessage (communicative-act PROPOSE) (receiver ?s) (content ?c) )))
 ;(printout t "Just received a message from " ?s " to " ?r  " with content " ?c crlf)
 ;(printout t "myagent" ?n crlf)
 (assert (ACLMessage (communicative-act REQUEST) (sender ?n) (receiver ?s) (content heating) ))
  (if (call ?heating containsKey ?s) then (printout t "Incrementing H " ?s crlf) (call ?heating put ?s (+ 1 (call ?heating get ?s))) else (call ?heating put ?s 1) (printout t "Added H " ?s crlf) )

 (retract ?m)
)

(defrule send-a-message
    (MyAgent (name ?n))
    ?m <-(ACLMessage(sender ?n) (receiver ?r) (content ?c) (communicative-act ?ca))
    =>
    (printout t "Time to send a message!" crlf)
     ;(printout t "Sender " ?n crlf)
      ;(printout t "Receiver " ?r crlf)
       ;(printout t "Content " ?c crlf)
        ;(printout t "Performative " ?ca crlf)

    (send ?m) (retract ?m) )



(watch facts)
;(watch all)


(reset)

(bind ?heating (new java.util.HashMap)) <Java-Object:java.util.HashMap>
(bind ?cooling (new java.util.HashMap)) <Java-Object:java.util.HashMap>

