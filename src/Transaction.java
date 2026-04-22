public class Transaction {

    private final String type;
    private final double amount;
    private final double balanceAfter;
    private final java.time.LocalDateTime timestamp;

    public Transaction(String type, double amount, double balanceAfter) {
        if (type == null || type.isBlank()) {
            throw new InvalidTransactionDataException("Transaction type is required.");
        }
        if (Double.isNaN(amount) || Double.isInfinite(amount)) {
            throw new InvalidTransactionDataException("Transaction amount must be a finite number.");
        }
        if (Double.isNaN(balanceAfter) || Double.isInfinite(balanceAfter)) {
            throw new InvalidTransactionDataException("Balance after transaction must be a finite number.");
        }
        this.type         = type.trim();
        this.amount       = amount;
        this.balanceAfter = balanceAfter;
        this.timestamp    = java.time.LocalDateTime.now();
    }

    public String                   getType()         { return type;         }
    public double                   getAmount()       { return amount;       }
    public double                   getBalanceAfter() { return balanceAfter; }
    public java.time.LocalDateTime  getTimestamp()    { return timestamp;    }

    @Override
    public String toString() {
        return String.format("[%s] %s  $%.2f  (balance: $%.2f)",
                timestamp.toLocalTime(), type, amount, balanceAfter);
    }

    public static class InvalidTransactionDataException extends Account.BankingException {
        public InvalidTransactionDataException(String message) {
            super(message);
        }
    }
}
