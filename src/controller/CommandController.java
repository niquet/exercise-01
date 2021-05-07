package controller;

import java.util.HashMap;
import java.util.Map;

public class CommandController {

    // private Map<Integer, String> replyCodes;
    private static Map<State, Map<Transition, State>> fsm;
    private State state;

    public enum Transition {

        TRANSITION_CONNECT_SUCCESS,
        TRANSITION_CONNECT_FAILURE,
        TRANSITION_HELP_CONNECT,
        TRANSITION_HELO_SUCCESS,
        TRANSITION_MAIL_FROM_SUCCESS,
        TRANSITION_MAIL_FROM_ERROR,
        TRANSITION_MAIL_FROM_FAILURE,
        TRANSITION_HELP_MAIL_FROM,
        TRANSITION_RCPT_TO_SUCCESS,
        TRANSITION_RCPT_TO_ERROR,
        TRANSITION_RCPT_TO_FAILURE,
        TRANSITION_HELP_RCPTTO,
        TRANSITION_DATA_INTERMEDIATE,
        TRANSITION_DATA_SUCCESS,
        TRANSITION_DATA_ERROR,
        TRANSITION_DATA_FAILURE,
        TRANSITION_HELP_DATA,
        TRANSITION_QUEUE_MESSAGE_SUCCESS,
        TRANSITION_QUEUE_MESSAGE_ERROR,
        // TRANSITION_QUEUE_MESSAGE_FAILURE,
        TRANSITION_QUIT_SUCCESS,
        // TRANSITION_QUIT_ERROR,
        TRANSITION_HELP_QUIT,
        TRANSITION_HELP,
        TRANSITION_BAD_COMMAND_SEQUENCE,
        TRANSITION_COMMAND_NOT_IMPLEMENTED;

