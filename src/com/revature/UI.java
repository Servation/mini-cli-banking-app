package com.revature;

import java.sql.SQLException;
import java.util.*;

public class UI {

    public UI() {
        BankingDao dao = BankingDaoFactory.getUserDao();
        Scanner scanner = new Scanner(System.in);
        boolean flag = true;

        while (flag) {
            System.out.println("*****************Project ZERO Bank*****************");
            System.out.println("Select from the options below:");
            System.out.println("Enter 1: Create User account");
            System.out.println("Enter 2: Login");
            System.out.println("Enter 9: Exit");
            System.out.println("**************************************");
            String input = scanner.next();
            System.out.println();
            switch (input) {
                case "1" -> createAccount(dao, scanner);
                case "2" -> loginAccount(dao, scanner);
                case "9" -> {
                    flag = false;
                    ConnectionFactory.closeConnection();
                }
                default -> System.out.println("Did not receive a valid number");
            }

        }
    }

    // Create account
    public void createAccount(BankingDao userDao, Scanner scanner) {
        try {
            System.out.println("***********CREATE USER ACCOUNT***********");
            System.out.println("Enter your first name:");
            String first = scanner.next();
            System.out.println("Enter your last name:");
            String last = scanner.next();
            System.out.println("Enter a username: ");
            String username = scanner.next();
            System.out.println("Enter a password: ");
            String password = scanner.next();
            System.out.println("Enter your email: ");
            String email = scanner.next();
            User user = new User(username, password, first, last, email);
            userDao.addUser(user);
        } catch (SQLException e) {
            System.out.println("Creating account failed");
            System.out.println("**************************************");
            System.out.println("Enter 1: Try again");
            System.out.println("Enter Anything Else to Exit Account Creation");
            System.out.println("**************************************");
            if (scanner.next().equalsIgnoreCase("1")) {
                createAccount(userDao, scanner);
            }
            System.out.println("Leaving account creation...");
        }
    }

    // User login
    public void loginAccount(BankingDao userDao, Scanner scanner) {
        try {
            System.out.println("***********LOGIN***********");
            System.out.println("Enter your username:");
            String username = scanner.next();
            System.out.println("Enter your password:");
            String password = scanner.next();
            User user = userDao.getUserByLogin(username, password);
            System.out.println();
            if (user == null) {
                System.out.println("There is no user with that name or password");
                System.out.println("Leaving login...");
            } else {
                System.out.println("Logged in...");
                if (user.isemployee()) {
                    employeeLoggedIn(userDao, scanner, user);
                } else {
                    customerLoggedIn(userDao, scanner, user);
                }

            }
        } catch (Exception e) {
            System.out.println("Something went wrong, automatically logged out.");
        }

    }

    // Login for employees
    public void employeeLoggedIn(BankingDao userDao, Scanner scanner, User user) {
        System.out.println("***********EMPLOYEE LOGIN***********");
        System.out.println("Welcome " + user.getUsername() + "!");
        boolean flag = true;
        while (flag) {
            System.out.println("**************************************");
            System.out.println("Select from the options below:");
            System.out.println("Enter 1: Accounts");
            System.out.println("Enter 2: Transactions");
            System.out.println("Enter 9: Logout");
            System.out.println("**************************************");
            String input = scanner.next();
            System.out.println();
            switch (input) {
                case "1" -> employeeGetUserAccounts(userDao, scanner);
                case "2" -> employeeGetTransactions(userDao, scanner);
                case "9" -> {
                    System.out.println("Logging out...");
                    flag = false;
                }
                default -> System.out.println("Did not receive a valid number.");

            }
        }
    }

