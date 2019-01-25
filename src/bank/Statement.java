package bank;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Statement implements StatementInterface {
    private int accountNum;
    private String accountName;
    private Date startDate;
    private Date endDate;
    private List<Transaction> transactions;

    public Statement(Account acc, Date startDate, Date endDate) {
        this.accountNum = acc.getAccountNum();
        this.accountName = acc.getAccountName();
        this.startDate = startDate;
        this.endDate = endDate;

        this.transactions = findTransactionsWithinDate(acc.getTransactions(), startDate, endDate);
    }

    /**
     * This method searches through all transactions to find those between start and end dates
     * @return valid list of transactions
     */
    private List<Transaction> findTransactionsWithinDate(@NotNull List<Transaction> transactions, Date startDate, Date endDate) {
        List<Transaction> validTransactions = new ArrayList<>();
        for (Transaction t : transactions) {
            Date tDate = t.getDate();
            if (startDate.before(tDate) && endDate.after(tDate))
                validTransactions.add(t);
        }
        return validTransactions;
    }

    @Override
    public int getAccountNum() {
        return accountNum;
    }

    @Override
    public Date getStartDate() {
        return startDate;
    }

    @Override
    public Date getEndDate() {
        return endDate;
    }

    @Override
    public String getAccountName() {
        return accountName;
    }

    @Override
    public List getTransactions() {
        return transactions;
    }
}
