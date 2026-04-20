public class Customer {
    private String name;
    private Account account;

    public Customer(String name, Account account) {
        this.name = name;
        this.account = account;
    }

    public void displayInfo() {
        System.out.println("Customer: " + name);
        System.out.println("Balance: " + account.getBalance());
    }

    public Account getAccount() {
        return account;
    }
}
