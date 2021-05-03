package controller;

import java.util.HashMap;
import java.util.Map;

public class CommandController {

    private Map<String, Integer> stateMatcher;

    public enum State {
        ERROR,
        IDLE,
        CONNECTED,
        SENDER_APPROVED,
        RECEPIENTS_APPROVED,
        RECEIVING_MESSAGE_DATA,
        MESSAGE_QUEUED
    };

    public CommandController() {

        this.stateMatcher = new HashMap<>();
        this.stateMatcher.put("helo", 1);
        this.stateMatcher.put("mailfrom", 2);
        this.stateMatcher.put("rcptto", 3);
        this.stateMatcher.put("data", 4);

    }

    public String unifyCommand(String command) {

        return command.toLowerCase().replaceAll("\s+","");

    }

    public Integer commandToState(String command) {

        String unifiedCommand = unifyCommand(command);
        return this.stateMatcher.get(unifiedCommand);

    }

}