    public void employeeGetTransactions(BankingDao bankingDao, Scanner scanner) {
        System.out.println("***********Bank Transactions***********");
        boolean flag = true;
        while (flag) {
            System.out.println("**************************************");
            System.out.println("Select from the options below:");
            System.out.println("Enter 1: View all");
            System.out.println("Enter 2: View by transaction id");
            System.out.println("Enter 3: View transactions by account id");
            System.out.println("Enter 9: Return");
            System.out.println("**************************************");
            String input = scanner.next();
            System.out.println();
            System.out.println("***********Bank Transactions***********");
            try {
                switch (input) {
                    case "1" -> {
                        List<Transaction> transactions = bankingDao.viewAllTransactions();
                        System.out.format("+--------------+------------+-----------------+------------+---------------+------------+---------------------+%n");
                        System.out.format("|Transaction ID| Account ID |      Amount     |    Type    | To Account ID |   Status   |         Date        |%n");
                        System.out.format("+--------------+------------+-----------------+------------+---------------+------------+---------------------+%n");
                        for (Transaction transaction : transactions) {
                            System.out.println(transaction);
                        }
                        System.out.format("+--------------+------------+-----------------+------------+---------------+------------+---------------------+%n");
                    }
                    case "2" -> {
                        System.out.println("Enter transaction id: ");
                        int transactionId = getInt();
                        System.out.format("+--------------+------------+-----------------+------------+---------------+------------+---------------------+%n");
                        System.out.format("|Transaction ID| Account ID |      Amount     |    Type    | To Account ID |   Status   |         Date        |%n");
                        System.out.format("+--------------+------------+-----------------+------------+---------------+------------+---------------------+%n");
                        System.out.println(bankingDao.viewTransactionById(transactionId));
                        System.out.format("+--------------+------------+-----------------+------------+---------------+------------+---------------------+%n");
                    }
                    case "3" -> {
                        System.out.println("Enter account id: ");
                        int accountId = getInt();
                        printUserTransactions(bankingDao,accountId);
                    }
                    case "9" -> flag = false;
                    default -> System.out.println("Did not receive a valid number.");
                }
            } catch (NullPointerException e) {
                System.out.println("Cannot process input.");
            }
        }
    }

    public void employeeGetUserAccounts(BankingDao userDao, Scanner scanner) {
        printUserDetails(userDao);
        boolean flag = true;
        while (flag) {
            System.out.println("**************************************");
            System.out.println("Select from the options below:");
            System.out.print("Enter 1: View bank accounts \nEnter 2: Change account status\n");
            System.out.println("Enter 9: Return");
            System.out.println("**************************************");
            String input = scanner.next();
            System.out.println();
            switch (input) {
                case "1" -> printUserDetails(userDao);
                case "2" -> userChangeStatus(userDao, scanner);
                case "9" -> flag = false;
                default -> System.out.println("Did not receive a valid number.");
            }
        }
    }

    public void userChangeStatus(BankingDao userDao, Scanner scanner) {
        System.out.println("*****************Change Account Status*****************");
        System.out.println("Enter Account ID: ");
        try {
            int accountID = getInt();
            System.out.print("Enter 1: Approve \nEnter 2: Reject\n");
            System.out.println("Enter 9: Return");
            System.out.println("**************************************");
            String input = scanner.next();
            System.out.println();
            switch (input) {
                case "1" -> userDao.approveAccount(accountID);
                case "2" -> userDao.rejectAccount(accountID);
            }
        } catch (NullPointerException e) {
            System.out.println("Cannot process input.");
        } catch (Exception e) {
            System.out.println("Could not find account");
        }
    }

    private void printUserDetails(BankingDao userDao) {
        String leftAlightFormat = "| %12s | %15s | %9s | %15s | %15s | %15s | %15s | %15s |%n";
        List<String[]> userInfo = userDao.getUserDetails();
        System.out.format("+--------------+-----------------+-----------+-----------------+-----------------+-----------------+-----------------+-----------------+%n");
        System.out.format("|  Account ID  | Account Name    |  User ID  |     Username    |    First Name   |     Last Name   |     Balance     |      Status     |%n");
        System.out.format("+--------------+-----------------+-----------+-----------------+-----------------+-----------------+-----------------+-----------------+%n");
        for (String[] account : userInfo) {
            System.out.format(leftAlightFormat, account[0], account[1], account[2], account[3], account[4], account[5], account[6], account[7]);
        }
        System.out.format("+--------------+-----------------+-----------+-----------------+-----------------+-----------------+-----------------+-----------------+%n");
    }


    // Login for customers
    public void customerLoggedIn(BankingDao userDao, Scanner scanner, User user) throws SQLException {
        System.out.println("*****************Project ZERO Bank*****************");
        System.out.println("Welcome " + user.getFirstName() + "!");
        System.out.println(user);
        boolean flag = true;
        while (flag) {
            System.out.println("**************************************");
            System.out.println("Select from the options below:");
            System.out.println("Enter 1: Create an account");
            System.out.println("Enter 2: Account(s)");
            System.out.println("Enter 9: Logout");
            System.out.println("**************************************");
            String input = scanner.next();
            System.out.println();
            switch (input) {
                case "1" -> {
                    System.out.println("***********CREATE A BANK ACCOUNT***********");
                    System.out.println("Enter account name: ");
                    String accountName = scanner.next();
                    BankAccount bankAccount = new BankAccount(user.getId(), accountName);
                    try {
                        userDao.addBankAccount(bankAccount);
                        System.out.println("Account successfully created. Waiting for approval.");
                    } catch (SQLException e) {
                        System.out.println("Looks like something went wrong. Could not create account.");
                    }
                }
                case "2" -> {
                    System.out.println("***********BANK ACCOUNT***********");
                    Map<Integer, BankAccount> accountsMap = new HashMap<>();
                    try {
                        List<BankAccount> accounts = userDao.getUserBankAccount(user.getId());
                        for (BankAccount account : accounts) {
                            System.out.println(account);
                            accountsMap.put(account.getAccount_id(), account);
                        }
                    } catch (SQLException e) {
                        System.out.println("Could not find your accounts");
                    }
                    if (!accountsMap.isEmpty()) {
                        System.out.println("Enter account number you wish to use: ");
                        try {
                            int accNumber = getInt();
                            if (accountsMap.containsKey(accNumber) && userDao.getBankAccountStatus(accNumber)) {
                                customerBankAccountOptions(userDao, scanner, accountsMap.get(accNumber));
                            } else {
                                System.out.println("You are not current authorized to use this account");
                            }
                        } catch (NullPointerException e) {
                            System.out.println("Cannot process input.");
                        }
                    }
                }
                case "9" -> {
                    System.out.println("Logging out...");
                    flag = false;
                }
                default -> System.out.println("Did not receive a valid number");
            }
        }
    }

