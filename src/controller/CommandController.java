package controller;

private Map<String, int> stateMatcher;

public class CommandController {

    public CommandController() {

        this.stateMatcher = new HashMap<String, int>;
        this.stateMatcher.put("helo", 1);
        this.stateMatcher.put("mailfrom", 2);
        this.stateMatcher.put("rcptto", 3);
        this.stateMatcher.put("data", 4);

    }

    public String unifyCommand(String command) {

        return command.toLowerCase().replaceAll("\\s+","");

    }

    public int commandToState(String command) {

        String unifiedCommand = unifyCommand(command);
        return this.stateMatcher.get(unifiedCommand);

    }

}
