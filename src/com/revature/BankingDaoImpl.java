package com.revature;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BankingDaoImpl implements BankingDao {
    final private String ANSI_BLUE = "\u001B[34m";
    final private String ANSI_YELLOW = "\u001B[33m";
    final private String ANSI_RESET = "\u001B[0m";
    Connection connection;

    public BankingDaoImpl() {
        this.connection = ConnectionFactory.getConnection();
    }

    @Override
    public void addUser(User user) throws SQLException {
        String sql = "INSERT INTO user (username, user_password, first_name, last_name, email) " +
                "VALUES (?, ?, ?, ?, ?)";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setString(1, user.getUsername());
        preparedStatement.setString(2, user.getPassword());
        preparedStatement.setString(3, user.getFirstName());
        preparedStatement.setString(4, user.getLastName());
        preparedStatement.setString(5, user.getEmail());
        int updates = preparedStatement.executeUpdate();
        if (updates > 0) {
            System.out.println(ANSI_BLUE + "User account created successfully" + ANSI_RESET);
        } else {
            System.out.println(ANSI_YELLOW + "Oops! There seems to be a problem..." + ANSI_RESET);
        }
    }

    @Override
    public void addBankAccount(BankAccount bankAccount) throws SQLException {
        String sql = "INSERT INTO bank_account (account_name, user_id) VALUES (?, ?)";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setString(1, bankAccount.getAccount_name());
        preparedStatement.setInt(2, bankAccount.getCustomer_id());
        int updates = preparedStatement.executeUpdate();
        if (updates > 0) {
            System.out.println(ANSI_BLUE + "Bank account created successfully. Please wait for approval" + ANSI_RESET);
        } else {
            System.out.println(ANSI_YELLOW + "Oops! There seems to be a problem..." + ANSI_RESET);
        }
    }

    @Override
    public List<BankAccount> getUserBankAccount(int id) throws SQLException {
        String sql = "SELECT * FROM bank_account WHERE user_id =" + id;
        List<BankAccount> accounts = new ArrayList<>();
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(sql);
        while (resultSet.next()) {
            int accountID = resultSet.getInt(1);
            String accountName = resultSet.getString(2);
            String status = resultSet.getString(4);
            double balance = resultSet.getDouble(5);
            accounts.add(new BankAccount(accountID, id, balance, accountName, status));
        }
        return accounts;
    }

    @Override
    public User getUserByLogin(String user, String password) throws SQLException {
        String sql = "SELECT user_id, username, first_name, last_name, email, employee FROM user WHERE username ='" + user + "' AND user_password='" + password + "'";
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(sql);
        if (resultSet.next()) {
            int id = resultSet.getInt(1);
            String username = resultSet.getString(2);
            String firstname = resultSet.getString(3);
            String lastname = resultSet.getString(4);
            String email = resultSet.getString(5);
            boolean employee = resultSet.getBoolean(6);
            return new User(username, firstname, lastname, email, id, employee);
        }
        return null;
    }


    @Override
    public void deposit(BankAccount bankAccount, double amount) throws SQLException {
        String sql = "UPDATE bank_account set balance=? WHERE account_id=?";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setDouble(1, bankAccount.getBalance() + amount);
        preparedStatement.setInt(2, bankAccount.getAccount_id());
        int result = preparedStatement.executeUpdate();
        if (result > 0) {
            bankAccount.setBalance(bankAccount.getBalance() + amount);
            System.out.println(ANSI_BLUE+"Successfully deposited: " + amount + ANSI_RESET);
            transactionRecorder(bankAccount, amount, "deposit", "accepted");
        } else {
            System.out.println(ANSI_YELLOW + "Could not process your deposit."+ANSI_RESET);
            transactionRecorder(bankAccount, amount, "deposit", "rejected");
        }
    }

    private void transactionRecorder(BankAccount bankAccount, double amount, String status, BankAccount bankAccountTo) {
        String sql = "INSERT INTO transactions (account_id, amount, type, status, account_id_to) VALUES (?,?,?,?,?)";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, bankAccount.getAccount_id());
            preparedStatement.setDouble(2, amount);
            preparedStatement.setString(3, "transfer");
            preparedStatement.setString(4, status);
            preparedStatement.setInt(5, bankAccountTo.getAccount_id());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println(ANSI_YELLOW + "Could not record transaction" + ANSI_RESET);
        }
    }

    private void transactionRecorder(BankAccount bankAccount, double amount, String type, String status) {
        String sql = "{CALL insert_transaction(?, ?, ?, ?)}";
        try {
            CallableStatement statement = connection.prepareCall(sql);
            statement.setInt(1, bankAccount.getAccount_id());
            statement.setDouble(2, amount);
            statement.setString(3, type);
            statement.setString(4, status);
            statement.execute();
        } catch (SQLException e) {
            System.out.println(ANSI_YELLOW + "Could not record transaction" + ANSI_RESET);
        }
    }

    @Override
    public void withdraw(BankAccount bankAccount, double amount) {
        try {
            if (amount > bankAccount.getBalance()) {
                transactionRecorder(bankAccount, amount, "withdraw", "rejected");
                System.out.println(ANSI_YELLOW + "Not enough funds in balance." + ANSI_RESET);
                return;
            }
            String sql = "UPDATE bank_account set balance=? WHERE account_id=?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setDouble(1, bankAccount.getBalance() - amount);
            preparedStatement.setInt(2, bankAccount.getAccount_id());
            int result = preparedStatement.executeUpdate();
            if (result > 0) {
                bankAccount.setBalance(bankAccount.getBalance() - amount);
                System.out.println(ANSI_BLUE + "Successful withdrawal: " + amount + ANSI_RESET);
                transactionRecorder(bankAccount, amount, "withdraw", "accepted");
            } else {
                System.out.println(ANSI_YELLOW + "Could not process your withdrawal." + ANSI_RESET);
                transactionRecorder(bankAccount, amount, "withdraw", "rejected");
            }
        } catch (Exception e) {
            System.out.println(ANSI_YELLOW + "Could not process your withdrawal." + ANSI_RESET);
            transactionRecorder(bankAccount, amount, "withdraw", "rejected");
        }
    }

    @Override
    public void transfer(BankAccount bankAccountFrom, BankAccount bankAccountTo, double amount) {
        try {
            if (!getBankAccountStatus(bankAccountTo.getAccount_id()) || amount > bankAccountFrom.getBalance() || bankAccountFrom.getAccount_id() == bankAccountTo.getAccount_id()) {
                transactionRecorder(bankAccountFrom, amount, "rejected", bankAccountTo);
                System.out.println(ANSI_YELLOW + "Could not transfer to account: " + bankAccountTo.getAccount_id() + ANSI_RESET);
                return;
            }
            String sql = "UPDATE bank_account set balance=? WHERE account_id=?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setDouble(1, bankAccountFrom.getBalance() - amount);
            preparedStatement.setInt(2, bankAccountFrom.getAccount_id());
            int first = preparedStatement.executeUpdate();
            sql = "UPDATE bank_account set balance=? WHERE account_id=?";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setDouble(1, bankAccountTo.getBalance() + amount);
            preparedStatement.setInt(2, bankAccountTo.getAccount_id());
            int second = preparedStatement.executeUpdate();
            int result = first > 0 && second > 0 ? 1 : 0;
            if (result > 0) {
                bankAccountFrom.setBalance(bankAccountFrom.getBalance() - amount);
                bankAccountTo.setBalance(bankAccountTo.getBalance() + amount);
                System.out.println(ANSI_BLUE + "Successfully transfer: " + amount + ANSI_RESET);
                transactionRecorder(bankAccountFrom, amount, "accepted", bankAccountTo);
            } else {
                System.out.println(ANSI_YELLOW + "Could not process your transfer." + ANSI_RESET);
                transactionRecorder(bankAccountFrom, amount, "rejected", bankAccountTo);
            }
        } catch (Exception e) {
            System.out.println(ANSI_YELLOW + "Could not process your transfer." + ANSI_RESET);
            transactionRecorder(bankAccountFrom, amount, "rejected", bankAccountTo);
        }
    }

    @Override
    public List<Transaction> viewAllTransactions() {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT * FROM transactions";
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                int transactionId = resultSet.getInt(1);
                int accountId = resultSet.getInt(2);
                double amount = resultSet.getDouble(3);
                String type = resultSet.getString(4);
                int accountIdToo = resultSet.getInt(5);
                String status = resultSet.getString(6);
                String date = resultSet.getString(7);
                transactions.add(new Transaction(transactionId, accountId, amount, type, accountIdToo, status, date));
            }
        } catch (SQLException e) {
            System.out.println(ANSI_YELLOW + "Could not find transactions." + ANSI_RESET);
        }
        return transactions;
    }

    @Override
    public List<Transaction> viewTransactionsByAccountId(int AccountId) {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT * FROM transactions WHERE account_id=" + AccountId;
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                int transactionId = resultSet.getInt(1);
                int accountId = resultSet.getInt(2);
                double amount = resultSet.getDouble(3);
                String type = resultSet.getString(4);
                int accountIdToo = resultSet.getInt(5);
                String status = resultSet.getString(6);
                String date = resultSet.getString(7);
                transactions.add(new Transaction(transactionId, accountId, amount, type, accountIdToo, status, date));
            }
        } catch (SQLException e) {
            System.out.println(ANSI_YELLOW + "Could not find transactions." + ANSI_RESET);
        }
        return transactions;
    }

    @Override
    public Transaction viewTransactionById(int id) {
        String sql = "SELECT * FROM transactions WHERE transaction_id=" + id;
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            if (resultSet.next()) {
                int transactionId = resultSet.getInt(1);
                int accountId = resultSet.getInt(2);
                double amount = resultSet.getDouble(3);
                String type = resultSet.getString(4);
                int accountIdToo = resultSet.getInt(5);
                String status = resultSet.getString(6);
                String date = resultSet.getString(7);
                return new Transaction(transactionId, accountId, amount, type, accountIdToo, status, date);
            }
        } catch (SQLException e) {
            System.out.println(ANSI_YELLOW + "Could not find transactions." + ANSI_RESET);
        }
        return new Transaction();
    }

    @Override
    public boolean getBankAccountStatus(int account_id) throws SQLException {
        String sql = "SELECT status FROM bank_account where account_id=" + account_id;
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(sql);
        if (resultSet.next()) {
            String result = resultSet.getString(1);
            return result.equalsIgnoreCase("approved");
        }
        return false;
    }

    @Override
    public BankAccount getBankAccountById(int bankAccountId) throws SQLException {
        String sql = "SELECT balance FROM bank_account where account_id=" + bankAccountId;
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(sql);
        if (resultSet.next()) {
            return new BankAccount(bankAccountId, resultSet.getDouble(1));
        }
        return null;
    }

    @Override
    public void approveAccount(int id) {
        String sql = "UPDATE bank_account SET status='approved' WHERE account_id=" + id;
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            int result = preparedStatement.executeUpdate();
            if (result > 0) {
                System.out.println(ANSI_BLUE+"Account updated."+ANSI_RESET);
            } else {
                System.out.println(ANSI_YELLOW+"Account could not update."+ANSI_RESET);
            }
        } catch (SQLException e) {
            System.out.println(ANSI_YELLOW+"Could not access account."+ANSI_RESET);
        }

    }

    @Override
    public void rejectAccount(int id) {
        String sql = "UPDATE bank_account SET status='rejected' WHERE account_id=" + id;
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            int result = preparedStatement.executeUpdate();
            if (result > 0) {
                System.out.println(ANSI_BLUE+"Account updated."+ANSI_RESET);
            } else {
                System.out.println(ANSI_YELLOW+"Account could not update."+ANSI_RESET);
            }
        } catch (SQLException e) {
            System.out.println(ANSI_YELLOW+"Could not access account."+ANSI_RESET);
        }
    }

    @Override
    public List<String[]> getUserDetails() {
        List<String[]> userInfo = new ArrayList<>();
        String sql = "SELECT b.account_id, b.account_name, b.user_id, u.username, u.first_name, u.last_name, b.balance, u.email, b.status FROM bank_account b INNER JOIN user u ON b.user_id = u.user_id";
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                String[] row = new String[9];
                for (int i = 0; i < 9; i++) {
                    row[i] = resultSet.getString(i + 1);
                }
                userInfo.add(row);
            }
        } catch (SQLException e) {
            System.out.println(ANSI_YELLOW+"Could not retrieve User Data."+ANSI_RESET);
        }

        return userInfo;
    }

}
