public class BankingSystem {

    public static void main(String[] args) {

        sep("SETUP: BANKS, CUSTOMERS, ACCOUNTS");

        Bank equity = new Bank("Equity Bank", "EQB");
        Bank kcb    = new Bank("KCB Bank",    "KCB");

        Customer raissa = new Customer("C-001", "Raissa Uwera", "raissa@email.com");
        Customer john   = new Customer("C-002", "John Mugisha",  "john@email.com");
        Customer amina  = new Customer("C-003", "Amina Kamara",  "amina@email.com");

        equity.registerCustomer(raissa);
        equity.registerCustomer(john);
        kcb.registerCustomer(amina);
        kcb.registerCustomer(raissa);

        SavingsAccount raissaSavings = equity.openSavingsAccount(raissa, 1_500.00);
        CurrentAccount raissaCurrent = kcb.openCurrentAccount(raissa,     500.00, 300.00);
        CurrentAccount johnCurrent   = equity.openCurrentAccount(john,     800.00, 200.00);
        SavingsAccount aminaSavings  = kcb.openSavingsAccount(amina,     2_000.00);
        CurrentAccount aminaCurrent  = kcb.openCurrentAccount(amina,       300.00, 500.00);

        sep("SCENARIO 1 -- Invalid Amount");

        System.out.println("Attempting to deposit $0 (invalid):");
        raissaSavings.deposit(0);

        System.out.println("\nAttempting to deposit -$50 (negative):");
        raissaSavings.deposit(-50);

        System.out.println("\nAttempting to withdraw $0:");
        raissaSavings.withdraw(0);

        System.out.println("\nAttempting a $75,000 single deposit (exceeds $50,000 limit):");
        johnCurrent.deposit(75_000);

        sep("SCENARIO 2 -- Insufficient Funds");

        System.out.println("Raissa savings balance: $" + raissaSavings.getBalance());
        System.out.println("Attempting to withdraw $2,000 (more than balance):");
        raissaSavings.withdraw(2_000);

        sep("SCENARIO 3 -- Withdrawal Limit");

        System.out.println("Making 3 valid withdrawals from Raissa's savings:");
        raissaSavings.withdraw(100);
        raissaSavings.withdraw(100);
        raissaSavings.withdraw(100);

        System.out.println("\nAttempting a 4th withdrawal (limit = 3/month):");
        raissaSavings.withdraw(50);

        sep("SCENARIO 4 -- Overdraft Limit Exceeded");

        System.out.println("John current balance: $" + johnCurrent.getBalance());
        System.out.println("Withdrawing $950 (goes into overdraft, limit is $200):");
        johnCurrent.withdraw(950);

        System.out.println("\nAttempting to withdraw $300 more (would exceed overdraft limit):");
        johnCurrent.withdraw(300);

        sep("SCENARIO 5 -- Duplicate Customer");

        System.out.println("Trying to register Raissa at Equity Bank again:");
        equity.registerCustomer(raissa);

        sep("SCENARIO 6 -- Customer Not Registered");

        System.out.println("John tries to open an account at KCB (not registered there):");
        kcb.openSavingsAccount(john, 500);

        sep("SCENARIO 7 -- Account Not Found");

        System.out.println("Searching for account 'EQB-SA-9999' (does not exist):");
        equity.findAccount("EQB-SA-9999");

        System.out.println("\nRaissa looks up account 'KCB-CA-FAKE' in her profile:");
        raissa.getAccount("KCB-CA-FAKE");

        sep("SCENARIO 8 -- Blank Customer Name / ID");

        System.out.println("Creating a customer with an empty name:");
        new Customer("C-999", "", "blank@email.com");

        System.out.println("\nCreating a customer with a blank ID:");
        new Customer("   ", "Ghost User", "ghost@email.com");

        sep("SCENARIO 9 -- Transfers");

        System.out.println("Valid transfer: Amina moves $100 from current -> savings:");
        kcb.transfer(aminaCurrent, aminaSavings, 100);

        System.out.println("\nFailing transfer: Amina's current can't cover $1,000 (overdraft exceeded):");
        kcb.transfer(aminaCurrent, aminaSavings, 1_000);

        System.out.println("\nFailing transfer: invalid amount of $0:");
        kcb.transfer(aminaSavings, aminaCurrent, 0);

        sep("SCENARIO 10 -- System Still Runs Normally");

        raissaSavings.applyMonthlyInterest();
        aminaSavings.applyMonthlyInterest();
        equity.printBankSummary();
        kcb.printBankSummary();

        sep("ACCOUNT STATEMENTS");
        System.out.println("  -- Raissa Savings --");
        raissaSavings.printStatement();
        System.out.println("\n  -- John Current --");
        johnCurrent.printStatement();
    }

    private static void sep(String title) {
        System.out.printf("%n============================================================%n");
        System.out.printf("  %s%n", title);
        System.out.printf("============================================================%n");
    }
}
