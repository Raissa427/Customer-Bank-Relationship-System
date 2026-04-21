public class SavingsAccount extends Account {

    private static final double INTEREST_RATE = 0.035;
    private static final int    MONTHLY_WITHDRAWAL_LIMIT = 3;

    private int withdrawalsThisMonth = 0;

    public SavingsAccount(String accountNumber, double initialBalance, Customer owner) {
        super(accountNumber, "Savings Account", initialBalance, owner);
    }

    @Override
    public void withdraw(double amount) {
        validatePositiveFiniteAmount(amount, "Withdrawal");
        if (withdrawalsThisMonth >= MONTHLY_WITHDRAWAL_LIMIT) {
            throw new Account.WithdrawalLimitExceededException(String.format(
                    "Monthly withdrawal limit (%d) reached for %s (used %d this period).",
                    MONTHLY_WITHDRAWAL_LIMIT, getAccountNumber(), withdrawalsThisMonth));
        }
        if (amount > getBalance()) {
            throw new Account.InsufficientFundsException(String.format(
                    "Insufficient funds on %s: available $%.2f, requested $%.2f.",
                    getAccountNumber(), getBalance(), amount));
        }
        applyWithdrawal(amount);
        withdrawalsThisMonth++;
        System.out.printf("  [%s] Withdrew $%.2f → New balance: $%.2f  (withdrawals this month: %d/%d)%n",
                getAccountNumber(), amount, getBalance(), withdrawalsThisMonth, MONTHLY_WITHDRAWAL_LIMIT);
    }

    @Override
    public String getAccountFeatures() {
        return String.format("Earns %.1f%% annual interest | Max %d withdrawals/month | No overdraft",
                INTEREST_RATE * 100, MONTHLY_WITHDRAWAL_LIMIT);
    }

    public void applyMonthlyInterest() {
        double interest = getBalance() * (INTEREST_RATE / 12);
        if (interest <= 0) {
            System.out.printf("  [%s] No interest accrued (zero balance).%n", getAccountNumber());
            return;
        }
        try {
            deposit(interest);
            System.out.printf("  [%s] Interest credited: $%.2f%n", getAccountNumber(), interest);
        } catch (Account.InvalidAmountException e) {
            System.out.printf("  [%s] Interest not applied: %s%n", getAccountNumber(), e.getMessage());
        }
    }

    public void resetMonthlyWithdrawals() {
        withdrawalsThisMonth = 0;
    }

    public int getWithdrawalsThisMonth() { return withdrawalsThisMonth; }
}
