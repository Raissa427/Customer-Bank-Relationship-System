public abstract class Account {

    private final String accountNumber;
    private final String accountType;
    private double balance;
    private final Customer owner;
    private final java.util.List<Transaction> transactionHistory;

    public Account(String accountNumber, String accountType, double initialBalance, Customer owner) {
        if (initialBalance < 0) {
            System.out.println("  [ERROR] Initial balance cannot be negative: " + initialBalance);
            initialBalance = 0;
        }
        this.accountNumber      = accountNumber;
        this.accountType        = accountType;
        this.balance            = initialBalance;
        this.owner              = owner;
        this.transactionHistory = new java.util.ArrayList<>();

        if (initialBalance > 0) {
            transactionHistory.add(new Transaction("OPENING DEPOSIT", initialBalance, initialBalance));
        }
    }

    public abstract boolean withdraw(double amount);
    public abstract String getAccountFeatures();

    public boolean deposit(double amount) {
        if (!validateAmount(amount)) return false;
        balance += amount;
        transactionHistory.add(new Transaction("DEPOSIT", amount, balance));
        System.out.printf("  [%s] Deposited $%.2f -> New balance: $%.2f%n",
                accountNumber, amount, balance);
        return true;
    }

    protected void applyWithdrawal(double amount) {
        balance -= amount;
        transactionHistory.add(new Transaction("WITHDRAWAL", amount, balance));
    }

    public static final double MAX_SINGLE_TRANSACTION = 50_000.00;

    protected boolean validateAmount(double amount) {
        if (amount <= 0) {
            System.out.printf("  [ERROR] Amount must be positive. Got: $%.2f%n", amount);
            return false;
        }
        if (amount > MAX_SINGLE_TRANSACTION) {
            System.out.printf("  [ERROR] Amount $%.2f exceeds single-transaction limit of $%.2f%n",
                    amount, MAX_SINGLE_TRANSACTION);
            return false;
        }
        return true;
    }

    public void printStatement() {
        System.out.println("  +----------------------+------------+-------------+");
        System.out.printf ("  |  Statement: %s (%s)%n", accountNumber, accountType);
        System.out.printf ("  |  Owner: %s%n", owner.getName());
        System.out.println("  +----------------------+------------+-------------+");
        System.out.println("  | Type                 |    Amount  |     Balance |");
        System.out.println("  +----------------------+------------+-------------+");
        for (Transaction t : transactionHistory) {
            System.out.printf("  | %-20s | %10.2f | %11.2f |%n",
                    t.getType(), t.getAmount(), t.getBalanceAfter());
        }
        System.out.println("  +----------------------+------------+-------------+");
    }

    public String   getAccountNumber() { return accountNumber; }
    public String   getAccountType()   { return accountType;   }
    public double   getBalance()       { return balance;       }
    public Customer getOwner()         { return owner;         }
    public java.util.List<Transaction> getHistory() {
        return java.util.Collections.unmodifiableList(transactionHistory);
    }
    protected void setBalance(double balance) { this.balance = balance; }

    @Override
    public String toString() {
        return String.format("%s [%s] Balance: $%.2f", accountType, accountNumber, balance);
    }
}
