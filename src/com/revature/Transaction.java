package com.revature;

import java.text.DecimalFormat;

public class Transaction {
    private int transactionId;
    private int accountId;
    private double amount;
    private String type;
    private int accountIdTo;
    private String status;
    private String date;

    public Transaction() {

    }

    public Transaction(int transactionId, int accountId, double amount, String type, int accountIdToo, String status, String date) {
        this.transactionId = transactionId;
        this.accountId = accountId;
        this.amount = amount;
        this.type = type;
        this.accountIdTo = accountIdToo;
        this.status = status;
        this.date = date;
    }

    @Override
    public String toString() {
        DecimalFormat decimalFormat = new DecimalFormat("#.00");
        String leftAlightFormat = "| %12d | %10s | %15s | %10s | %13s | %19s | %10s |";
        if (type.equalsIgnoreCase("transfer")){
            return String.format(leftAlightFormat, transactionId, accountId, decimalFormat.format(amount), type, accountIdTo, status.equalsIgnoreCase("accepted") ? "\u001B[32m" + status + "\u001B[0m" : "\u001B[31m" + status + "\u001B[0m", date);
        }
        return String.format(leftAlightFormat, transactionId, accountId, decimalFormat.format(amount), type, "", status.equalsIgnoreCase("accepted") ? "\u001B[32m" + status + "\u001B[0m" : "\u001B[31m" + status + "\u001B[0m", date);
    }

}
