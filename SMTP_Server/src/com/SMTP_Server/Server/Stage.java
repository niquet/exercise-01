package com.SMTP_Server.Server;

public class Stage {

	private int stage;
	private String lastCommand;
	
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
}
