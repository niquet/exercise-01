package com.SMTP_Server;

public class SMTPApplication {

    public static void main(String[] args) {
        // TODO Auto-generated method stub

        // Load configuration file
        ConfigHandler serverConfig = new ConfigHandler("../config.properties");
        // Cast server port configuration
        int serverPort = (int) serverConfig.getProperty("SMTP_Server_Port");

        // Start up the server
        Server smtpServer = new Server(serverPort);
        smtpServer.run();

    }

}