package bank;

import java.text.SimpleDateFormat;
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
     *
     * @return valid list of transactions
     */
    private List<Transaction> findTransactionsWithinDate(List<Transaction> transactions, Date startDate, Date endDate) {
        List<Transaction> validTransactions = new ArrayList<>();
        for (Transaction t : transactions) {
            Date tDate = t.getDate();
            if (startDate.before(tDate) && endDate.after(tDate))
                validTransactions.add(t);
        }
        return validTransactions;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        sb.append("Statement for ").append(accountName).append("\'s").append(" (").append(accountNum).append(") account\n");
        sb.append(dateFormat.format(startDate)).append(" - ").append(dateFormat.format(endDate)).append("\n");
        sb.append("Date: \t\t| Amount: \t\t| Balance: \n");
        sb.append("------------------------------------------------------------------------------------------\n");
        for (Transaction t : transactions)
            sb.append(t).append("\n");
        sb.append("------------------------------------------------------------------------------------------\n");
        return sb.toString();
    }

    public int getAccountNum() {
        return accountNum;
    }

    public Date getStartDate() {
        return startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public String getAccountName() {
        return accountName;
    }

    public List getTransactions() {
        return transactions;
    }
}
