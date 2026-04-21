public class Bank {

    private final String bankName;
    private final String bankCode;
    private final java.util.List<Customer>  customers;
    private final java.util.List<Account>   accounts;
    private int accountCounter = 1000;

    public Bank(String bankName, String bankCode) {
        this.bankName  = bankName;
        this.bankCode  = bankCode;
        this.customers = new java.util.ArrayList<>();
        this.accounts  = new java.util.ArrayList<>();
    }

    public void registerCustomer(Customer customer) {
        customers.add(customer);
        System.out.printf("[%s] Customer '%s' registered successfully.%n",
                bankName, customer.getName());
    }

    public SavingsAccount openSavingsAccount(Customer customer, double initialDeposit) {
        requireRegistered(customer);
        validateOpeningAmount(initialDeposit, "Savings opening balance");
        String accNum = generateAccountNumber("SA");
        SavingsAccount account = new SavingsAccount(accNum, initialDeposit, customer);
        accounts.add(account);
        customer.addAccount(account);
        System.out.printf("[%s] Savings account %s opened for %s (initial deposit: $%.2f)%n",
                bankName, accNum, customer.getName(), initialDeposit);
        return account;
    }

    public CurrentAccount openCurrentAccount(Customer customer, double initialDeposit,
                                             double overdraftLimit) {
        requireRegistered(customer);
        validateOpeningAmount(initialDeposit, "Current account opening balance");
        String accNum = generateAccountNumber("CA");
        CurrentAccount account = new CurrentAccount(accNum, initialDeposit, overdraftLimit, customer);
        accounts.add(account);
        customer.addAccount(account);
        System.out.printf("[%s] Current account %s opened for %s (overdraft limit: $%.2f)%n",
                bankName, accNum, customer.getName(), overdraftLimit);
        return account;
    }

    public void transfer(Account from, Account to, double amount) {
        validateInternalTransfer(from, to, amount);
        System.out.printf("%n[%s] TRANSFER  %s → %s  ($%.2f)%n",
                bankName, from.getAccountNumber(), to.getAccountNumber(), amount);
        try {
            from.withdraw(amount);
            try {
                to.deposit(amount);
            } catch (Account.InvalidAmountException creditError) {
                try {
                    from.deposit(amount);
                } catch (Account.InvalidAmountException rollbackFailed) {
                    throw new InvalidTransferException(
                            "Transfer aborted: credit failed and automatic rollback failed — contact support. "
                                    + rollbackFailed.getMessage(),
                            rollbackFailed);
                }
                throw new InvalidTransferException(
                        "Transfer rolled back: destination rejected deposit (" + creditError.getMessage() + ")",
                        creditError);
            }
            System.out.println("  [TRANSFER OK] Funds moved successfully.");
        } catch (Account.AccountRuleException ruleViolation) {
            System.out.println("  [TRANSFER FAILED] " + ruleViolation.getMessage());
            throw ruleViolation;
        }
    }

    public void printBankSummary() {
        System.out.println("\n╔══════════════════════════════════════════════════════╗");
        System.out.printf ("║  %-52s║%n", bankName + " (" + bankCode + ") — Bank Summary");
        System.out.printf ("║  Registered customers : %-28d║%n", customers.size());
        System.out.printf ("║  Total accounts       : %-28d║%n", accounts.size());
        double totalDeposits = accounts.stream().mapToDouble(Account::getBalance).sum();
        System.out.printf ("║  Total deposits held  : $%-27.2f║%n", totalDeposits);
        System.out.println("╠══════════════════════════════════════════════════════╣");
        for (Account a : accounts) {
            System.out.printf("║  %-52s║%n", a.toString() + " | " + a.getOwner().getName());
        }
        System.out.println("╚══════════════════════════════════════════════════════╝");
    }

    private String generateAccountNumber(String prefix) {
        return bankCode + "-" + prefix + "-" + (++accountCounter);
    }

    private void requireRegistered(Customer customer) {
        if (customer == null) {
            throw new Account.BankingException("Customer cannot be null.");
        }
        if (!customers.contains(customer)) {
            throw new CustomerNotRegisteredException(
                    "Customer '" + customer.getName() + "' is not registered at " + bankName + ".");
        }
    }

    private static void validateOpeningAmount(double amount, String context) {
        if (Double.isNaN(amount) || Double.isInfinite(amount)) {
            throw new Account.InvalidAmountException(context + " must be a finite number.");
        }
        if (amount < 0) {
            throw new Account.InvalidAmountException(context + " cannot be negative.");
        }
    }

    private void validateInternalTransfer(Account from, Account to, double amount) {
        if (from == null || to == null) {
            throw new InvalidTransferException("Transfer requires both a source and a destination account.");
        }
        if (from == to) {
            throw new InvalidTransferException("Cannot transfer between the same account.");
        }
        if (Double.isNaN(amount) || Double.isInfinite(amount)) {
            throw new InvalidTransferException("Transfer amount must be a finite number.");
        }
        if (amount <= 0) {
            throw new InvalidTransferException("Transfer amount must be greater than zero.");
        }
        if (!accounts.contains(from) || !accounts.contains(to)) {
            throw new InvalidTransferException(
                    "Both accounts must be held at " + bankName + " for an internal transfer.");
        }
    }

    public String getBankName() { return bankName; }
    public String getBankCode() { return bankCode; }

    public static class CustomerNotRegisteredException extends Account.BankingException {
        public CustomerNotRegisteredException(String message) {
            super(message);
        }
    }

    public static class InvalidTransferException extends Account.BankingException {
        public InvalidTransferException(String message) {
            super(message);
        }

        public InvalidTransferException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
