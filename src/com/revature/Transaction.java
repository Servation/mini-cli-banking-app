package com.revature;

import java.text.DecimalFormat;

public class Transaction {
    private final int transactionId;
    private final int accountId;
    private final double amount;
    private final String type;
    private final int accountIdTo;
    private final String status;
    private final String date;

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
        String leftAlightFormat = "| %12d | %10s | %15s | %10s | %13s | %10s | %10s |";
        if (type.equalsIgnoreCase("transfer")){
            return String.format(leftAlightFormat, transactionId, accountId, decimalFormat.format(amount), type, accountIdTo, status, date);
        }
        return String.format(leftAlightFormat, transactionId, accountId, decimalFormat.format(amount), type, "", status, date);
    }

}
