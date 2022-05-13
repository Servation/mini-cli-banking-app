package com.revature;

import java.sql.SQLException;
import java.util.List;

public interface BankingDao {
    void addUser(User user) throws SQLException;

    void addBankAccount(BankAccount bankAccount) throws SQLException;

    User getUserByLogin(String user, String password) throws SQLException;

    List<BankAccount> getUserBankAccount(int id) throws SQLException;

    void deposit(BankAccount bankAccount, double amount) throws SQLException;

    void withdraw(BankAccount bankAccount, double amount);

    void transfer(BankAccount bankAccountFrom, BankAccount bankAccountTo, double amount);

    List<Transaction> viewAllTransactions();

    List<Transaction> viewTransactionsByAccountId(int AccountId);

    Transaction viewTransactionById(int transactionId);

    BankAccount getBankAccountById(int bankAccountId) throws SQLException;

    boolean getBankAccountStatus(int account_id) throws SQLException;

    void approveAccount(int id);

    void rejectAccount(int id);

    List<String[]> getUserDetails();

}
