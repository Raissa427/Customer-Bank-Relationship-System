import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Bank {

    private final String bankName;
    private final String bankCode;

    private final List<Customer> customers;
    private final List<Account> accounts;

    private final Set<String> registeredCustomerIds;
    private final Map<String, Account> accountByNumber;
    private final Map<String, List<Account>> accountsByCustomerId;

    private int accountCounter = 1000;

    public Bank(String bankName, String bankCode) {
        this.bankName  = bankName;
        this.bankCode  = bankCode;
        this.customers = new ArrayList<>();
        this.accounts  = new ArrayList<>();
        this.registeredCustomerIds = new LinkedHashSet<>();
        this.accountByNumber = new HashMap<>();
        this.accountsByCustomerId = new LinkedHashMap<>();
    }

    public void registerCustomer(Customer customer) {
        if (customer == null) {
            throw new Account.BankingException("Customer cannot be null.");
        }
        String id = customer.getCustomerId();
        if (!registeredCustomerIds.add(id)) {
            throw new DuplicateCustomerException(
                    "Customer ID '" + id + "' is already registered at " + bankName + ".");
        }
        customers.add(customer);
        System.out.printf("[%s] Customer '%s' registered successfully.%n",
                bankName, customer.getName());
    }

    public SavingsAccount openSavingsAccount(Customer customer, double initialDeposit) {
        requireRegistered(customer);
        validateOpeningAmount(initialDeposit, "Savings opening balance");
        String accNum = generateAccountNumber("SA");
        SavingsAccount account = new SavingsAccount(accNum, initialDeposit, customer);
        registerNewAccount(account, customer);
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
        registerNewAccount(account, customer);
        System.out.printf("[%s] Current account %s opened for %s (overdraft limit: $%.2f)%n",
                bankName, accNum, customer.getName(), overdraftLimit);
        return account;
    }

    private void registerNewAccount(Account account, Customer customer) {
        accounts.add(account);
        customer.addAccount(account);
        accountByNumber.put(account.getAccountNumber(), account);
        accountsByCustomerId
                .computeIfAbsent(customer.getCustomerId(), k -> new ArrayList<>())
                .add(account);
    }

    public Account getAccountByNumber(String accountNumber) {
        if (accountNumber == null || accountNumber.isBlank()) {
            throw new InvalidTransferException("Account number is required for lookup.");
        }
        Account a = accountByNumber.get(accountNumber.trim());
        if (a == null) {
            throw new InvalidTransferException(
                    "No account '" + accountNumber + "' at " + bankName + ".");
        }
        return a;
    }

    public List<Account> getAccountsForCustomer(String customerId) {
        List<Account> list = accountsByCustomerId.get(customerId);
        if (list == null) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(list);
    }

    public void closeAccount(Account account) {
        if (account == null) {
            throw new InvalidTransferException("Account cannot be null.");
        }
        requireRegistered(account.getOwner());
        if (!accounts.remove(account)) {
            throw new InvalidTransferException("Account is not held at " + bankName + ".");
        }
        accountByNumber.remove(account.getAccountNumber());
        List<Account> bucket = accountsByCustomerId.get(account.getOwner().getCustomerId());
        if (bucket != null) {
            bucket.remove(account);
            if (bucket.isEmpty()) {
                accountsByCustomerId.remove(account.getOwner().getCustomerId());
            }
        }
        account.getOwner().removeAccount(account);
        System.out.printf("[%s] Closed account %s.%n", bankName, account.getAccountNumber());
    }

    public int getAccountCount() {
        return accounts.size();
    }

    public int getRegisteredCustomerCount() {
        return registeredCustomerIds.size();
    }

    public Set<String> getRegisteredCustomerIdsView() {
        return Collections.unmodifiableSet(registeredCustomerIds);
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
        System.out.printf ("║  Unique customer IDs  : %-28d║%n", registeredCustomerIds.size());
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

    public static class DuplicateCustomerException extends Account.BankingException {
        public DuplicateCustomerException(String message) {
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
