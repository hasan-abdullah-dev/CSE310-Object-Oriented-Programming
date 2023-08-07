import java.util.*;
import java.time.LocalDate;

class BankAcoount {
    private int accountNumber;
    private String accountName;
    private double balance;
    private final List<Transaction> transactions;

    public BankAcoount(int accountNumber, String accountName) {
        this.accountNumber = accountNumber;
        this.accountName = accountName;
        this.balance = 0.0;
        this.transactions = new ArrayList<>();

    }

    public void deposit(double amount) {
        if(amount > 0) {
            balance += amount;
            transactions.add(new Transaction("Deposit", amount, balance));
            System.out.println("Deposit successful: $" + amount);
        }else{
            System.out.println("Invalid deposit amount!");
        }
    }

    public void withdraw(double amount) {
        if (amount > 0) {
            if((balance - 500) >= amount) {
                balance -= amount;
                transactions.add(new Transaction("Withdrawal", amount, balance));
                System.out.println("Withdrawal successful: " + amount);
            }else{
                System.out.println("Insufficient balance!");
            }
        }else{
            System.out.println("Invaid withdrawal amount!");
        }
    }

    public double getBalance() {
        return balance;
    }

    public void printStatement() {
        System.out.println("Statement for Account: " + accountNumber);
        System.out.println("Acount Name: " + accountName);
        System.out.println("Balance: " + balance);
        System.out.println("Transactions:");
        for (Transaction transaction : transactions) {
            System.out.println(transaction.toString());
        }
    }

}

class CheckingAccount extends BankAcoount {

    public CheckingAccount(int accountNumber, String accountName) {
        super(accountNumber, accountName);
    }
}

class SavingsAccount extends BankAcoount {
  
  public SavingsAccount(int accountNumber, String accountName) {
    super(accountNumber, accountName);

  }

}

class Transaction {
    private LocalDate date;
    private String type;
    private double amount;
    private double balanceAfterTransaction;

    public Transaction(String type, double amount, double balanceAfterTransaction) {
        this.date = LocalDate.now();
        this.type = type;
        this.amount = amount;
        this.balanceAfterTransaction = balanceAfterTransaction;
    } 

    @Override
    public String toString() {
        return date + " - " + type + ": " + amount + ", Balance: " + balanceAfterTransaction;
    }

}

public class BankingSystem {
    public static void main(String[] args) {
        CheckingAccount checkingAccount = new CheckingAccount(3412, "Tom");
        checkingAccount.deposit(1000);
        checkingAccount.withdraw(200);
        checkingAccount.withdraw(800);
        checkingAccount.printStatement();

        SavingsAccount savingsAccount = new SavingsAccount(5311, "Jerry");
        savingsAccount.deposit(5000);
        savingsAccount.withdraw(500);
        savingsAccount.withdraw(1000);
        savingsAccount.printStatement();
    }
}