        public String getReply() {

            String reply = "";

            // this refers to the transition itself
            switch(this) {
                case TRANSITION_CONNECT_SUCCESS:
                    reply = "220 <domain> Service ready";
                    break;
                case TRANSITION_CONNECT_FAILURE:
                    reply = "421 <domain> Service not available,\n" +
                           "closing transmission channel";
                    break;
                case TRANSITION_HELP_CONNECT:
                    // HELP HELO
                    reply = "214 Help message\n\n" +
                            "HELO <SP> <domain> <CRLF>\n\n" +
                           "In the HELO command the host sending the command identifies\n" +
                           " itself; the command may be interpreted as saying \"Hello, I am\n" +
                           " <domain>\"";
                    break;
                case TRANSITION_HELO_SUCCESS:
                case TRANSITION_MAIL_FROM_SUCCESS:
                case TRANSITION_RCPT_TO_SUCCESS:
                case TRANSITION_DATA_SUCCESS:
                case TRANSITION_QUEUE_MESSAGE_SUCCESS:
                    // MAIL <SP> FROM:<reverse-path> <CRLF>
                    // RCPT <SP> TO:<forward-path> <CRLF>
                    // DATA <CRLF> ... <CRLF>.<CRLF>
                    reply = "250 OK";
                    break;
                // case TRANSITION_MAIL_FROM_ERROR:
                    // TODO
                // case TRANSITION_MAIL_FROM_FAILURE:
                    // TODO
                case TRANSITION_HELP_MAIL_FROM:
                    // HELP MAIL FROM
                    reply = "214 Help message\n\n" +
                            "MAIL <SP> FROM:<reverse-path> <CRLF>\n\n" +
                            "This command tells the SMTP-receiver that a new mail\n" +
                            "transaction is starting and to reset all its state tables and\n" +
                            "buffers, including any recipients or mail data. It gives the\n" +
                            "reverse-path which can be used to report errors. If accepted,\n" +
                            "the receiver-SMTP returns a 250 OK reply.\n" +
                            "The <reverse-path> can contain more than just a mailbox. The\n" +
                            "<reverse-path> is a reverse source routing list of hosts and\n" +
                            "source mailbox. The first host in the <reverse-path> should be\n" +
                            "the host sending this command.";
                    break;
                // case TRANSITION_RCPT_TO_ERROR:
                    // TODO
                case TRANSITION_RCPT_TO_FAILURE:
                    reply = "550 Requested action not taken: mailbox unavailable\n";
                    break;
                case TRANSITION_HELP_RCPTTO:
                    // HELP RCPT TO
                    reply = "214 Help message\n\n" +
                            "RCPT <SP> TO:<forward-path> <CRLF>\n\n" +
                            "This command gives a forward-path identifying one recipient.\n" +
                            "If accepted, the receiver-SMTP returns a 250 OK reply, and\n" +
                            "stores the forward-path. If the recipient is unknown the\n" +
                            "receiver-SMTP returns a 550 Failure reply. This second step of\n" +
                            "the procedure can be repeated any number of times.\n" +
                            "The <forward-path> can contain more than just a mailbox. The\n" +
                            "<forward-path> is a source routing list of hosts and the\n" +
                            "destination mailbox. The first host in the <forward-path>\n" +
                            "should be the host receiving this command.";
                    break;
                case TRANSITION_DATA_INTERMEDIATE:
                    reply = "354 Start mail input; end with <CRLF>.<CRLF>";
                    break;
                // case TRANSITION_DATA_ERROR:
                    // TODO
                // case TRANSITION_DATA_FAILURE:
                    // TODO
                case TRANSITION_HELP_DATA:
                    // HELP DATA
                    reply = "214 Help message\n\n" +
                            "DATA <CRLF>\n\n" +
                            "If accepted, the receiver-SMTP returns a 354 Intermediate reply\n" +
                            "and considers all succeeding lines to be the message text.\n" +
                            "When the end of text is received and stored the SMTP-receiver\n" +
                            "sends a 250 OK reply.\n" +
                            "Since the mail data is sent on the transmission channel the end\n" +
                            "of the mail data must be indicated so that the command and\n" +
                            "reply dialog can be resumed. SMTP indicates the end of the\n" +
                            "mail data by sending a line containing only a period.";
                    break;
                /*
                 case TRANSITION_QUEUE_MESSAGE_SUCCESS:
                 case TRANSITION_QUEUE_MESSAGE_ERROR:
                 case TRANSITION_QUEUE_MESSAGE_FAILURE:
                 case TRANSITION_HELP_QUEUE_MESSAGE:
                */
                case TRANSITION_QUIT_SUCCESS:
                    reply = "221 <domain> Service closing transmission channel";
                    break;
                // case TRANSITION_QUIT_ERROR:
                case TRANSITION_HELP_QUIT:
                    reply = "214 Help message\n\n" +
                           "QUIT <CRLF>\n\n" +
                           "This command specifies that the receiver must send an OK\n" +
                           "reply, and then close the transmission channel.\n" +
                           "The receiver should not close the transmission channel until\n" +
                           "it receives and replies to a QUIT command (even if there was\n" +
                           "an error). The sender should not close the transmission\n" +
                           "channel until it send a QUIT command and receives the reply\n" +
                           "(even if there was an error response to a previous command).\n" +
                           "If the connection is closed prematurely the receiver should\n" +
                           "act as if a RSET command had been received (canceling any\n" +
                           "pending transaction, but not undoing any previously\n" +
                           "completed transaction), the sender should act as if the\n" +
                           "command or transaction in progress had received a temporary\n" +
                           "error (4xx).";
                    break;
                case TRANSITION_HELP:
                    reply = "214 Help message\n\n" +
                            "HELO <SP> <domain> <CRLF>\n" +
                            "MAIL <SP> FROM:<reverse-path> <CRLF>\n" +
                            "RCPT <SP> TO:<forward-path> <CRLF>\n" +
                            "DATA <CRLF>\n" +
                            "HELP [<SP> <string>] <CRLF>\n" +
                            "QUIT <CRLF>";
                    break;
                case TRANSITION_COMMAND_NOT_IMPLEMENTED:
                    reply = "502 Command not implemented";
                    break;
                case TRANSITION_BAD_COMMAND_SEQUENCE:
                    reply = "503 Bad sequence of commands";
                    break;
            }

            return reply;

        }

    }

    public enum State {

        BEFORE,
        IDLE,
        CONNECTED,
        SENDER_APPROVED,
        RECIPIENTS_APPROVED,
        RECEIVING_MESSAGE_DATA,
        MESSAGE_QUEUED,
        FINISHED;

        public String[] allowedCommands() {

            String[] allowed = new String[10];

            switch(this) {
                case BEFORE:
                    allowed = new String[]{"before"};
                    break;
                case IDLE:
                    allowed = new String[]{"helo", "help", "helpmailfrom", "helprcptto", "helpdata", "helpquit"};
                    break;
                case CONNECTED:
                    allowed = new String[]{"mailfrom", "help", "helpmailfrom", "helprcptto", "helpdata", "helpquit", "quit"};
                    break;
                case SENDER_APPROVED:
                    allowed = new String[]{"rcptto", "help", "helpmailfrom", "helprcptto", "helpdata", "helpquit", "quit"};
                    break;
                case RECIPIENTS_APPROVED:
                    allowed = new String[]{"data", "rcptto", "help", "helpmailfrom", "helprcptto", "helpdata", "helpquit", "quit"};
                    break;
                case RECEIVING_MESSAGE_DATA:
                    allowed = new String[]{"newline.newline", "help", "helpmailfrom", "helprcptto", "helpdata", "helpquit", "quit"};
                    break;
                case MESSAGE_QUEUED:
                    allowed = new String[]{"mailfrom", "help", "helpmailfrom", "helprcptto", "helpdata", "helpquit", "quit"};
                    break;
            }

            return allowed;

        }

    }

