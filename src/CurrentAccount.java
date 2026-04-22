public class CurrentAccount extends Account {

    private static final double OVERDRAFT_FEE = 15.00;

    private final double overdraftLimit;
    private boolean overdraftActive = false;

    public CurrentAccount(String accountNumber, double initialBalance,
                          double overdraftLimit, Customer owner) {
        super(accountNumber, "Current Account", initialBalance, owner);
        this.overdraftLimit = Math.abs(overdraftLimit);
    }

    @Override
    public boolean withdraw(double amount) {
        if (!validateAmount(amount)) return false;

        double projected = getBalance() - amount;
        if (projected < -overdraftLimit) {
            System.out.printf("  [ERROR] Overdraft limit exceeded for account %s. " +
                    "Limit: $%.2f, Projected balance: $%.2f%n",
                    getAccountNumber(), overdraftLimit, projected);
            return false;
        }

        applyWithdrawal(amount);

        if (getBalance() < 0 && !overdraftActive) {
            applyWithdrawal(OVERDRAFT_FEE);
            overdraftActive = true;
            System.out.printf("  [%s] Overdraft activated. Fee $%.2f charged. Balance: $%.2f%n",
                    getAccountNumber(), OVERDRAFT_FEE, getBalance());
        } else {
            System.out.printf("  [%s] Withdrew $%.2f -> Balance: $%.2f%n",
                    getAccountNumber(), amount, getBalance());
        }

        if (getBalance() >= 0) overdraftActive = false;
        return true;
    }

    @Override
    public String getAccountFeatures() {
        return String.format("Overdraft limit: $%.2f | Overdraft fee: $%.2f | Unlimited transactions",
                overdraftLimit, OVERDRAFT_FEE);
    }

    public double  getOverdraftLimit()  { return overdraftLimit;  }
    public boolean isOverdraftActive()  { return overdraftActive; }
}
