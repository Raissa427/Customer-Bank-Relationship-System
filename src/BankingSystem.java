import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class BankingSystem {

    private static final Scanner INPUT = new Scanner(System.in);
    private static final Map<String, Customer> CUSTOMERS = new LinkedHashMap<>();
    private static int customerCounter = 1;

    public static void main(String[] args) {
        Bank bank = new Bank("SheCanCode Bank", "SCB");
        BankDataStore.load(bank, CUSTOMERS);
        syncCustomerCounter();
        System.out.println("Saved data loaded.");
        boolean running = true;

        while (running) {
            printMenu();
            int option = readInt("Choose service (1-7): ", 1, 7);
            switch (option) {
                case 1 -> registerCustomer(bank);
                case 2 -> openAccount(bank);
                case 3 -> depositToAccount(bank);
                case 4 -> withdrawFromAccount(bank);
                case 5 -> transferFunds(bank);
                case 6 -> printCustomerStatement();
                case 7 -> {
                    BankDataStore.save(bank, CUSTOMERS);
                    running = false;
                    System.out.println("Thanks for banking with us.");
                }
                default -> System.out.println("Invalid menu option.");
            }
        }
    }

    private static void printMenu() {
        System.out.println("\n================= BANK MENU =================");
        System.out.println("1. Register customer");
        System.out.println("2. Open account (Savings/Current)");
        System.out.println("3. Deposit");
        System.out.println("4. Withdraw");
        System.out.println("5. Transfer");
        System.out.println("6. View customer accounts and statements");
        System.out.println("7. Exit");
        System.out.println("=============================================");
    }

    private static void registerCustomer(Bank bank) {
        String name = readNonBlank("Enter full name: ");
        String email = readValidEmail("Enter email: ");
        String customerId = String.format("C-%03d", customerCounter++);

        try {
            Customer customer = new Customer(customerId, name, email);
            bank.registerCustomer(customer);
            CUSTOMERS.put(customerId, customer);
            BankDataStore.save(bank, CUSTOMERS);
            System.out.println("Registration successful. Your customer ID is " + customerId);
        } catch (Account.BankingException e) {
            System.out.println("Registration failed: " + e.getMessage());
        }
    }

    private static void openAccount(Bank bank) {
        Customer customer = selectRegisteredCustomer("Enter customer ID to open account: ");
        if (customer == null) {
            return;
        }

        System.out.println("Select account type:");
        System.out.println("1. Savings Account");
        System.out.println("2. Current Account");
        int accountOption = readInt("Choose account type (1-2): ", 1, 2);
        double openingBalance = readPositiveDouble("Enter opening deposit amount: ");

        try {
            if (accountOption == 1) {
                bank.openSavingsAccount(customer, openingBalance);
            } else {
                double overdraftLimit = readNonNegativeDouble("Enter overdraft limit: ");
                bank.openCurrentAccount(customer, openingBalance, overdraftLimit);
            }
            BankDataStore.save(bank, CUSTOMERS);
            System.out.println("Account opened successfully.");
        } catch (Account.BankingException e) {
            System.out.println("Could not open account: " + e.getMessage());
        }
    }

    private static void depositToAccount(Bank bank) {
        Account account = selectAccount(bank, "Enter account number to deposit to: ");
        if (account == null) {
            return;
        }

        double amount = readPositiveDouble("Enter amount to deposit: ");
        try {
            account.deposit(amount);
            BankDataStore.save(bank, CUSTOMERS);
        } catch (Account.BankingException e) {
            System.out.println("Deposit failed: " + e.getMessage());
        }
    }

    private static void withdrawFromAccount(Bank bank) {
        Account account = selectAccount(bank, "Enter account number to withdraw from: ");
        if (account == null) {
            return;
        }

        double amount = readPositiveDouble("Enter amount to withdraw: ");
        try {
            account.withdraw(amount);
            BankDataStore.save(bank, CUSTOMERS);
        } catch (Account.BankingException e) {
            System.out.println("Withdrawal failed: " + e.getMessage());
        }
    }

    private static void transferFunds(Bank bank) {
        Account from = selectAccount(bank, "Enter source account number: ");
        if (from == null) {
            return;
        }
        Account to = selectAccount(bank, "Enter destination account number: ");
        if (to == null) {
            return;
        }
        double amount = readPositiveDouble("Enter transfer amount: ");

        try {
            bank.transfer(from, to, amount);
            BankDataStore.save(bank, CUSTOMERS);
        } catch (Account.BankingException e) {
            System.out.println("Transfer failed: " + e.getMessage());
        }
    }

    private static void printCustomerStatement() {
        Customer customer = selectRegisteredCustomer("Enter customer ID to view statement: ");
        if (customer == null) {
            return;
        }

        customer.displayInfo();
        List<Account> accounts = customer.getAccounts();
        if (accounts.isEmpty()) {
            return;
        }

        for (Account account : accounts) {
            System.out.println();
            System.out.println("Statement for account: " + account.getAccountNumber());
            account.printStatement();
        }
    }

    private static Customer selectRegisteredCustomer(String prompt) {
        if (CUSTOMERS.isEmpty()) {
            System.out.println("No customer has registered yet. Please register first.");
            return null;
        }

        String customerId = readNonBlank(prompt).toUpperCase();
        Customer customer = CUSTOMERS.get(customerId);
        if (customer == null) {
            System.out.println("Customer not found. Please use a valid registered customer ID.");
            return null;
        }
        return customer;
    }

    private static Account selectAccount(Bank bank, String prompt) {
        String accountNumber = readNonBlank(prompt);
        try {
            return bank.getAccountByNumber(accountNumber);
        } catch (Bank.InvalidTransferException e) {
            System.out.println("Account lookup failed: " + e.getMessage());
            return null;
        }
    }

    private static int readInt(String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            String raw = INPUT.nextLine().trim();
            try {
                int value = Integer.parseInt(raw);
                if (value < min || value > max) {
                    System.out.printf("Enter a number between %d and %d.%n", min, max);
                    continue;
                }
                return value;
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid whole number.");
            }
        }
    }

    private static double readPositiveDouble(String prompt) {
        while (true) {
            System.out.print(prompt);
            String raw = INPUT.nextLine().trim();
            try {
                double value = Double.parseDouble(raw);
                if (Double.isNaN(value) || Double.isInfinite(value) || value <= 0) {
                    System.out.println("Enter a valid amount greater than zero.");
                    continue;
                }
                return value;
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }

    private static double readNonNegativeDouble(String prompt) {
        while (true) {
            System.out.print(prompt);
            String raw = INPUT.nextLine().trim();
            try {
                double value = Double.parseDouble(raw);
                if (Double.isNaN(value) || Double.isInfinite(value) || value < 0) {
                    System.out.println("Enter a valid amount (0 or more).");
                    continue;
                }
                return value;
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }

    private static String readNonBlank(String prompt) {
        while (true) {
            System.out.print(prompt);
            String value = INPUT.nextLine();
            if (value != null && !value.trim().isEmpty()) {
                return value.trim();
            }
            System.out.println("This field cannot be blank.");
        }
    }

    private static String readValidEmail(String prompt) {
        while (true) {
            String email = readNonBlank(prompt);
            int atIndex = email.indexOf('@');
            int lastDot = email.lastIndexOf('.');
            boolean valid = atIndex > 0 && atIndex < email.length() - 1 && lastDot > atIndex + 1
                    && lastDot < email.length() - 1;
            if (valid) {
                return email;
            }
            System.out.println("Enter a valid email, for example name@example.com.");
        }
    }

    private static void syncCustomerCounter() {
        int max = 0;
        for (String customerId : CUSTOMERS.keySet()) {
            if (customerId == null) {
                continue;
            }
            String upper = customerId.toUpperCase();
            if (!upper.startsWith("C-") || upper.length() <= 2) {
                continue;
            }
            String suffix = upper.substring(2);
            try {
                int id = Integer.parseInt(suffix);
                if (id > max) {
                    max = id;
                }
            } catch (NumberFormatException ignored) {
                // Ignore non-standard IDs while syncing ID counter.
            }
        }
        customerCounter = max + 1;
    }
}
