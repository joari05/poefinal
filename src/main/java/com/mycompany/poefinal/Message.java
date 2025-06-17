package com.mycompany.poefinal;

import java.util.ArrayList;
import javax.swing.JOptionPane;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Random;
import org.json.simple.JSONObject;

public class Message {
   private static int totalMessagesSent = 0;
    private static int messageCount = 0;
    private static List<String> sentMessages = new ArrayList<>();

    private String messageID;
    private String recipient;
    private String messageText;
    private String messageHash;

    public Message(String recipient, String messageText) {
        this.recipient = recipient;
        this.messageText = messageText;
        this.messageID = generateMessageID();
        this.messageHash = createMessageHash();
    }

    private String generateMessageID() {
        String id = String.format("%010d", new Random().nextInt(1000000000));
        return id;
    }

    public boolean checkMessageID() {
        return messageID.length() == 10;
    }

    public boolean checkRecipientCell() {
        return recipient.matches("^\\+27\\d{9}$");
    }

    public String validateMessageLength() {
        if (messageText.length() > 250) {
            return "Message exceeds 250 characters by " + (messageText.length() - 250) + ", please reduce size.";
        }
        return "Message ready to send.";
    }

    public String createMessageHash() {
        String[] words = messageText.split("\\s+");
        String firstWord = words.length > 0 ? words[0] : "";
        String lastWord = words.length > 1 ? words[words.length - 1] : "";
        return messageID.substring(0, 2) + ":" + messageCount + ":" + firstWord.toUpperCase() + lastWord.toUpperCase();
    }

    public String sentMessage(String option) {
        switch (option.toLowerCase()) {
            case "1":
                totalMessagesSent++;
                sentMessages.add(printMessages());
                return "Message successfully sent.";
            case "2":
                return "Press 0 to delete message.";
            case "3":
                storeMessage();
                return "Message successfully stored.";
            default:
                return "Invalid option.";
        }
    }

    public String printMessages() {
        return "Message ID: " + messageID + "\n" +
               "Message Hash: " + messageHash + "\n" +
               "Recipient: " + recipient + "\n" +
               "Message: " + messageText + "\n";
    }

    public int returnTotalMessages() {
        return totalMessagesSent;
    }

    public void storeMessage() {
        JSONObject json = new JSONObject();
        json.put("messageID", messageID);
        json.put("recipient", recipient);
        json.put("message", messageText);
        json.put("hash", messageHash);

        try (FileWriter file = new FileWriter("stored_messages.json", true)) {
            file.write(json.toJSONString() + "\n");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error saving message: " + e.getMessage());
        }
    }

    public static void resetCount() {
        totalMessagesSent = 0;
        messageCount = 0;
        sentMessages.clear();
    }

    public static void incrementMessageCount() {
        messageCount++;
    }

}
