package utilities;

public class MailHandler {
    private String sender;
    private String[] recipient;
    private String mailData;


    public MailHandler() {
        this.sender = "";
        this.mailData = "";
    }


    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String[] getRecipient() {
        return recipient;
    }

    public void setRecipient(String[] recipient) {
        this.recipient = recipient;
    }

    public String getMailData() {
        return mailData;
    }

    public void setMailData(String mailData) {
        this.mailData = mailData;
    }
}
