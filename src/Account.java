/**
 * Abstract base class representing a bank account.
 * Demonstrates: Abstraction (abstract class + method), Encapsulation (private fields + getters)
 */
public abstract class Account {

    // ── Encapsulation: all fields are private ──────────────────────────────────
    private final String accountNumber;
    private final String accountType;
    private double balance;
    private final Customer owner;

    // Keeps a history of every transaction on this account
    private final java.util.List<Transaction> transactionHistory;

    // ── Constructor ────────────────────────────────────────────────────────────
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

        // Record the opening deposit
        if (initialBalance > 0) {
            transactionHistory.add(new Transaction("OPENING DEPOSIT", initialBalance, initialBalance));
        }
    }

    // ── Abstract methods — subclasses MUST provide their own implementation ────
    /**
     * Withdraw money. Each account type enforces its own rules.
     *
     * @throws InvalidAmountException   if the amount is not a positive finite value
     * @throws AccountRuleException     if a business rule blocks the withdrawal
     */
    public abstract void withdraw(double amount);

    /**
     * Returns a short description of what makes this account type special.
     */
    public abstract String getAccountFeatures();

    // ── Concrete shared behaviour ──────────────────────────────────────────────
    /**
     * Deposit money into the account. The same logic applies for all account types.
     *
     * @throws InvalidAmountException if the amount is not a positive finite value
     */
    public void deposit(double amount) {
        validatePositiveFiniteAmount(amount, "Deposit");
        balance += amount;
        transactionHistory.add(new Transaction("DEPOSIT", amount, balance));
        System.out.printf("  [%s] Deposited $%.2f → New balance: $%.2f%n",
                accountNumber, amount, balance);
    }

    /** Shared validation for money movement amounts (deposit / internal credits). */
    protected static void validatePositiveFiniteAmount(double amount, String operationLabel) {
        if (Double.isNaN(amount) || Double.isInfinite(amount)) {
            throw new InvalidAmountException(
                    operationLabel + " amount must be a finite number (got NaN or infinite).");
        }
        if (amount <= 0) {
            throw new InvalidAmountException(operationLabel + " amount must be positive.");
        }
    }

    /**
     * Used internally by subclasses after they have validated the withdrawal.
     */
    protected void applyWithdrawal(double amount) {
        balance -= amount;
        transactionHistory.add(new Transaction("WITHDRAWAL", amount, balance));
    }

    /**
     * Print the full transaction history for this account.
     */
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

    // ── Getters (Encapsulation: controlled read access) ────────────────────────
    public String  getAccountNumber()            { return accountNumber; }
    public String  getAccountType()              { return accountType;   }
    public double  getBalance()                  { return balance;       }
    public Customer getOwner()                   { return owner;         }
    public java.util.List<Transaction> getHistory() { return java.util.Collections.unmodifiableList(transactionHistory); }

    // Protected setter — only subclasses may directly set the balance
    // (used by CurrentAccount for overdraft adjustments)
    protected void setBalance(double balance)    { this.balance = balance; }

    @Override
    public String toString() {
        return String.format("%s [%s] Balance: $%.2f", accountType, accountNumber, balance);
    }

    // ═══ Nested custom unchecked exceptions (no extra .java files) ═════════════════

    /** Root runtime exception for the banking model (all domain errors extend this). */
    public static class BankingException extends RuntimeException {
        public BankingException(String message) {
            super(message);
        }

        public BankingException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    /** Amount is zero, negative, NaN, or infinite when a positive finite value is required. */
    public static class InvalidAmountException extends BankingException {
        public InvalidAmountException(String message) {
            super(message);
        }
    }

    /** Business rules on an account block the operation (funds, limits, overdraft, etc.). */
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