    public CommandController() {

        // HELO <SP> <domain> <CRLF>
        // MAIL <SP> FROM:<reverse-path> <CRLF>
        // RCPT <SP> TO:<forward-path> <CRLF>
        // DATA <CRLF>
        // RSET <CRLF>
        // SEND <SP> FROM:<reverse-path> <CRLF>
        // SOML <SP> FROM:<reverse-path> <CRLF>
        // SAML <SP> FROM:<reverse-path> <CRLF>
        // VRFY <SP> <string> <CRLF>
        // EXPN <SP> <string> <CRLF>
        // HELP [<SP> <string>] <CRLF>
        // NOOP <CRLF>
        // QUIT <CRLF>
        // TURN <CRLF>

        this.state = State.BEFORE;

        fsm = new HashMap<>();

        Map<Transition, State> before = new HashMap<>();
        before.put(Transition.TRANSITION_CONNECT_SUCCESS, State.IDLE);
        before.put(Transition.TRANSITION_CONNECT_FAILURE, State.BEFORE);
        before.put(Transition.TRANSITION_QUIT_SUCCESS, State.FINISHED);

        Map<Transition, State> idle = new HashMap<>();
        idle.put(Transition.TRANSITION_HELO_SUCCESS, State.CONNECTED);
        idle.put(Transition.TRANSITION_QUIT_SUCCESS, State.FINISHED);

        Map<Transition, State> connected = new HashMap<>();
        connected.put(Transition.TRANSITION_MAIL_FROM_SUCCESS, State.SENDER_APPROVED);
        connected.put(Transition.TRANSITION_MAIL_FROM_ERROR, State.CONNECTED);
        connected.put(Transition.TRANSITION_MAIL_FROM_FAILURE, State.CONNECTED);
        connected.put(Transition.TRANSITION_QUIT_SUCCESS, State.FINISHED);

        Map<Transition, State> senderApproved = new HashMap<>();
        senderApproved.put(Transition.TRANSITION_RCPT_TO_SUCCESS, State.RECIPIENTS_APPROVED);
        senderApproved.put(Transition.TRANSITION_RCPT_TO_ERROR, State.SENDER_APPROVED);
        senderApproved.put(Transition.TRANSITION_RCPT_TO_FAILURE, State.SENDER_APPROVED);
        senderApproved.put(Transition.TRANSITION_QUIT_SUCCESS, State.FINISHED);

        Map<Transition, State> recipientsApproved = new HashMap<>();
        recipientsApproved.put(Transition.TRANSITION_RCPT_TO_SUCCESS, State.RECIPIENTS_APPROVED);
        recipientsApproved.put(Transition.TRANSITION_DATA_INTERMEDIATE, State.RECEIVING_MESSAGE_DATA);
        recipientsApproved.put(Transition.TRANSITION_QUIT_SUCCESS, State.FINISHED);

        Map<Transition, State> receivingMessageData = new HashMap<>();
        receivingMessageData.put(Transition.TRANSITION_DATA_SUCCESS, State.MESSAGE_QUEUED);
        receivingMessageData.put(Transition.TRANSITION_DATA_INTERMEDIATE, State.RECEIVING_MESSAGE_DATA);
        receivingMessageData.put(Transition.TRANSITION_DATA_ERROR, State.RECEIVING_MESSAGE_DATA);
        receivingMessageData.put(Transition.TRANSITION_DATA_FAILURE, State.RECEIVING_MESSAGE_DATA);
        receivingMessageData.put(Transition.TRANSITION_QUIT_SUCCESS, State.FINISHED);

        Map<Transition, State> messageQueued = new HashMap<>();
        messageQueued.put(Transition.TRANSITION_MAIL_FROM_SUCCESS, State.SENDER_APPROVED);
        messageQueued.put(Transition.TRANSITION_MAIL_FROM_ERROR, State.MESSAGE_QUEUED);
        messageQueued.put(Transition.TRANSITION_QUIT_SUCCESS, State.FINISHED);

        fsm.put(State.BEFORE, before);
        fsm.put(State.IDLE, idle);
        fsm.put(State.CONNECTED, connected);
        fsm.put(State.SENDER_APPROVED, senderApproved);
        fsm.put(State.RECIPIENTS_APPROVED, recipientsApproved);
        fsm.put(State.RECEIVING_MESSAGE_DATA, receivingMessageData);
        fsm.put(State.MESSAGE_QUEUED, messageQueued);

    }

