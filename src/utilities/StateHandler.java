package utilities;

import java.nio.ByteBuffer;

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
	private String lastCommand;
	private ByteBuffer ret;
	private boolean returnFlag;
	
	public StateHandler() {
		this.state = "";
		lastCommand = "";
	}

	public String getState() {
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
	public ByteBuffer getByteBuffer() {
		return this.ret;
	}
	public void setReturnFlag(boolean val) {
		this.returnFlag=val;
	}
	public boolean getReturnFlag() {
		return this.returnFlag;
	}
}
