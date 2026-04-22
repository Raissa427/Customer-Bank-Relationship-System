public class SavingsAccount extends Account {

    private static final double INTEREST_RATE            = 0.035;
    private static final int    MONTHLY_WITHDRAWAL_LIMIT = 3;

    private int withdrawalsThisMonth = 0;

    public SavingsAccount(String accountNumber, double initialBalance, Customer owner) {
        super(accountNumber, "Savings Account", initialBalance, owner);
    }

    @Override
    public boolean withdraw(double amount) {
        if (!validateAmount(amount)) return false;

        if (withdrawalsThisMonth >= MONTHLY_WITHDRAWAL_LIMIT) {
            System.out.printf("  [ERROR] Withdrawal limit reached for account %s. " +
                    "Used: %d/%d this month.%n",
                    getAccountNumber(), withdrawalsThisMonth, MONTHLY_WITHDRAWAL_LIMIT);
            return false;
        }

        if (amount > getBalance()) {
            System.out.printf("  [ERROR] Insufficient funds in account %s. " +
                    "Balance: $%.2f, Requested: $%.2f, Shortfall: $%.2f%n",
                    getAccountNumber(), getBalance(), amount, amount - getBalance());
            return false;
        }

        applyWithdrawal(amount);
        withdrawalsThisMonth++;
        System.out.printf("  [%s] Withdrew $%.2f -> New balance: $%.2f  (%d/%d this month)%n",
                getAccountNumber(), amount, getBalance(),
                withdrawalsThisMonth, MONTHLY_WITHDRAWAL_LIMIT);
        return true;
    }

    @Override
    public String getAccountFeatures() {
        return String.format("Earns %.1f%% annual interest | Max %d withdrawals/month | No overdraft",
                INTEREST_RATE * 100, MONTHLY_WITHDRAWAL_LIMIT);
    }

    public void applyMonthlyInterest() {
        double interest = getBalance() * (INTEREST_RATE / 12);
        deposit(interest);
        System.out.printf("  [%s] Interest credited: $%.2f%n", getAccountNumber(), interest);
    }

    public void resetMonthlyWithdrawals() { withdrawalsThisMonth = 0; }
    public int  getWithdrawalsThisMonth() { return withdrawalsThisMonth; }
}
