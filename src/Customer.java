public class Customer {

    private final String customerId;
    private final String name;
    private final String email;
    private final java.util.List<Account> accounts;

    public Customer(String customerId, String name, String email) {
        validateCustomerFields(customerId, name, email);
        this.customerId = customerId.trim();
        this.name       = name.trim();
        this.email      = email.trim();
        this.accounts   = new java.util.ArrayList<>();
    }

    private static void validateCustomerFields(String customerId, String name, String email) {
        if (customerId == null || customerId.isBlank()) {
            throw new InvalidCustomerDataException("Customer ID is required and cannot be blank.");
        }
        if (name == null || name.isBlank()) {
            throw new InvalidCustomerDataException("Customer name is required and cannot be blank.");
        }
        if (email == null || email.isBlank()) {
            throw new InvalidCustomerDataException("Email is required and cannot be blank.");
        }
        if (!email.contains("@") || email.indexOf('@') == email.length() - 1) {
            throw new InvalidCustomerDataException("Email must contain a domain (e.g. name@bank.com).");
        }
    }

    void addAccount(Account account) {
        accounts.add(account);
    }

    void removeAccount(Account account) {
        accounts.remove(account);
    }

    public Account getAccount(String accountNumber) {
        if (accountNumber == null || accountNumber.isBlank()) {
            throw new InvalidCustomerDataException("Account number is required to look up an account.");
        }
        for (Account a : accounts) {
            if (a.getAccountNumber().equals(accountNumber.trim())) {
                return a;
            }
        }
        return null;
    }

    public void displayInfo() {
        System.out.println("╔══════════════════════════════════════════════════════╗");
        System.out.printf ("║  Customer : %-40s║%n", name);
        System.out.printf ("║  ID       : %-40s║%n", customerId);
        System.out.printf ("║  Email    : %-40s║%n", email);
        System.out.println("╠══════════════════════════════════════════════════════╣");
        if (accounts.isEmpty()) {
            System.out.println("║  No accounts on record.                              ║");
        } else {
            System.out.printf("║  Accounts (%d):%n", accounts.size());
            for (Account a : accounts) {
                System.out.printf("║    • %-48s║%n", a.toString());
                System.out.printf("║      Features: %-36s║%n", a.getAccountFeatures());
            }
        }
        System.out.println("╚══════════════════════════════════════════════════════╝");
    }

    public String                getCustomerId() { return customerId; }
    public String                getName()       { return name;       }
    public String                getEmail()      { return email;      }
    public java.util.List<Account> getAccounts() {
        return java.util.Collections.unmodifiableList(accounts);
    }

    public static class InvalidCustomerDataException extends Account.BankingException {
        public InvalidCustomerDataException(String message) {
            super(message);
        }
    }
}
