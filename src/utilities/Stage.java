package utilities;

import java.nio.ByteBuffer;

public class Stage {

	private int stage;
	private String lastCommand;
	private ByteBuffer ret;
	private boolean returnFlag;
	
	public Stage() {
		stage=0;
		lastCommand="";
	}
	
	public int getStage() {
		return stage;
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
