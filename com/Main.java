package com;

import com.exception.BankException;
import com.model.*;
import com.service.Bank;

import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    private static final Scanner scanner = new Scanner(System.in);
    private static final Bank bank = new Bank("data");

    public static void main(String[] args) {
        System.out.println("=================================================");
        System.out.println("   🏛️  BANKING & FRAUD DETECTION SYSTEM (v2.0)   ");
        System.out.println("=================================================");
        System.out.println("Default Admin Login: admin / admin123");
        System.out.println("Data files stored in: ./data/*.csv (Persistent)");

        while (true) {
            System.out.println("\n--- Main Menu ---");
            System.out.println("1. Register as Customer");
            System.out.println("2. Customer Login");
            System.out.println("3. Admin Login");
            System.out.println("0. Exit Application");
            String choice = read("Choose an option: ");

            switch (choice) {
                case "1" -> register();
                case "2" -> login(true);
                case "3" -> login(false);
                case "0" -> {
                    System.out.println("\nThank you for using the Banking & Fraud Detection System. Goodbye!");
                    return;
                }
                default -> System.out.println("Invalid option. Please choose 1, 2, 3, or 0.");
            }
        }
    }

    // ---------- Authentication ----------

    private static void register() {
        try {
            System.out.println("\n--- Customer Registration ---");
            String name = read("Full Name: ");
            String username = read("Username: ");
            String password = read("Password (min 4 characters): ");
            Customer customer = bank.registerCustomer(name, username, password);
            System.out.println("✅ Registration successful! Your Customer ID is: " + customer.getCustomerId());
        } catch (BankException e) {
            System.out.println("❌ Registration Failed: " + e.getMessage());
        }
    }

    private static void login(boolean isCustomerExpected) {
        try {
            System.out.println("\n--- " + (isCustomerExpected ? "Customer" : "Admin") + " Login ---");
            String username = read("Username: ");
            String password = read("Password: ");
            User user = bank.login(username, password);

            if (isCustomerExpected && user instanceof Customer customer) {
                System.out.println("✅ Welcome, " + customer.getName() + " (" + customer.getCustomerId() + ")!");
                customerMenu(customer);
            } else if (!isCustomerExpected && user instanceof Admin admin) {
                System.out.println("✅ Welcome, Administrator " + admin.getName() + "!");
                adminMenu();
            } else {
                System.out.println("❌ Access Denied: This account is a " + user.getRole() + ". Please select the correct login option.");
            }
        } catch (BankException e) {
            System.out.println("❌ Login Error: " + e.getMessage());
        }
    }

    // ---------- Customer Portal ----------

    private static void customerMenu(Customer customer) {
        while (true) {
            System.out.println("\n=================================");
            System.out.println("       CUSTOMER DASHBOARD        ");
            System.out.println("=================================");
            System.out.println("1. Open Savings Account");
            System.out.println("2. Open Checking Account");
            System.out.println("3. Balance Enquiry & View Accounts");
            System.out.println("4. Deposit Money");
            System.out.println("5. Withdraw Money");
            System.out.println("6. Transfer Funds");
            System.out.println("7. View Transaction History");
            System.out.println("0. Logout");
            String choice = read("Select option: ");

            try {
                switch (choice) {
                    case "1" -> {
                        System.out.println("\nOpening Savings Account (Requires min balance $500, earns 3.5% interest)");
                        double initial = readAmount("Initial Deposit ($0 for none): ");
                        Account acc = bank.openSavingsAccount(customer, initial);
                        System.out.println("✅ Savings Account created successfully: " + acc.getAccountNumber());
                    }
                    case "2" -> {
                        System.out.println("\nOpening Checking Account (Includes $1,000 overdraft protection)");
                        double initial = readAmount("Initial Deposit ($0 for none): ");
                        Account acc = bank.openCheckingAccount(customer, initial);
                        System.out.println("✅ Checking Account created successfully: " + acc.getAccountNumber());
                    }
                    case "3" -> showCustomerAccounts(customer);
                    case "4" -> {
                        String accNo = read("Account Number: ");
                        double amt = readAmount("Deposit Amount: $");
                        bank.deposit(customer, accNo, amt);
                        System.out.println("✅ Deposit successful.");
                    }
                    case "5" -> {
                        String accNo = read("Account Number: ");
                        double amt = readAmount("Withdrawal Amount: $");
                        bank.withdraw(customer, accNo, amt);
                        System.out.println("✅ Withdrawal successful.");
                    }
                    case "6" -> {
                        String from = read("From Account Number: ");
                        String to = read("To Account Number: ");
                        double amt = readAmount("Transfer Amount: $");
                        bank.transfer(customer, from, to, amt);
                        System.out.println("✅ Transfer completed successfully.");
                    }
                    case "7" -> showHistory(customer);
                    case "0" -> {
                        System.out.println("Logged out successfully.");
                        return;
                    }
                    default -> System.out.println("Invalid option. Try again.");
                }
            } catch (BankException e) {
                System.out.println("❌ Operation Error: " + e.getMessage());
            }
        }
    }

    private static void showCustomerAccounts(Customer customer) {
        ArrayList<Account> accounts = bank.getCustomerAccounts(customer);
        if (accounts.isEmpty()) {
            System.out.println("ℹ️ You have no accounts open yet.");
            return;
        }
        System.out.println("\nYour Registered Accounts:");
        for (Account a : accounts) {
            System.out.println("  " + a);
        }
    }

    private static void showHistory(Customer customer) throws BankException {
        String accNo = read("Enter Account Number: ");
        ArrayList<Transaction> history = bank.getHistory(customer, accNo);
        if (history.isEmpty()) {
            System.out.println("ℹ️ No transactions recorded yet for account " + accNo);
            return;
        }
        System.out.println("\n--- Transaction History for " + accNo + " ---");
        for (Transaction t : history) {
            System.out.println(t);
        }
    }

    // ---------- Admin Portal ----------

    private static void adminMenu() {
        while (true) {
            System.out.println("\n=================================");
            System.out.println("    ADMIN SECURITY DASHBOARD     ");
            System.out.println("=================================");
            System.out.println("1. View All Fraud Alerts");
            System.out.println("2. View Pending Alerts Only");
            System.out.println("3. Resolve Fraud Alert");
            System.out.println("4. Dismiss Fraud Alert");
            System.out.println("5. Freeze Customer Account");
            System.out.println("6. Unfreeze Customer Account");
            System.out.println("7. View All Bank Accounts");
            System.out.println("0. Logout");
            String choice = read("Select option: ");

            try {
                switch (choice) {
                    case "1" -> {
                        ArrayList<FraudAlert> alerts = bank.getAlerts();
                        if (alerts.isEmpty()) {
                            System.out.println("ℹ️ No fraud alerts logged in the system.");
                        } else {
                            System.out.println("\n--- All Fraud Alerts ---");
                            for (FraudAlert fa : alerts) {
                                System.out.println(fa);
                            }
                        }
                    }
                    case "2" -> {
                        ArrayList<FraudAlert> pending = bank.getPendingAlerts();
                        if (pending.isEmpty()) {
                            System.out.println("✅ All fraud alerts are currently resolved or dismissed.");
                        } else {
                            System.out.println("\n--- Pending Fraud Alerts ---");
                            for (FraudAlert fa : pending) {
                                System.out.println(fa);
                            }
                        }
                    }
                    case "3" -> {
                        int id = (int) readAmount("Enter Alert ID to Resolve: ");
                        bank.resolveAlert(id);
                        System.out.println("✅ Alert #" + id + " marked as RESOLVED.");
                    }
                    case "4" -> {
                        int id = (int) readAmount("Enter Alert ID to Dismiss: ");
                        bank.dismissAlert(id);
                        System.out.println("✅ Alert #" + id + " marked as DISMISSED.");
                    }
                    case "5" -> {
                        String acc = read("Enter Account Number to Freeze: ");
                        String reason = read("Reason for Freeze: ");
                        bank.freezeAccount(acc, reason);
                        System.out.println("🔒 Account " + acc + " has been FROZEN.");
                    }
                    case "6" -> {
                        String acc = read("Enter Account Number to Unfreeze: ");
                        bank.unfreezeAccount(acc);
                        System.out.println("🔓 Account " + acc + " is now ACTIVE.");
                    }
                    case "7" -> {
                        ArrayList<Account> all = bank.getAllAccounts();
                        if (all.isEmpty()) {
                            System.out.println("ℹ️ No accounts exist in the bank.");
                        } else {
                            System.out.println("\n--- Bank Accounts Master Ledger ---");
                            for (Account a : all) {
                                System.out.println("  " + a);
                            }
                        }
                    }
                    case "0" -> {
                        System.out.println("Admin logged out.");
                        return;
                    }
                    default -> System.out.println("Invalid option. Try again.");
                }
            } catch (BankException e) {
                System.out.println("❌ Admin Action Error: " + e.getMessage());
            }
        }
    }

    // ---------- Helper Input Methods ----------

    private static String read(String prompt) {
        System.out.print(prompt);
        if (!scanner.hasNextLine()) {
            System.out.println("\nInput stream closed. Exiting.");
            System.exit(0);
        }
        return scanner.nextLine().trim();
    }

    private static double readAmount(String prompt) {
        while (true) {
            try {
                return Double.parseDouble(read(prompt));
            } catch (NumberFormatException e) {
                System.out.print("Invalid number. Please re-enter: ");
            }
        }
    }
}
