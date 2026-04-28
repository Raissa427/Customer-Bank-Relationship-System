import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public final class BankDataStore {

    private static final String DATA_DIR = "data";
    private static final String CUSTOMERS_FILE = DATA_DIR + File.separator + "customers.csv";
    private static final String ACCOUNTS_FILE = DATA_DIR + File.separator + "accounts.csv";

    private BankDataStore() {
    }

    public static void load(Bank bank, Map<String, Customer> customers) {
        ensureDataDir();
        if (!new File(CUSTOMERS_FILE).exists() || !new File(ACCOUNTS_FILE).exists()) {
            return;
        }

        loadCustomers(bank, customers);
        loadAccounts(bank, customers);
    }

    public static void save(Bank bank, Map<String, Customer> customers) {
        ensureDataDir();
        writeCustomers(customers);
        writeAccounts(bank.getAllAccountsView());
    }

    private static void loadCustomers(Bank bank, Map<String, Customer> customers) {
        try (BufferedReader reader = new BufferedReader(new FileReader(CUSTOMERS_FILE))) {
            String row;
            while ((row = reader.readLine()) != null) {
                if (row.isBlank()) {
                    continue;
                }
                String[] parts = row.split(",", -1);
                if (parts.length != 3) {
                    continue;
                }
                String customerId = parts[0].trim();
                String name = parts[1].trim();
                String email = parts[2].trim();
                if (customerId.isBlank()) {
                    continue;
                }

                try {
                    Customer customer = new Customer(customerId, name, email);
                    bank.registerCustomer(customer);
                    customers.put(customerId.toUpperCase(), customer);
                } catch (RuntimeException ignored) {
                    
                }
            }
        } catch (IOException e) {
            System.out.println("Could not read customers file: " + e.getMessage());
        }
    }

    private static void loadAccounts(Bank bank, Map<String, Customer> customers) {
        try (BufferedReader reader = new BufferedReader(new FileReader(ACCOUNTS_FILE))) {
            String row;
            while ((row = reader.readLine()) != null) {
                if (row.isBlank()) {
                    continue;
                }
                String[] parts = row.split(",", -1);
                if (parts.length != 6) {
                    continue;
                }

                String accountNumber = parts[0].trim();
                String customerId = parts[1].trim().toUpperCase();
                String accountType = parts[2].trim();
                String balanceRaw = parts[3].trim();
                String overdraftRaw = parts[4].trim();
                String savingsWithdrawalsRaw = parts[5].trim();

                Customer owner = customers.get(customerId);
                if (owner == null) {
                    continue;
                }

                try {
                    double balance = Double.parseDouble(balanceRaw);
                    Account account;
                    if ("SAVINGS".equals(accountType)) {
                        SavingsAccount savingsAccount = new SavingsAccount(accountNumber, balance, owner);
                        int withdrawalsThisMonth = Integer.parseInt(savingsWithdrawalsRaw);
                        savingsAccount.setWithdrawalsThisMonthForImport(withdrawalsThisMonth);
                        account = savingsAccount;
                    } else if ("CURRENT".equals(accountType)) {
                        double overdraftLimit = Double.parseDouble(overdraftRaw);
                        account = new CurrentAccount(accountNumber, balance, overdraftLimit, owner);
                    } else {
                        continue;
                    }
                    bank.importExistingAccount(account);
                } catch (RuntimeException ignored) {
                    // Skip bad account row and continue loading.
                }
            }
        } catch (IOException e) {
            System.out.println("Could not read accounts file: " + e.getMessage());
        }
    }

    private static void writeCustomers(Map<String, Customer> customers) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(CUSTOMERS_FILE))) {
            for (Customer customer : customers.values()) {
                writer.write(customer.getCustomerId() + ","
                        + sanitize(customer.getName()) + ","
                        + sanitize(customer.getEmail()));
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Could not write customers file: " + e.getMessage());
        }
    }

    private static void writeAccounts(List<Account> accounts) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ACCOUNTS_FILE))) {
            for (Account account : accounts) {
                String accountType;
                String overdraftLimit = "";
                String savingsWithdrawals = "";

                if (account instanceof SavingsAccount savingsAccount) {
                    accountType = "SAVINGS";
                    savingsWithdrawals = String.valueOf(savingsAccount.getWithdrawalsThisMonth());
                } else if (account instanceof CurrentAccount currentAccount) {
                    accountType = "CURRENT";
                    overdraftLimit = String.valueOf(currentAccount.getOverdraftLimit());
                } else {
                    continue;
                }

                writer.write(account.getAccountNumber() + ","
                        + account.getOwner().getCustomerId() + ","
                        + accountType + ","
                        + account.getBalance() + ","
                        + overdraftLimit + ","
                        + savingsWithdrawals);
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Could not write accounts file: " + e.getMessage());
        }
    }

    private static String sanitize(String value) {
        return value == null ? "" : value.replace(",", " ");
    }

    private static void ensureDataDir() {
        File dir = new File(DATA_DIR);
        if (!dir.exists() && !dir.mkdirs()) {
            throw new Account.BankingException("Could not create data directory: " + DATA_DIR);
        }
    }
}
