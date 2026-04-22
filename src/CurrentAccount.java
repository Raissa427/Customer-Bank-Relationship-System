public class CurrentAccount extends Account {

    private static final double OVERDRAFT_FEE = 15.00;

    private final double overdraftLimit;
    private boolean overdraftActive = false;

    public CurrentAccount(String accountNumber, double initialBalance,
                          double overdraftLimit, Customer owner) {
        super(accountNumber, "Current Account", initialBalance, owner);
        if (Double.isNaN(overdraftLimit) || Double.isInfinite(overdraftLimit) || overdraftLimit < 0) {
            throw new Account.InvalidAmountException(
                    "Overdraft limit must be a non-negative finite number (maximum debt allowed).");
        }
        this.overdraftLimit = overdraftLimit;
    }

    @Override
    public void withdraw(double amount) {
        validatePositiveFiniteAmount(amount, "Withdrawal");
        double projected = getBalance() - amount;
        if (projected < -overdraftLimit) {
            throw new Account.OverdraftLimitExceededException(String.format(
                    "Overdraft limit ($%.2f) would be exceeded on %s (projected balance: $%.2f).",
                    overdraftLimit, getAccountNumber(), projected));
        }
        applyWithdrawal(amount);
        if (getBalance() < 0) {
            if (!overdraftActive) {
                applyWithdrawal(OVERDRAFT_FEE);
                overdraftActive = true;
                System.out.printf("  [%s] Overdraft activated. Fee of $%.2f charged. Balance: $%.2f%n",
                        getAccountNumber(), OVERDRAFT_FEE, getBalance());
            } else {
                System.out.printf("  [%s] Withdrew $%.2f (overdraft) → Balance: $%.2f%n",
                        getAccountNumber(), amount, getBalance());
            }
        } else {
            overdraftActive = false;
            System.out.printf("  [%s] Withdrew $%.2f → New balance: $%.2f%n",
                    getAccountNumber(), amount, getBalance());
        }
    }

    @Override
    public String getAccountFeatures() {
        return String.format("Overdraft limit: $%.2f | Overdraft fee: $%.2f | Unlimited transactions",
                overdraftLimit, OVERDRAFT_FEE);
    }

    public double  getOverdraftLimit()  { return overdraftLimit;  }
    public boolean isOverdraftActive()  { return overdraftActive; }
}
