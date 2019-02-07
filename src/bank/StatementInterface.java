package bank;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public interface StatementInterface extends Serializable {
    int getAccountNum();  // returns account number associated with this statement

    Date getStartDate(); // returns start Date of Statement

    Date getEndDate(); // returns end Date of Statement

    String getAccountName(); // returns name of account holder

    List getTransactions(); // returns list of bank.Transaction objects that encapsulate details about each transaction
}