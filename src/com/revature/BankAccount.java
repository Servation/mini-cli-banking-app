package com.revature;

import java.text.DecimalFormat;

public class BankAccount {
    private int account_id;
    private int customer_id;
    private double balance;
    private String account_name;
    private String status;

    public BankAccount(int customer_id, String account_name) {
        this.customer_id = customer_id;
        this.account_name = account_name;
        this.balance = 0;
        this.status = "pending";
    }

    public BankAccount(int account_id, double balance) {
        this.account_id =account_id;
        this.balance = balance;
    }

    public BankAccount(int account_id, int customer_id, double balance, String account_name, String status) {
        this.account_id = account_id;
        this.customer_id = customer_id;
        this.balance = balance;
        this.account_name = account_name;
        this.status = status;
    }

    @Override
    public String toString() {
        DecimalFormat decimalFormat = new DecimalFormat("#.00");
        String leftAlightFormat = "| %12d | %15s | %15s | %19s |";
        return String.format(leftAlightFormat,account_id,account_name,decimalFormat.format(balance),status.equalsIgnoreCase("approved") ? "\u001B[32m" + status + "\u001B[0m" : "\u001B[31m" + status + "\u001B[0m");
    }

    public int getAccount_id() {
        return account_id;
    }

    public int getCustomer_id() {
        return customer_id;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public String getAccount_name() {
        return account_name;
    }

}
