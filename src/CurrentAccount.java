public class CurrentAccount extends Account {

    public CurrentAccount(double balance) {
        super(balance);
    }

    @Override
    public void deposit(double amount) {
        balance += amount;
        System.out.println("Deposited: " + amount);
    }

    @Override
    public void withdraw(double amount) {
        balance -= amount; // allows overdraft
        System.out.println("Withdrawn (overdraft allowed): " + amount);
    }
}