    public String getState() {

        return this.state.toString();

    }

    public String makeTransition(String command) {

        Transition currentTransition;
        String reply = "";

        switch (this.state) {
            case BEFORE:
                currentTransition = Transition.TRANSITION_CONNECT_SUCCESS;
                reply = currentTransition.getReply();
                this.state = fsm.get(this.state).get(currentTransition);
                break;
            case IDLE:
                if (command.equals("helo")) {

                    currentTransition = Transition.TRANSITION_HELO_SUCCESS;
                    reply = currentTransition.getReply();
                    this.state = fsm.get(this.state).get(currentTransition);
                    break;

                } else if (command.equals("quit")) {
                    currentTransition = Transition.TRANSITION_QUIT_SUCCESS;
                    reply = currentTransition.getReply();
                    this.state = fsm.get(this.state).get(currentTransition);
                    break;
                } else {

                    currentTransition = Transition.TRANSITION_CONNECT_FAILURE;
                    reply = currentTransition.getReply();
                    this.state = fsm.get(this.state).get(currentTransition);
                    break;

                }
            case CONNECTED:
                if (command.equals("mailfrom")) {

                    currentTransition = Transition.TRANSITION_MAIL_FROM_SUCCESS;
                    reply = currentTransition.getReply();
                    this.state = fsm.get(this.state).get(currentTransition);
                    break;

                } else if (command.equals("quit")) {
                    currentTransition = Transition.TRANSITION_QUIT_SUCCESS;
                    reply = currentTransition.getReply();
                    this.state = fsm.get(this.state).get(currentTransition);
                    break;
                } else {

                    currentTransition = Transition.TRANSITION_MAIL_FROM_ERROR;
                    reply = currentTransition.getReply();
                    this.state = fsm.get(this.state).get(currentTransition);
                    break;

                }
            case SENDER_APPROVED:
                if (command.equals("rcptto")) {

                    currentTransition = Transition.TRANSITION_RCPT_TO_SUCCESS;
                    reply = currentTransition.getReply();
                    this.state = fsm.get(this.state).get(currentTransition);
                    break;

                } else if (command.equals("quit")) {
                    currentTransition = Transition.TRANSITION_QUIT_SUCCESS;
                    reply = currentTransition.getReply();
                    this.state = fsm.get(this.state).get(currentTransition);
                    break;

                } else {

                    currentTransition = Transition.TRANSITION_RCPT_TO_FAILURE;
                    reply = currentTransition.getReply();
                    this.state = fsm.get(this.state).get(currentTransition);
                    break;

                }
            case RECIPIENTS_APPROVED:
                if (command.equals("data")) {

                    currentTransition = Transition.TRANSITION_DATA_INTERMEDIATE;
                    reply = currentTransition.getReply();
                    this.state = fsm.get(this.state).get(currentTransition);
                    break;

                } else if (command.equals("rcptto")) {

                    currentTransition = Transition.TRANSITION_RCPT_TO_SUCCESS;
                    reply = currentTransition.getReply();
                    this.state = fsm.get(this.state).get(currentTransition);
                    break;

                } else if (command.equals("quit")) {
                    currentTransition = Transition.TRANSITION_QUIT_SUCCESS;
                    reply = currentTransition.getReply();
                    this.state = fsm.get(this.state).get(currentTransition);
                    break;

                } else {

                    currentTransition = Transition.TRANSITION_RCPT_TO_FAILURE;
                    reply = currentTransition.getReply();
                    this.state = fsm.get(this.state).get(currentTransition);
                    break;

                }
            case RECEIVING_MESSAGE_DATA:
                if (command.equals("newline.newline")) {

                    currentTransition = Transition.TRANSITION_DATA_SUCCESS;
                    reply = currentTransition.getReply();
                    this.state = fsm.get(this.state).get(currentTransition);
                    break;

                } else if (command.equals("quit")) {
                    currentTransition = Transition.TRANSITION_QUIT_SUCCESS;
                    reply = currentTransition.getReply();
                    this.state = fsm.get(this.state).get(currentTransition);
                    break;

                }
            case MESSAGE_QUEUED:
                if(command.equals("quit")){
                    currentTransition =Transition.TRANSITION_QUIT_SUCCESS;
                    reply = currentTransition.getReply();
                    this.state = fsm.get(this.state).get(currentTransition);
                    break;
                }

        }

        return reply;

    }

}