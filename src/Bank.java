public class Bank {

    private final String bankName;
    private final String bankCode;
    private final java.util.List<Customer> customers;
    private final java.util.List<Account>  accounts;
    private int accountCounter = 1000;

    public Bank(String bankName, String bankCode) {
        this.bankName  = bankName;
        this.bankCode  = bankCode;
        this.customers = new java.util.ArrayList<>();
        this.accounts  = new java.util.ArrayList<>();
    }

    public boolean registerCustomer(Customer customer) {
        for (Customer c : customers) {
            if (c.getCustomerId().equals(customer.getCustomerId())) {
                System.out.printf("  [ERROR] Customer '%s' is already registered at %s.%n",
                        customer.getName(), bankName);
                return false;
            }
        }
        customers.add(customer);
        System.out.printf("[%s] Customer '%s' registered successfully.%n",
                bankName, customer.getName());
        return true;
    }

    public SavingsAccount openSavingsAccount(Customer customer, double initialDeposit) {
        if (!customers.contains(customer)) {
            System.out.printf("  [ERROR] Customer '%s' is not registered at %s.%n",
                    customer.getName(), bankName);
            return null;
        }
        String accNum = generateAccountNumber("SA");
        SavingsAccount account = new SavingsAccount(accNum, initialDeposit, customer);
        accounts.add(account);
        customer.addAccount(account);
        System.out.printf("[%s] Savings account %s opened for %s (deposit: $%.2f)%n",
                bankName, accNum, customer.getName(), initialDeposit);
        return account;
    }

    public CurrentAccount openCurrentAccount(Customer customer, double initialDeposit,
                                             double overdraftLimit) {
        if (!customers.contains(customer)) {
            System.out.printf("  [ERROR] Customer '%s' is not registered at %s.%n",
                    customer.getName(), bankName);
            return null;
        }
        String accNum = generateAccountNumber("CA");
        CurrentAccount account = new CurrentAccount(accNum, initialDeposit, overdraftLimit, customer);
        accounts.add(account);
        customer.addAccount(account);
        System.out.printf("[%s] Current account %s opened for %s (overdraft: $%.2f)%n",
                bankName, accNum, customer.getName(), overdraftLimit);
        return account;
    }

    public Account findAccount(String accountNumber) {
        for (Account a : accounts) {
            if (a.getAccountNumber().equals(accountNumber)) return a;
        }
        System.out.println("  [ERROR] Account not found: " + accountNumber);
        return null;
    }

    public boolean transfer(Account from, Account to, double amount) {
        System.out.printf("%n[%s] TRANSFER  %s -> %s  ($%.2f)%n",
                bankName, from.getAccountNumber(), to.getAccountNumber(), amount);
        boolean withdrawn = from.withdraw(amount);
        if (withdrawn) {
            to.deposit(amount);
            System.out.printf("  [OK] Transfer of $%.2f completed successfully.%n", amount);
            return true;
        }
        System.out.println("  [TRANSFER FAILED] See error above.");
        return false;
    }

    public void printBankSummary() {
        System.out.println("\n+------------------------------------------------------+");
        System.out.printf ("|  %-52s|%n", bankName + " (" + bankCode + ") -- Bank Summary");
        System.out.printf ("|  Registered customers : %-28d|%n", customers.size());
        System.out.printf ("|  Total accounts       : %-28d|%n", accounts.size());
        double total = accounts.stream().mapToDouble(Account::getBalance).sum();
        System.out.printf ("|  Total deposits held  : $%-27.2f|%n", total);
        System.out.println("+------------------------------------------------------+");
        for (Account a : accounts) {
            System.out.printf("|  %-50s  |%n", a + " | " + a.getOwner().getName());
        }
        System.out.println("+------------------------------------------------------+");
    }

    private String generateAccountNumber(String prefix) {
        return bankCode + "-" + prefix + "-" + (++accountCounter);
    }

    public String getBankName() { return bankName; }
    public String getBankCode() { return bankCode; }
}
