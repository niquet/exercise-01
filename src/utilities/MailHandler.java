package utilities;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.file.*;
import java.nio.*;

public class MailHandler {
    private String sender;
    private String[] recipient;
    private String mailData;


    public MailHandler() {
        this.sender = "";
        this.mailData = "";
        this.recipient = null;
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
    // Input: String
    // Adds one Recipient to the recipient list
    // Must be called for every recipient

    public void addRecipient(String recp) {
        if (this.recipient == null) {
            String[] recpStart = new String[1];
            recpStart[0] = recp;
            this.recipient = recpStart;
            return;
        }

        String[] recpNew = new String[this.recipient.length+1];
        System.arraycopy(this.recipient,0,recpNew,0,this.recipient.length);
        recpNew[recipient.length] = recp;
        this.recipient = recpNew;
    }

    public void addData(String data) {
        this.mailData = this.mailData + data;
    }

    public void clearMailHandlerData() {
        this.sender = "";
        this.mailData = "";
        this.recipient = null;
    }


    public void store() throws IOException {
        Path path = Paths.get("").toAbsolutePath();
        path = Paths.get(path + "/mails");
        System.out.println(path);

        if (!Files.exists(path)) {
            Files.createDirectory(path);
        }

        //Writes Message from Sender for every recipient
        for (String recp : recipient) {
            Path writePath = Paths.get(path +  "/" + recp);
            if (!Files.exists(writePath)) {
                Files.createDirectory(writePath);
            }
            Path finalWritePath = Paths.get(writePath.toString() + "/" + sender + "_" + Math.round(Math.random()*10000) + ".txt");

            // Checks whether the mail specific id is already taken an assigns a new value
            while (true) {
                File f = new File(finalWritePath.toString());

                if(f.exists()) {
                    finalWritePath = Paths.get(writePath.toString() + "/" + sender + "_" + Math.round(Math.random()*10000) + ".txt");
                    continue;
                }

                break;
            }


            // Create the file and write to it
            Files.createFile(finalWritePath);
            RandomAccessFile file = new RandomAccessFile(finalWritePath.toString(), "rw");
            ByteBuffer buffi = ByteBuffer.allocate(mailData.length());
            buffi.put(mailData.getBytes());
            buffi.flip();
            FileChannel writer = file.getChannel();
            writer.write(buffi);
            writer.close();
        }
    }
}
