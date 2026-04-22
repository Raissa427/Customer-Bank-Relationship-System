public class Customer {

    private final String customerId;
    private final String name;
    private final String email;
    private final java.util.List<Account> accounts;

    public Customer(String customerId, String name, String email) {
        if (customerId == null || customerId.isBlank()) {
            System.out.println("  [ERROR] Customer ID cannot be blank.");
            customerId = "UNKNOWN";
        }
        if (name == null || name.isBlank()) {
            System.out.println("  [ERROR] Customer name cannot be blank.");
            name = "UNKNOWN";
        }
        this.customerId = customerId;
        this.name       = name;
        this.email      = email;
        this.accounts   = new java.util.ArrayList<>();
    }

    void addAccount(Account account) { accounts.add(account); }

    public Account getAccount(String accountNumber) {
        for (Account a : accounts) {
            if (a.getAccountNumber().equals(accountNumber)) return a;
        }
        System.out.println("  [ERROR] Account not found: " + accountNumber);
        return null;
    }

    public void displayInfo() {
        System.out.println("+------------------------------------------------------+");
        System.out.printf ("|  Customer : %-40s|%n", name);
        System.out.printf ("|  ID       : %-40s|%n", customerId);
        System.out.printf ("|  Email    : %-40s|%n", email);
        System.out.println("+------------------------------------------------------+");
        if (accounts.isEmpty()) {
            System.out.println("|  No accounts on record.                              |");
        } else {
            System.out.printf("|  Accounts (%d):%n", accounts.size());
            for (Account a : accounts) {
                System.out.printf("|    * %-48s|%n", a.toString());
                System.out.printf("|      Features: %-38s|%n", a.getAccountFeatures());
            }
        }
        System.out.println("+------------------------------------------------------+");
    }

    public String getCustomerId() { return customerId; }
    public String getName()       { return name;       }
    public String getEmail()      { return email;      }
    public java.util.List<Account> getAccounts() {
        return java.util.Collections.unmodifiableList(accounts);
    }
}
