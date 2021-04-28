package utilities;

import java.nio.ByteBuffer;

public class StateHandler {

	// Valid states
	// 0 - waiting for HELO
	// 1 - HELO received
	// 2 - MAIL FROM received
	// 3 - RCPT TO received
	// 4 - DATA received
	// x - \r\n.\r\n recevied (probably going back to state 1)
	// x - HELP received (can be ignored here, only relevant for the CommandController
	// x - QUIT received (should only be used to kill a StateHandler object when used)
	private int state;
	private String lastCommand;
	private ByteBuffer ret;
	private boolean returnFlag;
	
	public StateHandler() {
		this.state = 0;
		lastCommand = "";
	}
	
	public int getState() {
		return state;
	}
	
	public String getLastCommand() {
		return lastCommand;
	}
	
	public void advance() {
		
	}
	public void SetLastCommand(String s) {
		lastCommand=s;
	}
	public void setByteBuffer(ByteBuffer input) {
		this.ret = input;
	}
}
