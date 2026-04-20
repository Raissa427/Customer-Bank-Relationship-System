public class Bank {
    public static void main(String[] args) {

            Account savings = new SavingsAccount(1000);
            Account current = new CurrentAccount(500);

            Customer c1 = new Customer("Raissa", savings);
            Customer c2 = new Customer("John", current);

            c1.getAccount().deposit(200);
            c1.getAccount().withdraw(150);

            c2.getAccount().withdraw(600);

            c1.displayInfo();
            c2.displayInfo();
        }
    }
