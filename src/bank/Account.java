package bank;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Account implements Serializable
{
	private int accountNum;
	private String accountName;
	private int balance;
	private List<Transaction> transactions;

	public Account(int accnum, String accname)
	{
		this.accountNum = accnum;
		this.accountName = accname;
		this.balance = 0;
		this.transactions = new ArrayList<>();
	}

	public void deposit(int amount)
	{
		balance += amount;
		transactions.add(new Transaction(amount, balance));
	}

	public void withdraw(int amount)
	{
		balance -= amount;
		transactions.add(new Transaction(-amount, balance));
	}

	public int getAccountNum()
	{
		return accountNum;
	}
	public String getAccountName()
	{
		return accountName;
	}
	public int getBalance()
	{
		return balance;
	}
	public List<Transaction> getTransactions()
	{
		return transactions;
	}
}
