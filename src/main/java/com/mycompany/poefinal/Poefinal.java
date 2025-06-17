package com.mycompany.poefinal;

import java.util.Scanner;
import javax.swing.JOptionPane;

public class Poefinal {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        String firstName, lastName, username, password, phone;

        // === WELCOME HEADER ===
        System.out.println("======================================");
        System.out.println("       WELCOME TO QUICKCHAT 💬        ");
        System.out.println("======================================");

        // === USER REGISTRATION ===
        System.out.print("Enter your first name: ");
        firstName = scanner.nextLine();

        System.out.print("Enter your last name: ");
        lastName = scanner.nextLine();

        Login user = new Login(firstName, lastName);

        System.out.println("\n=== Registration ===");
        System.out.print("Choose a username: ");
        username = scanner.nextLine();

        System.out.print("Choose a password: ");
        password = scanner.nextLine();

        System.out.print("Enter your phone number (with country code): ");
        phone = scanner.nextLine();

        user.registerUser(username, password, phone);
        System.out.println("✅ Registration complete!");

        // === LOGIN ===
        System.out.println("\n=== Login ===");
        System.out.print("Enter your username: ");
        String enteredUsername = scanner.nextLine();

        System.out.print("Enter your password: ");
        String enteredPassword = scanner.nextLine();

        if (!user.loginUser(enteredUsername, enteredPassword)) {
            System.out.println("❌ Login failed. Please restart and try again.");
            return;
        }

        System.out.println("\n✅ Login successful!");
        System.out.println("Welcome to QuickChat, " + firstName + "!");

        // === Load Stored Messages ===
        Message.loadStoredMessagesFromFile();

        // === Message Limit ===
        System.out.print("\nHow many messages would you like to send? ");
        int messageLimit = Integer.parseInt(scanner.nextLine());
        int messagesSent = 0;

        Message.resetCount();

        while (true) {
            System.out.println("\n===== Main Menu =====");
            System.out.println("1) Send/Store/Discard a Message");
            System.out.println("2) Show Sent Messages");
            System.out.println("3) Show Longest Sent Message");
            System.out.println("4) Search Message by ID");
            System.out.println("5) Search Messages by Recipient");
            System.out.println("6) Delete Message by Hash");
            System.out.println("7) Display Full Sent Message Report");
            System.out.println("8) Quit");
            System.out.print("Choose an option: ");
            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    if (messagesSent >= messageLimit) {
                        System.out.println("⚠️ You have reached your message limit.");
                        continue;
                    }

                    System.out.print("Enter recipient number (format +27XXXXXXXXX): ");
                    String recipient = scanner.nextLine();

                    System.out.print("Enter your message: ");
                    String text = scanner.nextLine();

                    Message.incrementMessageCount();
                    Message message = new Message(recipient, text);

                    if (!message.checkRecipientCell()) {
                        System.out.println("❌ Invalid phone number. Must start with +27 and have 9 digits.");
                        continue;
                    }

                    String validation = message.validateMessageLength();
                    System.out.println(validation);
                    if (!validation.contains("ready")) continue;

                    System.out.print("Do you want to Send / Store / Discard this message? ");
                    String action = scanner.nextLine().toLowerCase();

                    String result = message.sentMessage(action);
                    JOptionPane.showMessageDialog(null, message.printMessages(), "Message Details", JOptionPane.INFORMATION_MESSAGE);
                    System.out.println("📨 " + result);

                    if (result.contains("sent")) messagesSent++;
                    break;

                case "2":
                    System.out.println("📬 Sent Messages:");
                    Message.displaySentMessages();
                    break;

                case "3":
                    Message.getLongestMessage();
                    break;

                case "4":
                    System.out.print("🔍 Enter Message ID to search: ");
                    String id = scanner.nextLine();
                    Message.findMessageByID(id);
                    break;

                case "5":
                    System.out.print("📤 Enter recipient number to search: ");
                    String rec = scanner.nextLine();
                    Message.findMessagesByRecipient(rec);
                    break;

                case "6":
                    System.out.print("🗑️ Enter message hash to delete: ");
                    String hash = scanner.nextLine();
                    Message.deleteMessageByHash(hash);
                    break;

                case "7":
                    Message.displayMessageReport();
                    break;

                case "8":
                    System.out.println("👋 Exiting QuickChat. Total messages sent: " + messagesSent);
                    return;

                default:
                    System.out.println("❓ Invalid option. Try again.");
            }
        }
    }
}
