import bank.*;

import java.io.*;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ATM
{
	static String sessionFileName = "session.txt";
	static long sessionID = 0;

	public static void main(String[] args)
	{
		String operation = null, username = null, password = null;
		int accountNumber = -1, amount = 0;
		Date startDate = null, endDate = null;
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		String hostname = "localhost";
		int port = 1099;

		readSession();

		try {
			hostname = args[0];
			port = Integer.valueOf(args[1]);
			operation = args[2];
			switch(operation) {
				case "login":
					username = args[3];
					password = args[4];
					break;
				case "deposit":
					accountNumber = Integer.valueOf(args[3]);
					amount = Integer.valueOf(args[4]);
					break;
				case "withdraw":
					accountNumber = Integer.valueOf(args[3]);
					amount = Integer.valueOf(args[4]);
					break;
				case "statement":
					accountNumber = Integer.valueOf(args[3]);
					startDate = dateFormat.parse(args[4]);
					endDate = dateFormat.parse(args[5]);
					break;
				case "inquiry":
					accountNumber = Integer.valueOf(args[3]);
					break;
				default:
					System.out.println("Error. " + operation + " is not a valid option.");
					System.exit(1);
			}
		} catch(Exception ignored) {
			System.out.println("Parameter Missing or Incorrect!");
			System.exit(1);
		}

		try {
			//Get Registry
			Registry registry = LocateRegistry.getRegistry(hostname, port);

			BankInterface bank = (BankInterface) registry.lookup("Bank");

			switch(operation) {
				case "login":
					try {
						sessionID = bank.login(username, password);
						System.out.println("Successful login for user " + username);
						System.out.println("Session is valid for 5 minutes");
					} catch(InvalidLogin e) {
						e.printStackTrace();
					}
					break;
				case "deposit":
					try {
						bank.deposit(accountNumber, amount, sessionID);
						System.out.println("Successfully deposited €" + amount + " to account " + accountNumber);
					} catch(InvalidAccount | InvalidSession e) {
						e.printStackTrace();
					}
					break;
				case "withdraw":
					try {
						bank.withdraw(accountNumber, amount, sessionID);
						System.out.println("Successfully withdrew €" + amount + " from account " + accountNumber);
					} catch(InvalidAccount | InvalidSession e) {
						e.printStackTrace();
					}
					break;
				case "statement":
					try {
						StatementInterface s = bank.getStatement(accountNumber, startDate, endDate, sessionID);
						System.out.println(s);
					} catch(InvalidAccount | InvalidSession e) {
						e.printStackTrace();
					}
					break;
				case "inquiry":
					try {
						System.out.println(sessionID);
						int balance = bank.inquiry(accountNumber, sessionID);
						System.out.println("The current balance of account " + accountNumber + " is €" + balance);
					} catch(InvalidAccount | InvalidSession e) {
						e.printStackTrace();
					}
					break;
				default:
					System.out.println("Error. " + operation + " is not a valid option.");
					System.exit(1);
			}
			writeSession();


		} catch(RemoteException | NotBoundException e) {
			e.printStackTrace();
		}
	}

	static void writeSession()
	{
		try {
			Writer writer = new FileWriter(sessionFileName);
			writer.write(String.valueOf(sessionID));
			writer.close();
		} catch(IOException e) {
			e.printStackTrace();
		}
	}

	static void readSession()
	{
		try {
			FileReader reader = new FileReader(sessionFileName);
			BufferedReader br = new BufferedReader(reader);
			sessionID = Long.parseLong(br.readLine());
			reader.close();
		} catch(IOException e) {
			e.printStackTrace();
		}
	}

}
