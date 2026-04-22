public abstract class Account {

    private final String accountNumber;
    private final String accountType;
    private double balance;
    private final Customer owner;

    private final java.util.List<Transaction> transactionHistory;

    public Account(String accountNumber, String accountType, double initialBalance, Customer owner) {
        if (owner == null) {
            throw new BankingException("Every account must have an owner customer.");
        }
        if (accountNumber == null || accountNumber.isBlank()) {
            throw new BankingException("Account number is required.");
        }
        if (accountType == null || accountType.isBlank()) {
            throw new BankingException("Account type is required.");
        }
        if (Double.isNaN(initialBalance) || Double.isInfinite(initialBalance)) {
            throw new InvalidAmountException("Opening balance must be a finite number.");
        }
        if (initialBalance < 0) {
            throw new InvalidAmountException("Opening balance cannot be negative.");
        }
        this.accountNumber      = accountNumber.trim();
        this.accountType        = accountType.trim();
        this.balance            = initialBalance;
        this.owner              = owner;
        this.transactionHistory = new java.util.ArrayList<>();

        if (initialBalance > 0) {
            transactionHistory.add(new Transaction("OPENING DEPOSIT", initialBalance, initialBalance));
        }
    }

    public abstract void withdraw(double amount);

    public abstract String getAccountFeatures();

    public void deposit(double amount) {
        validatePositiveFiniteAmount(amount, "Deposit");
        balance += amount;
        transactionHistory.add(new Transaction("DEPOSIT", amount, balance));
        System.out.printf("  [%s] Deposited $%.2f → New balance: $%.2f%n",
                accountNumber, amount, balance);
    }

    protected static void validatePositiveFiniteAmount(double amount, String operationLabel) {
        if (Double.isNaN(amount) || Double.isInfinite(amount)) {
            throw new InvalidAmountException(
                    operationLabel + " amount must be a finite number (got NaN or infinite).");
        }
        if (amount <= 0) {
            throw new InvalidAmountException(operationLabel + " amount must be positive.");
        }
    }

    protected void applyWithdrawal(double amount) {
        balance -= amount;
        transactionHistory.add(new Transaction("WITHDRAWAL", amount, balance));
    }

    public void printStatement() {
        System.out.println("  ┌─────────────────────────────────────────────────┐");
        System.out.printf ("  │  Statement for %s (%s)%n", accountNumber, accountType);
        System.out.printf ("  │  Owner : %s%n", owner.getName());
        System.out.println("  ├──────────────────────┬────────────┬─────────────┤");
        System.out.println("  │ Type                 │    Amount  │     Balance │");
        System.out.println("  ├──────────────────────┼────────────┼─────────────┤");
        for (Transaction t : transactionHistory) {
            System.out.printf("  │ %-20s │ %10.2f │ %11.2f │%n",
                    t.getType(), t.getAmount(), t.getBalanceAfter());
        }
        System.out.println("  └──────────────────────┴────────────┴─────────────┘");
    }

    public String  getAccountNumber()            { return accountNumber; }
    public String  getAccountType()              { return accountType;   }
    public double  getBalance()                  { return balance;       }
    public Customer getOwner()                   { return owner;         }
    public java.util.List<Transaction> getHistory() { return java.util.Collections.unmodifiableList(transactionHistory); }

    protected void setBalance(double balance)    { this.balance = balance; }

    @Override
    public String toString() {
        return String.format("%s [%s] Balance: $%.2f", accountType, accountNumber, balance);
    }

    public static class BankingException extends RuntimeException {
        public BankingException(String message) {
            super(message);
        }

        public BankingException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    public static class InvalidAmountException extends BankingException {
        public InvalidAmountException(String message) {
            super(message);
        }
    }

    public static class AccountRuleException extends BankingException {
        public AccountRuleException(String message) {
            super(message);
        }
    }

    public static class InsufficientFundsException extends AccountRuleException {
        public InsufficientFundsException(String message) {
            super(message);
        }
    }

    public static class WithdrawalLimitExceededException extends AccountRuleException {
        public WithdrawalLimitExceededException(String message) {
            super(message);
        }
    }

    public static class OverdraftLimitExceededException extends AccountRuleException {
        public OverdraftLimitExceededException(String message) {
            super(message);
        }
    }
}
