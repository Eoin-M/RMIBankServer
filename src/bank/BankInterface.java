package bank;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Date;

public interface BankInterface extends Remote
{
	long login(String username, String password) throws RemoteException, InvalidLogin;
	void deposit(int accountnum, int amount, long sessionID) throws RemoteException, InvalidAccount, InvalidSession;
	void withdraw(int accountnum, int amount, long sessionID) throws RemoteException, InvalidAccount, InvalidSession;
	int inquiry(int accountnum, long sessionID) throws RemoteException, InvalidAccount, InvalidSession;
	Statement getStatement(int accountnum, Date from, Date to, long sessionID) throws RemoteException, InvalidAccount, InvalidSession;
}