import bank.*;

import java.io.*;
import java.rmi.ConnectException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class Bank extends UnicastRemoteObject implements BankInterface {
    private final String accountFilename = "accounts.ser";
    private final String loginFilename = "logins.ser";
    private final int maxSessionTime = 5; // Session expiration in minutes
    private Map<Integer, Account> accounts; // Users accounts
    private Map<String, String> logins; // User / Pass key pair
    private Map<Long, Date> sessions;

    public Bank() throws RemoteException {
        loadAccounts();
        loadLogins();
        System.out.println();
        sessions = new HashMap<>();
        startSessionValidator();
    }

    /**
     * Authenticates user / pass, and generates unique sessionID stored in Map: sessions
     *
     * @return sessionID - expires after maxSessionTime, to be stored by client
     */
    @Override
    public long login(String username, String password) throws RemoteException, InvalidLogin {
        if (logins.containsKey(username) && logins.get(username).equals(password)) {
            long sessionID = ThreadLocalRandom.current().nextLong();
            sessions.put(sessionID, new Date());
            return sessionID;
        } else
            throw new InvalidLogin();
    }

    @Override
    public void deposit(int accountnum, int amount, long sessionID) throws RemoteException, InvalidAccount, InvalidSession {
        checkSession(sessionID);
        if (!accounts.containsKey(accountnum))
            throw new InvalidAccount("No Account with Number: " + accountnum + " Exists!");

        accounts.get(accountnum).deposit(amount);
        writeAccounts();
    }

    @Override
    public void withdraw(int accountnum, int amount, long sessionID) throws RemoteException, InvalidAccount, InvalidSession {
        checkSession(sessionID);
        if (!accounts.containsKey(accountnum))
            throw new InvalidAccount("No Account with Number: " + accountnum + " Exists!");

        accounts.get(accountnum).withdraw(amount);
        writeAccounts();
    }

    @Override
    public int inquiry(int accountnum, long sessionID) throws RemoteException, InvalidAccount, InvalidSession {
        checkSession(sessionID);
        if (!accounts.containsKey(accountnum))
            throw new InvalidAccount("No Account with Number: " + accountnum + " Exists!");

        return accounts.get(accountnum).getBalance();
    }

    @Override
    public Statement getStatement(int accountnum, Date from, Date to, long sessionID) throws RemoteException, InvalidAccount, InvalidSession {
        checkSession(sessionID);
        if (!accounts.containsKey(accountnum))
            throw new InvalidAccount("No Account with Number: " + accountnum + " Exists!");

        Account acc = accounts.get(accountnum);
        return new Statement(acc, from, to);
    }

    /**
     * This method checks if a sessionID exists and is not expired.
     * If a sessionID exists but is expired, it is removed
     *
     * @param sessionID - Any number of type long
     *                  throws InvalidSession error with msg depending on scenario
     */
    private void checkSession(long sessionID) throws InvalidSession {
        if (sessions.containsKey(sessionID)) {
            long currTime = new Date().getTime();

            // Check if session is less than set time in minutes
            if ((currTime - sessions.get(sessionID).getTime()) / (1000 * 60) < maxSessionTime) {
                sessions.put(sessionID, new Date()); // Reset expiration timer if session is valid
                return;
            } else {
                sessions.remove(sessionID);
                throw new InvalidSession("Session Expired. (>" + maxSessionTime + ")");
            }
        }
        throw new InvalidSession("No Session Exists.");
    }

    /**
     * This method periodically (every 10 seconds) checks for any expired sessionID
     * and removes it
     */
    private void startSessionValidator() {
        Timer t = new Timer();
        t.schedule(new TimerTask() {
            @Override
            public void run() {
                Iterator<Map.Entry<Long, Date>> it = sessions.entrySet().iterator();
                while (it.hasNext()) {
                    long currTime = new Date().getTime();
                    Map.Entry<Long, Date> pair = it.next();
                    Date date = pair.getValue();
                    if ((currTime - date.getTime()) / (1000d * 60d) > maxSessionTime) {
                        it.remove();
                    }
                }
            }
        }, 0, 1000 * 10);
    }

    private void loadAccounts() {
        ObjectInputStream ois = null;
        try {
            FileInputStream fis = new FileInputStream(accountFilename);
            ois = new ObjectInputStream(fis);
            accounts = (Map<Integer, Account>) ois.readObject();
            System.out.println("Successfully Loaded " + accounts.size() + " Account(s)");
        } catch (FileNotFoundException e) {
            createAccounts();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (ois != null) {
                try {
                    ois.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void writeAccounts() {
        ObjectOutputStream oos = null;
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(accountFilename);
            oos = new ObjectOutputStream(fos);
            oos.writeObject(accounts);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (oos != null) {
                try {
                    oos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void loadLogins() {
        ObjectInputStream ois = null;
        try {
            FileInputStream fis = new FileInputStream(loginFilename);
            ois = new ObjectInputStream(fis);
            logins = (Map<String, String>) ois.readObject();
            System.out.println("Successfully Loaded " + logins.size() + " Login(s)");
        } catch (FileNotFoundException e) {
            createLogins();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (ois != null) {
                try {
                    ois.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void writeLogins() {
        ObjectOutputStream oos = null;
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(loginFilename);
            oos = new ObjectOutputStream(fos);
            oos.writeObject(logins);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (oos != null) {
                try {
                    oos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Basic method to manually initialise a few accounts when file doesn't exists
     */
    private void createAccounts() {
        accounts = new HashMap<>();
        accounts.put(101, new Account(101, "Eoin"));
        accounts.put(102, new Account(102, "Matt"));
        accounts.put(103, new Account(103, "Adam"));
        accounts.put(104, new Account(104, "Edgars"));
        writeAccounts();
    }

    /**
     * Basic method to manually initialise a few logins when file doesn't exist
     */
    private void createLogins() {
        logins = new HashMap<>();
        logins.put("admin", "admin");
        logins.put("user", "pass");
        writeLogins();
    }

    public static void main(String[] args) throws Exception {
        // Add necessary permissions for file and network access
        System.setProperty("java.security.policy", "bank.policy");
        System.setSecurityManager(new SecurityManager());

        // Default RMI port
        int port = 1099;

        // Try read 1st param as integer port, else use default
        try {
            port = Integer.valueOf(args[0]);
        } catch (Exception ignored) {
            System.out.println("No port specified!\nUsing Default.\n");
        }

        Bank bank = new Bank();

        Registry registry = LocateRegistry.getRegistry(port);

        // Try connect to running registry, else create new registry
        try {
            // rebind to avoid already exists error, will bind if doesn't already exist
            registry.rebind("Bank", bank);
        } catch (ConnectException e) {
            registry = LocateRegistry.createRegistry(port);
            registry.rebind("Bank", bank);
        }

        System.out.println("Bank Server Started on Port: " + port);
    }
}
