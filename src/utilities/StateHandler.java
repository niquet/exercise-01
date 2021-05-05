package utilities;

import java.io.IOException;
import java.nio.ByteBuffer;
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


	public String executeCommand(String data) throws IOException {

		String currentData = data;

		// Extract command from data
		String commandBeginning = unifyCommand(data.substring(0, data.indexOf(' ')));
		String content = data.substring(data.indexOf(' ') + 1);
		String command, sender, recipient, replyString = null;

		// Check for kind of command
		switch(commandBeginning) {
			// In case of HELO we establish the connection
			case "helo":
				replyString = controller.makeTransition(commandBeginning);
				this.state = controller.getState();
				break;
			case "mail":
				command = unifyCommand(commandBeginning.concat(content.substring(0, content.indexOf(' '))));
				content = data.substring(content.indexOf(' ') + 1);
				sender = content.substring(0, content.indexOf(' '));
				replyString = controller.makeTransition(command);
				mailHandler.setSender(sender);
				this.state = controller.getState();
				break;
			case "rcpt":
				command = unifyCommand(commandBeginning.concat(content.substring(0, content.indexOf(' '))));
				content = data.substring(content.indexOf(' ') + 1);
				recipient = content.substring(0, content.indexOf(' '));
				replyString = controller.makeTransition(command);
				mailHandler.addRecipient(recipient);
				this.state = controller.getState();
				break;
			case "data":
				mailHandler.addData(data);
				replyString = controller.makeTransition(commandBeginning);
				this.state = controller.getState();
				break;
			case "help":
				replyString = controller.makeTransition(unifyCommand(currentData));
				break;
			case "quit":
				replyString = controller.makeTransition(commandBeginning);
				break;
				
				/*
				if(this.state.equals("MESSAGE_QUEUED")) {
					replyString = controller.makeTransition(commandBeginning);
					break;
				} else {
					// TODO
				}
				*/
				
			default:
				if(currentData.contains("\r\n.\r\n") && this.state == "RECEIVING_MESSAGE_DATA") {
					String rawData = currentData.substring(0, currentData.indexOf("\r\n.\r\n"));
					mailHandler.addData(rawData);
					replyString = controller.makeTransition("<CR>.<CR>");
					this.state = controller.getState();
					mailHandler.store();
					mailHandler.clearMailHandlerData();
					break;
				}
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