    public void customerBankAccountOptions(BankingDao userDao, Scanner scanner, BankAccount bankAccount) {
        boolean flag = true;
        while (flag) {
            System.out.println();
            System.out.println("***********Bank Account***********");
            System.out.println("Select from the options below:");
            System.out.print("Enter 1: Deposit\nEnter 2: Withdraw\nEnter 3: Transfer\nEnter 4: View transactions\n");
            System.out.println("Enter 9: Return");
            System.out.println("**************************************");
            System.out.println(bankAccount);
            String input = scanner.next();
            System.out.println();
            switch (input) {
                case "1" -> {
                    System.out.println("***********Bank Deposit***********");
                    System.out.println("Enter amount to deposit: ");
                    try {
                        double amount = getDouble();
                        if (amount <= 0) {
                            System.out.println("Cannot process deposit: " + amount);
                            continue;
                        }
                        userDao.deposit(bankAccount, amount);
                    } catch (SQLException e) {
                        System.out.println("Could not process your deposit");
                    } catch (NullPointerException e) {
                        System.out.println("Cannot process input.");
                    }
                }
                case "2" -> {
                    System.out.println("***********Bank Withdraw***********");
                    System.out.println("Enter amount to withdraw: ");
                    try {
                        double amount = getDouble();
                        if (amount <= 0) {
                            System.out.println("Cannot process withdraw: " + amount);
                            continue;
                        }
                        userDao.withdraw(bankAccount, amount);
                    } catch (NullPointerException e) {
                        System.out.println("Cannot process input.");
                    }
                }
                case "3" -> {
                    System.out.println("***********Bank Transfer***********");
                    try {
                        System.out.println("Enter amount to transfer: ");
                        double amount = getDouble();
                        System.out.println("Enter account id to transfer to:");
                        int accountId = getInt();
                        if (amount <= 0) {
                            System.out.println("Cannot process transfer");
                            continue;
                        }
                        BankAccount bankAccountTo = userDao.getBankAccountById(accountId);
                        userDao.transfer(bankAccount, bankAccountTo, amount);
                    } catch (SQLException e) {
                        System.out.println("No account by that id");
                    } catch (NullPointerException e) {
                        System.out.println("Cannot process input.");
                    }
                }
                case "4" -> printUserTransactions(userDao,bankAccount.getAccount_id());
                case "9" -> flag = false;
                default -> System.out.println("Please enter a number from one of the choices");

            }
        }
    }

    private void printUserTransactions(BankingDao bankingDao, int id) {
        List<Transaction> transactions = bankingDao.viewTransactionsByAccountId(id);
        System.out.println("***********Bank Transactions***********");
        System.out.format("+--------------+------------+-----------------+------------+---------------+------------+---------------------+%n");
        System.out.format("|Transaction ID| Account ID |      Amount     |    Type    | To Account ID |   Status   |         Date        |%n");
        System.out.format("+--------------+------------+-----------------+------------+---------------+------------+---------------------+%n");
        for (Transaction transaction : transactions) {
            System.out.println(transaction);
        }
        System.out.format("+--------------+------------+-----------------+------------+---------------+------------+---------------------+%n");
    }

    public Double getDouble() {
        Scanner scanner = new Scanner(System.in);
        try {
            return scanner.nextDouble();
        } catch (InputMismatchException e) {
            scanner.nextLine();
        }
        return null;
    }

    public Integer getInt() {
        Scanner scanner = new Scanner(System.in);
        try {
            return scanner.nextInt();
        } catch (InputMismatchException e) {
            scanner.nextLine();
        }
        return null;
    }
}
