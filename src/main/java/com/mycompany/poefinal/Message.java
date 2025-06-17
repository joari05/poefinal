package com.mycompany.poefinal;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import javax.swing.JOptionPane;
import java.io.*;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Message {
    private static int totalMessagesSent = 0;
    private static int messageCount = 0;

    // === ARRAYS REQUERIDOS ===
    public static List<Message> sentMessages = new ArrayList<>();
    public static List<Message> disregardedMessages = new ArrayList<>();
    public static List<Message> storedMessages = new ArrayList<>();
    public static List<String> messageHashes = new ArrayList<>();
    public static List<String> messageIDs = new ArrayList<>();

    private String messageID;
    private String recipient;
    private String messageText;
    private String messageHash;

    public Message(String recipient, String messageText) {
        this.recipient = recipient;
        this.messageText = messageText;
        this.messageID = generateMessageID();
        this.messageHash = createMessageHash();

        messageIDs.add(this.messageID);
        messageHashes.add(this.messageHash);
    }

    private String generateMessageID() {
        return String.format("%010d", new Random().nextInt(1_000_000_000));
    }

    public boolean checkMessageID() {
        return messageID.length() == 10;
    }

    public boolean checkRecipientCell() {
        return recipient != null && recipient.matches("^\\+27\\d{9}$");
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
            case "send":
                sentMessages.add(this);
                totalMessagesSent++;
                return "Message successfully sent.";
            case "store":
                storedMessages.add(this);
                storeMessage();
                return "Message successfully stored.";
            case "discard":
                disregardedMessages.add(this);
                return "Message discarded.";
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

    public static void loadStoredMessagesFromFile() {
        try (BufferedReader reader = new BufferedReader(new FileReader("stored_messages.json"))) {
            String line;
            JSONParser parser = new JSONParser();
            while ((line = reader.readLine()) != null) {
                JSONObject json = (JSONObject) parser.parse(line);
                Message msg = new Message(
                    (String) json.get("recipient"),
                    (String) json.get("message")
                );
                msg.messageID = (String) json.get("messageID");
                msg.messageHash = (String) json.get("hash");
                storedMessages.add(msg);
                messageIDs.add(msg.messageID);
                messageHashes.add(msg.messageHash);
            }
        } catch (IOException | ParseException e) {
            JOptionPane.showMessageDialog(null, "Error reading stored messages: " + e.getMessage());
        }
    }

    // === PART 3 FEATURES ===

    public static void displaySentMessages() {
        for (Message msg : sentMessages) {
            System.out.println("Sender: [User] ➡ Recipient: " + msg.recipient);
        }
    }

    public static void getLongestMessage() {
        Message longest = null;
        for (Message msg : sentMessages) {
            if (longest == null || msg.messageText.length() > longest.messageText.length()) {
                longest = msg;
            }
        }
        if (longest != null) {
            System.out.println("📏 Longest message: \n" + longest.printMessages());
        }
    }

    public static void findMessageByID(String searchID) {
        for (Message msg : sentMessages) {
            if (msg.messageID.equals(searchID)) {
                System.out.println("🔍 Message found:\n" + msg.printMessages());
                return;
            }
        }
        System.out.println("❌ Message with ID " + searchID + " not found.");
    }

    public static void findMessagesByRecipient(String recipientNumber) {
        for (Message msg : sentMessages) {
            if (msg.recipient.equals(recipientNumber)) {
                System.out.println("📨 Message to " + recipientNumber + ":\n" + msg.messageText);
            }
        }
    }

    public static void deleteMessageByHash(String hash) {
        for (int i = 0; i < sentMessages.size(); i++) {
            if (sentMessages.get(i).messageHash.equals(hash)) {
                sentMessages.remove(i);
                System.out.println("🗑️ Message with hash " + hash + " deleted.");
                return;
            }
        }
        System.out.println("❌ Message with hash " + hash + " not found.");
    }

    public static void displayMessageReport() {
        System.out.println("===== Sent Message Report =====");
        for (Message msg : sentMessages) {
            System.out.println(msg.printMessages());
        }
        System.out.println("Total messages sent: " + sentMessages.size());
    }

    public static void resetCount() {
        totalMessagesSent = 0;
        messageCount = 0;
        sentMessages.clear();
        disregardedMessages.clear();
        storedMessages.clear();
        messageHashes.clear();
        messageIDs.clear();
    }

    public static void incrementMessageCount() {
        messageCount++;
    }
}
