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
        if (type.equalsIgnoreCase("transfer")){
            return
                    "transactionId: " + transactionId +
                            " | accountId: " + accountId +
                            " | amount: " + decimalFormat.format(amount) +
                            " | type: " + type +
                            " | accountIdTo: " + accountIdTo +
                            " | status: " + status +
                            " | date: " + date ;
        }
        return
        "transactionId: " + transactionId +
                " | accountId: " + accountId +
                " | amount: " + decimalFormat.format(amount) +
                " | type: " + type +
                " | status: " + status +
                " | date: " + date ;
    }

}
