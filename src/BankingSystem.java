/**
 * ═══════════════════════════════════════════════════════════════════
 *  BankingSystem.java  —  Main driver / simulation
 *
 *  Exception handling uses only types declared inside the seven model
 *  classes (nested static classes extending {@link Account.BankingException}),
 *  plus standard unchecked types such as {@link NumberFormatException}.
 *
 *  Demonstrates: try / multi-catch, validation before throws, meaningful
 *  messages, and controlled execution (no crash).
 * ═══════════════════════════════════════════════════════════════════
 */
public class BankingSystem {

    public static void main(String[] args) {

        separator("SETTING UP BANKS");

        Bank equity = new Bank("Equity Bank", "EQB");
        Bank kcb    = new Bank("KCB Bank",    "KCB");

        separator("REGISTERING CUSTOMERS");

        Customer raissa = new Customer("C-001", "Raissa Uwera",   "raissa@email.com");
        Customer john   = new Customer("C-002", "John Mugisha",   "john@email.com");
        Customer amina  = new Customer("C-003", "Amina Kamara",   "amina@email.com");

        equity.registerCustomer(raissa);
        equity.registerCustomer(john);
        kcb.registerCustomer(amina);
        kcb.registerCustomer(raissa);

        separator("OPENING ACCOUNTS");

        SavingsAccount raissaSavings = equity.openSavingsAccount(raissa, 1_500.00);
        CurrentAccount raissaCurrent = kcb.openCurrentAccount(raissa, 500.00, 300.00);

        CurrentAccount johnCurrent = equity.openCurrentAccount(john, 800.00, 200.00);

        SavingsAccount aminaSavings = kcb.openSavingsAccount(amina, 2_000.00);
        CurrentAccount aminaCurrent = kcb.openCurrentAccount(amina, 300.00, 500.00);

        // ── SCENARIO 1: deposits / withdrawals + multiple catch (specific → general) ─
        separator("SCENARIO 1 — Withdrawals and nested account exceptions");

        try {
            raissaSavings.deposit(300.00);
            raissaSavings.withdraw(200.00);
            raissaSavings.withdraw(200.00);
            raissaSavings.withdraw(200.00);
            raissaSavings.withdraw(50.00); // 4th withdrawal — exceeds monthly limit
        } catch (Account.WithdrawalLimitExceededException e) {
            System.out.println("  [USER FEEDBACK] Withdrawal cap: " + e.getMessage());
        } catch (Account.InsufficientFundsException e) {
            System.out.println("  [USER FEEDBACK] Not enough funds: " + e.getMessage());
        } catch (Account.AccountRuleException e) {
            System.out.println("  [USER FEEDBACK] Account rule: " + e.getMessage());
        } catch (Account.InvalidAmountException e) {
            System.out.println("  [USER FEEDBACK] Bad amount: " + e.getMessage());
        } catch (Account.BankingException e) {
            System.out.println("  [USER FEEDBACK] Banking: " + e.getMessage());
        }

        System.out.println();
        try {
            johnCurrent.deposit(100.00);
            johnCurrent.withdraw(950.00);
            johnCurrent.withdraw(600.00);
        } catch (Account.OverdraftLimitExceededException e) {
            System.out.println("  [USER FEEDBACK] Overdraft policy: " + e.getMessage());
        } catch (Account.InsufficientFundsException e) {
            System.out.println("  [USER FEEDBACK] " + e.getMessage());
        } catch (Account.AccountRuleException e) {
            System.out.println("  [USER FEEDBACK] " + e.getMessage());
        } catch (Account.InvalidAmountException e) {
            System.out.println("  [USER FEEDBACK] " + e.getMessage());
        } catch (Account.BankingException e) {
            System.out.println("  [USER FEEDBACK] " + e.getMessage());
        }

        // ── SCENARIO 2: transfers — bank nested exceptions + account rule exceptions ─
        separator("SCENARIO 2 — Transfers (Bank.try/catch inside transfer + main catches)");

        try {
            kcb.transfer(aminaCurrent, aminaSavings, 150.00);
        } catch (Bank.InvalidTransferException e) {
            System.out.println("  [USER FEEDBACK] Transfer invalid: " + e.getMessage());
        } catch (Account.OverdraftLimitExceededException e) {
            System.out.println("  [USER FEEDBACK] " + e.getMessage());
        } catch (Account.InsufficientFundsException e) {
            System.out.println("  [USER FEEDBACK] " + e.getMessage());
        } catch (Account.AccountRuleException e) {
            System.out.println("  [USER FEEDBACK] " + e.getMessage());
        } catch (Account.BankingException e) {
            System.out.println("  [USER FEEDBACK] " + e.getMessage());
        }

        try {
            kcb.transfer(aminaCurrent, raissaCurrent, 700.00);
        } catch (Bank.InvalidTransferException e) {
            System.out.println("  [USER FEEDBACK] " + e.getMessage());
        } catch (Account.OverdraftLimitExceededException e) {
            System.out.println("  [USER FEEDBACK] Transfer blocked: " + e.getMessage());
        } catch (Account.AccountRuleException e) {
            System.out.println("  [USER FEEDBACK] " + e.getMessage());
        } catch (Account.BankingException e) {
            System.out.println("  [USER FEEDBACK] " + e.getMessage());
        }

        // ── SCENARIO 3: Monthly interest (try/catch inside SavingsAccount) ───────────
        separator("SCENARIO 3 — Monthly Interest");

        raissaSavings.applyMonthlyInterest();
        aminaSavings.applyMonthlyInterest();

        // ── SCENARIO 4: Polymorphism + per-account handling ───────────────────────────
        separator("SCENARIO 4 — Polymorphism with isolated try/catch");

        Account[] accountsToWithdraw = { raissaSavings, raissaCurrent, johnCurrent };
        for (Account acc : accountsToWithdraw) {
            System.out.println("  Trying to withdraw $100 from " + acc.getAccountType() + ":");
            try {
                acc.withdraw(100.00);
            } catch (Account.WithdrawalLimitExceededException e) {
                System.out.println("    → Handled: " + e.getMessage());
            } catch (Account.InsufficientFundsException e) {
                System.out.println("    → Handled: " + e.getMessage());
            } catch (Account.OverdraftLimitExceededException e) {
                System.out.println("    → Handled: " + e.getMessage());
            } catch (Account.AccountRuleException e) {
                System.out.println("    → Handled: " + e.getMessage());
            } catch (Account.InvalidAmountException e) {
                System.out.println("    → Handled: " + e.getMessage());
            }
        }

        // ── BONUS: validation + customer / transaction nested types ───────────────────
        separator("BONUS — Customer, transfer, amount, and JDK unchecked");

        try {
            new Customer("C-BAD", "Bad Email User", "not-an-email");
        } catch (Customer.InvalidCustomerDataException e) {
            System.out.println("  [HANDLED] " + e.getMessage());
        } catch (Account.BankingException e) {
            System.out.println("  [HANDLED] " + e.getMessage());
        }

        Customer ghost = new Customer("C-999", "Ghost User", "ghost@email.com");
        try {
            equity.openSavingsAccount(ghost, 100.00);
        } catch (Bank.CustomerNotRegisteredException e) {
            System.out.println("  [HANDLED] " + e.getMessage());
        } catch (Account.BankingException e) {
            System.out.println("  [HANDLED] " + e.getMessage());
        }

        try {
            raissaSavings.deposit(-10.00);
        } catch (Account.InvalidAmountException e) {
            System.out.println("  [HANDLED] " + e.getMessage());
        }

        try {
            equity.transfer(raissaSavings, raissaCurrent, 50.00);
        } catch (Bank.InvalidTransferException e) {
            System.out.println("  [HANDLED] " + e.getMessage());
        } catch (Account.BankingException e) {
            System.out.println("  [HANDLED] " + e.getMessage());
        }

        try {
            new Transaction("", 1.0, 1.0);
        } catch (Transaction.InvalidTransactionDataException e) {
            System.out.println("  [HANDLED] Transaction validation: " + e.getMessage());
        } catch (Account.BankingException e) {
            System.out.println("  [HANDLED] " + e.getMessage());
        }

        try {
            Integer.parseInt("not-a-number");
        } catch (NumberFormatException e) {
            System.out.println("  [HANDLED] NumberFormatException: " + e.getMessage());
        }

        // ── Final reports ─────────────────────────────────────────────────────────────
        separator("BANK SUMMARIES");
        equity.printBankSummary();
        kcb.printBankSummary();

        separator("CUSTOMER PROFILES");
        raissa.displayInfo();
        john.displayInfo();
        amina.displayInfo();

        separator("ACCOUNT STATEMENTS");
        System.out.println("  — Raissa's Savings Account Statement —");
        raissaSavings.printStatement();
        System.out.println();
        System.out.println("  — John's Current Account Statement —");
        johnCurrent.printStatement();

        separator("END OF RUN");
        System.out.println("  Program completed without an unhandled crash.");
    }

    private static void separator(String title) {
        System.out.printf("%n══════════════ %s ══════════════%n", title);
    }
}
