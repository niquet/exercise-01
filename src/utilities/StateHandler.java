package utilities;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;

import controller.CommandController;
import utilities.MailHandler;

public class StateHandler {

	// Valid states
	// 0 - begin
	// 1 - HELO received, waiting
	// 2 - MAIL FROM received
	// 3 - RCPT TO received
	// 4 - DATA received
	// x - \r\n.\r\n recevied (probably going back to state 1)
	// x - HELP received (can be ignored here, only relevant for the CommandController
	// x - QUIT received (should only be used to kill a StateHandler object when used)
	private String state;
	private CommandController controller;
	private MailHandler mailHandler;

	private String lastCommand;
	private ByteBuffer reply;
	private boolean returnFlag;
	
	public StateHandler() {

		this.controller = new CommandController();
		this.mailHandler = new MailHandler();
		// IDLE,
		// CONNECTED,
		// SENDER_APPROVED,
		// RECIPIENTS_APPROVED,
		// RECEIVING_MESSAGE_DATA
		this.state = this.controller.getState();	// state is now IDLE
		lastCommand = "";

	}

	public String unifyCommand(String command) {

		return command.toLowerCase().replaceAll("\\s+","");

	}

	public String extractCommand(String data) {

		if(data.startsWith("before")) {
			return "before";
		}
		if(data.startsWith("helo")) {
			return "helo";
		}
		if(data.startsWith("mailfrom")) {
			return "mailfrom";
		}
		if(data.startsWith("rcptto")) {
			return "rcptto";
		}
		if(data.startsWith("data")) {
			return "data";
		}
		if(data.endsWith("newline.newline")) {
			return "newline.newline";
		}
		if(data.startsWith("helphelo")) {
			return "helphelo";
		}
		if(data.startsWith("helpmailfrom")) {
			return "helpmailfrom";
		}
		if(data.startsWith("helprcptto")) {
			return "helprcptto";
		}
		if(data.startsWith("helpdata")) {
			return "helpdata";
		}
		if(data.startsWith("helpquit")) {
			return "helprcptto";
		}
		if(data.startsWith("help")) {
			return "help";
		}
		if(data.startsWith("quit")) {
			return "quit";
		}

		return "command unknown";

	}

	public String stripData(String data, String command) {

		String strippedData = new String();

		switch(command) {
			// case "before":
			case "helo":
				strippedData = data.substring(0, data.indexOf(' '));
				break;
			case "mailfrom":
			case "rcptto":
				strippedData = data.substring(0, data.indexOf(' '));
				strippedData = strippedData.substring(0, data.indexOf(' '));
				break;
			// case "data":
		}

		return strippedData;

	}


	public String executeCommand(String data) throws IOException {

		String currentData = data;
		String unifiedData = unifyCommand(data);

		// Extract command from data
		String command = extractCommand(unifiedData);

		// Check if command is allowed at this state
		String[] allowedCommands = CommandController.State.valueOf(this.state).allowedCommands();
		Boolean commandAllowed = Arrays.asList(allowedCommands).contains(command);

		// Send reply if command not allowed
		if(!commandAllowed && this.state != "RECEIVING_MESSAGE_DATA") {
			return CommandController.Transition.TRANSITION_BAD_COMMAND_SEQUENCE.getReply();
		}

		// Begin processing / executing the commands
		String strippedData;
		String sender;
		String recipient;
		String replyString = "";

		// Check for kind of command
		switch(command) {
			// In case of HELO we establish the connection
			case "before":
				replyString = controller.makeTransition(command);
				this.state = controller.getState();
				break;
			case "helo":
				replyString = controller.makeTransition(command);
				this.state = controller.getState();
				break;
			case "mailfrom":
				strippedData = stripData(currentData, command);
				if (strippedData.contains(" ")) {
					sender = strippedData.substring(0, strippedData.indexOf(' '));
				} else {
					sender = strippedData;
				}
				replyString = controller.makeTransition(command);
				this.mailHandler.setSender(sender);
				this.state = controller.getState();
				break;
			case "rcptto":
				strippedData = stripData(currentData, command);
				if (strippedData.contains(" ")) {
					recipient = strippedData.substring(0, strippedData.indexOf(' '));
				} else {
					recipient = strippedData;
				}
				replyString = controller.makeTransition(command);
				this.mailHandler.addRecipient(recipient);
				this.state = controller.getState();
				break;
			case "data":
				this.mailHandler.addData(data);
				replyString = controller.makeTransition(command);
				this.state = controller.getState();
				break;
			case "helphelo":
				replyString = CommandController.Transition.TRANSITION_HELP_CONNECT.getReply();
				break;
			case "helpmailfrom":
				replyString = CommandController.Transition.TRANSITION_HELP_MAIL_FROM.getReply();
				break;
			case "helprcptto":
				replyString = CommandController.Transition.TRANSITION_HELP_RCPTTO.getReply();
				break;
			case "helpdata":
				replyString = CommandController.Transition.TRANSITION_HELP_DATA.getReply();
				break;
			case "helpquit":
				replyString = CommandController.Transition.TRANSITION_HELP_QUIT.getReply();
				break;
			case "help":
				replyString = CommandController.Transition.TRANSITION_HELP.getReply();
				break;
			case "quit":
				replyString = controller.makeTransition(command);
				this.state = controller.getState();
				break;
			case "newline.newline":
				if(!currentData.equals("newline.newline")) {
					String rawData = currentData.substring(0, currentData.length()-16);
					this.mailHandler.addData(rawData);
				}
				this.state = controller.getState();
				this.mailHandler.store();
				this.mailHandler.clearMailHandlerData();
				replyString = controller.makeTransition("newline.newline");
				break;
			default:
				if(this.state == "RECEIVING_MESSAGE_DATA") {
					break;
				}
				replyString = CommandController.Transition.TRANSITION_COMMAND_NOT_IMPLEMENTED.getReply();
				break;
		}

		return replyString;

	}

	public String getState() {
		return state;
	}
	
	public String getLastCommand() {
		return lastCommand;
	}

	public void SetLastCommand(String s) {
		lastCommand=s;
	}
	public void setByteBuffer(ByteBuffer input) {
		this.reply = input;
	}
	public ByteBuffer getByteBuffer() {
		return this.reply;
	}
	public void setReturnFlag(boolean val) {
		this.returnFlag=val;
	}
	public boolean getReturnFlag() {
		return this.returnFlag;
	}
}
