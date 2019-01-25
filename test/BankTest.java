import bank.*;
import org.junit.jupiter.api.*;

import java.rmi.RemoteException;
import java.util.Date;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class BankTest {
    private static int accNum = 101;

    private static Bank bank;
    private static long sessionID = 0;
    private static Integer balance = null;

    private static Date startDate;

    @BeforeAll
    static void setUp() {
        startDate = new Date();
        try {
            bank = new Bank();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Test
    @Order(1)
    void invalidLogin() {
        Assertions.assertThrows(InvalidLogin.class, () -> bank.login("invalid", "user"));
    }

    @Test
    @Order(2)
    void login() {
        try {
            sessionID = bank.login("user", "pass");
        } catch (RemoteException | InvalidLogin e) {
            e.printStackTrace();
        }

        Assertions.assertNotEquals(0, sessionID);
        System.out.println("SessionID: " + sessionID);
    }

    @Test
    @Order(3)
    void inquiry() {
        try {
            balance = bank.inquiry(accNum, sessionID);
        } catch (RemoteException | InvalidAccount | InvalidSession e) {
            e.printStackTrace();
        }

        Assertions.assertNotNull(balance);
        System.out.println("Balance: " + balance);
        System.out.println("--------------------------------");
    }

    @Test
    @Order(4)
    void invalidSession() {
        Assertions.assertThrows(InvalidSession.class, () -> bank.deposit(accNum, 100, 0));
    }

    @Test
    @Order(5)
    void invalidAccount() {
        Assertions.assertThrows(InvalidAccount.class, () -> bank.deposit(0, 100, sessionID));
    }

    @Test
    @Order(6)
    void deposit() {
        int expectedNewBal = balance + 100;
        int actualNewBal = balance - 100; // Placeholder
        try {
            bank.deposit(accNum, 100, sessionID);
            actualNewBal = bank.inquiry(accNum, sessionID);
        } catch (RemoteException | InvalidSession | InvalidAccount e) {
            e.printStackTrace();
        }

        Assertions.assertEquals(expectedNewBal, actualNewBal);
        balance = actualNewBal;
        System.out.println("Deposit 100");
        System.out.println("New Balance: " + balance);
        System.out.println();
    }

    @Test
    @Order(7)
    void withdraw() {
        int expectedNewBal = balance - 100;
        int actualNewBal = balance + 100; // Placeholder
        try {
            bank.withdraw(accNum, 100, sessionID);
            actualNewBal = bank.inquiry(accNum, sessionID);
        } catch (RemoteException | InvalidSession | InvalidAccount e) {
            e.printStackTrace();
        }

        Assertions.assertEquals(expectedNewBal, actualNewBal);
        balance = actualNewBal;
        System.out.println("Withdraw 100");
        System.out.println("New Balance: " + balance);
        System.out.println();
    }

    @Test
    @Order(8)
    void getStatement() {
        Statement s = null;
        try {
            s = bank.getStatement(accNum, startDate, new Date(), sessionID);
        } catch (RemoteException | InvalidAccount | InvalidSession e) {
            e.printStackTrace();
        }

        Assertions.assertNotNull(s);
        Assertions.assertEquals(2, s.getTransactions().size());

        System.out.println(s.getTransactions().toString());
    }
}