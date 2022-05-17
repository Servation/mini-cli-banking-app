package com.revature;

import java.sql.SQLException;
import java.util.*;

public class UI {
    private final BankingDao dao;
    private final Scanner scanner;
    private boolean exitFlag = true;

    public UI(BankingDao bankingDao) {
        dao = bankingDao;
        scanner = new Scanner(System.in);
        boolean flag = true;

        while (flag && exitFlag) {
            System.out.println("*****************Project ZERO Bank*****************");
            System.out.println("Select from the options below:");
            System.out.println("Enter 1: Create User account");
            System.out.println("Enter 2: Login");
            System.out.println("Enter 9: Exit");
            System.out.println("**************************************");
            String input = scanner.next();
            System.out.println();
            switch (input) {
                case "1" -> createAccount();
                case "2" -> loginAccount();
                case "9" -> flag = false;
                default -> System.out.println(yellowText("Did not receive a valid number"));
            }

        }
        ConnectionFactory.closeConnection();
    }

    // Create account
    private void createAccount() {
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
            dao.addUser(user);
        } catch (SQLException e) {
            System.out.println("Creating account failed");
            System.out.println("**************************************");
            System.out.println("Enter 1: Try again");
            System.out.println("Enter Anything Else to Exit Account Creation");
            System.out.println("**************************************");
            if (scanner.next().equalsIgnoreCase("1")) {
                createAccount();
            }
            System.out.println(blueText("Leaving account creation..."));
        }
    }

    // User login
    private void loginAccount() {
        try {
            System.out.println("***********LOGIN***********");
            System.out.println("Enter your username:");
            String username = scanner.next();
            System.out.println("Enter your password:");
            String password = scanner.next();
            User user = dao.getUserByLogin(username, password);
            System.out.println();
            if (user == null) {
                System.out.println(yellowText("There is no user with that name or password"));
                System.out.println(yellowText("Leaving login..."));
            } else {
                System.out.println(blueText("Logged in..."));
                if (user.isemployee()) {
                    employeeLoggedIn(user);
                } else {
                    customerLoggedIn(user);
                }

            }
        } catch (Exception e) {
            System.out.println(yellowText("Something went wrong, automatically logged out."));
        }

    }

    // Login for employees
    private void employeeLoggedIn(User user) {
        System.out.println("***********EMPLOYEE LOGIN***********");
        System.out.println("Welcome " + user.getUsername() + "!");
        boolean flag = true;
        while (flag && exitFlag) {
            System.out.println("**************************************");
            System.out.println("Select from the options below:");
            System.out.println("Enter 1: Accounts");
            System.out.println("Enter 2: Transactions");
            System.out.println("Enter 8: Logout");
            System.out.println("Enter 9: Exit");
            System.out.println("**************************************");
            String input = scanner.next();
            System.out.println();
            switch (input) {
                case "1" -> employeeGetUserAccounts();
                case "2" -> employeeGetTransactions();
                case "8" -> {
                    System.out.println(blueText("Logging out..."));
                    flag = false;
                }
                case "9" -> exit();
                default -> System.out.println(yellowText("Did not receive a valid number."));

            }
        }
    }

    private void employeeGetTransactions() {
        boolean flag = true;
        while (flag && exitFlag) {
            System.out.println("***********Bank Transactions***********");
            System.out.println("Select from the options below:");
            System.out.println("Enter 1: View all");
            System.out.println("Enter 2: View by transaction id");
            System.out.println("Enter 3: View transactions by account id");
            System.out.println("Enter 8: Return");
            System.out.println("Enter 9: Exit");
            System.out.println("**************************************");
            String input = scanner.next();
            System.out.println();
            try {
                switch (input) {
                    case "1" -> {
                        List<Transaction> transactions = dao.viewAllTransactions();
                        System.out.format(
                                "+--------------+------------+-----------------+------------+---------------+------------+---------------------+%n");
                        System.out.format("|Transaction ID| Account ID |      Amount     |    Type    | To Account ID" +
                                " |   Status   |         Date        |%n");
                        System.out.format(
                                "+--------------+------------+-----------------+------------+---------------+------------+---------------------+%n");
                        for (Transaction transaction : transactions) {
                            System.out.println(transaction);
                        }
                        System.out.format(
                                "+--------------+------------+-----------------+------------+---------------+------------+---------------------+%n");
                    }
                    case "2" -> {
                        System.out.println("Enter transaction id: ");
                        int transactionId = getInputInt();
                        System.out.format(
                                "+--------------+------------+-----------------+------------+---------------+------------+---------------------+%n");
                        System.out.format("|Transaction ID| Account ID |      Amount     |    Type    | To Account ID" +
                                " |   Status   |         Date        |%n");
                        System.out.format(
                                "+--------------+------------+-----------------+------------+---------------+------------+---------------------+%n");
                        System.out.println(dao.viewTransactionById(transactionId));
                        System.out.format(
                                "+--------------+------------+-----------------+------------+---------------+------------+---------------------+%n");
                    }
                    case "3" -> {
                        System.out.println("Enter account id: ");
                        int accountId = getInputInt();
                        printAccountTransactions(accountId);
                    }
                    case "8" -> flag = false;
                    case "9" -> exit();
                    default -> System.out.println(yellowText("Did not receive a valid number."));
                }
            } catch (NullPointerException e) {
                System.out.println(yellowText("Cannot process input."));
            }
        }
    }

    private void employeeGetUserAccounts() {
        printUserDetails();
        boolean flag = true;
        while (flag && exitFlag) {
            System.out.println("**************************************");
            System.out.println("Select from the options below:");
            System.out.print("Enter 1: View bank accounts \nEnter 2: View bank account by user ID\nEnter 3: Change " +
                    "account status\n");
            System.out.println("Enter 8: Return");
            System.out.println("Enter 9: Exit");
            System.out.println("**************************************");
            String input = scanner.next();
            System.out.println();
            switch (input) {
                case "1" -> printUserDetails();
                case "2" -> {
                    System.out.println("Enter user ID:");
                    try {
                        int id = getInputInt();
                        List<BankAccount> accounts = dao.getUserBankAccount(id);
                        System.out.println("+--------------+-----------------+-----------------+------------+");
                        System.out.println("|  Account ID  |  Account name   | Current Balance |   Status   |");
                        System.out.println("+--------------+-----------------+-----------------+------------+");
                        for (BankAccount bankAccount : accounts) {
                            System.out.println(bankAccount);
                        }
                        System.out.println("+--------------+-----------------+-----------------+------------+");
                    } catch (SQLException e) {
                        System.out.println(yellowText("Could not find accounts"));
                    } catch (NullPointerException e) {
                        System.out.println(yellowText("Cannot process input."));
                    }
                }
                case "3" -> employeeChangeAccountStatus();
                case "8" -> flag = false;
                case "9" -> exit();
                default -> System.out.println(yellowText("Did not receive a valid number."));
            }
        }
    }

    private void employeeChangeAccountStatus() {
        try {
            System.out.println("*****************Change Account Status*****************");
            System.out.println("Enter Account ID: ");
            int accountID = getInputInt();
            System.out.println("Enter 1: Approve \nEnter 2: Reject");
            System.out.println("**************************************");
            String input = scanner.next();
            System.out.println();
            switch (input) {
                case "1" -> dao.approveAccount(accountID);
                case "2" -> dao.rejectAccount(accountID);
            }
        } catch (NullPointerException e) {
            System.out.println(yellowText("Cannot process input."));
        } catch (Exception e) {
            System.out.println(yellowText("Could not find account"));
        }
    }

    private void printUserDetails() {
        String leftAlightFormat = "| %12s | %15s | %9s | %15s | %15s | %15s | %15s | %17s | %24s |%n";
        List<String[]> userInfo = dao.getUserDetails();
        System.out.format(
                "+--------------+-----------------+-----------+-----------------+-----------------+-----------------+-----------------+-------------------|-----------------+%n");
        System.out.format("|  Account ID  | Account Name    |  User ID  |     Username    |    First Name   |     " +
                "Last Name   |     Balance     |       Email       |      Status     |%n");
        System.out.format(
                "+--------------+-----------------+-----------+-----------------+-----------------+-----------------+-----------------+-------------------|-----------------+%n");
        for (String[] account : userInfo) {
            System.out.format(leftAlightFormat, account[0], account[1].toUpperCase(), account[2],
                    account[3].toUpperCase(), account[4].toUpperCase(), account[5].toUpperCase(), account[6],
                    account[7].toUpperCase(), account[8].equals("approved") ?
                            "\u001B[32m" + account[8].toUpperCase() + "\u001B[0m" :
                            "\u001B[31m" + account[8].toUpperCase() + "\u001B[0m");
        }
        System.out.format(
                "+--------------+-----------------+-----------+-----------------+-----------------+-----------------+-----------------+-------------------|-----------------+%n");
    }


    // Login for customers
    private void customerLoggedIn(User user) {
        System.out.println("Welcome " + user.getFirstName().toUpperCase() + "!");
        boolean flag = true;
        while (flag && exitFlag) {
            System.out.println("*************************Project ZERO Bank*************************");
            System.out.println("+-----------------+-----------------+-----------------+------------+");
            System.out.println("|       Name      |     Username    |      Email      |   User ID  |");
            System.out.println("+-----------------+-----------------+-----------------+------------+");
            System.out.println(user);
            System.out.println("+-----------------+-----------------+-----------------+------------+");
            System.out.println("**************************************");
            System.out.println("Select from the options below:");
            System.out.println("Enter 1: Create an account");
            System.out.println("Enter 2: Account(s)");
            System.out.println("Enter 8: Logout");
            System.out.println("Enter 9: Exit");
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
                        dao.addBankAccount(bankAccount);
                        System.out.println(blueText("Account successfully created. Waiting for approval."));
                    } catch (SQLException e) {
                        System.out.println(yellowText("Looks like something went wrong. Could not create account."));
                    }
                }
                case "2" -> {
                    System.out.println("**************************Bank Account**************************");
                    Map<Integer, BankAccount> accountsMap = new HashMap<>();
                    try {
                        List<BankAccount> accounts = dao.getUserBankAccount(user.getId());
                        System.out.println("+--------------+-----------------+-----------------+------------+");
                        System.out.println("|  Account ID  |  Account name   | Current Balance |   Status   |");
                        System.out.println("+--------------+-----------------+-----------------+------------+");
                        for (BankAccount account : accounts) {
                            System.out.println(account);
                            accountsMap.put(account.getAccount_id(), account);
                        }
                        System.out.println("+--------------+-----------------+-----------------+------------+");
                    } catch (SQLException e) {
                        System.out.println(yellowText("Could not find your accounts"));
                    }
                    if (!accountsMap.isEmpty()) {
                        System.out.println("Enter account number you wish to use: ");
                        try {
                            int accNumber = getInputInt();
                            if (accountsMap.containsKey(accNumber) && dao.getBankAccountStatus(accNumber)) {
                                customerBankAccountOptions(accountsMap.get(accNumber));
                            } else {
                                System.out.println(yellowText("You are not current authorized to use this account"));
                            }
                        } catch (NullPointerException e) {
                            System.out.println(yellowText("Cannot process input."));
                        } catch (SQLException e) {
                            System.out.println(yellowText("Could not access account."));
                        }
                    } else {
                        System.out.println(yellowText("You don't currently have an account"));
                    }
                }
                case "8" -> {
                    System.out.println(blueText("Logging out..."));
                    flag = false;
                }
                case "9" -> exit();
                default -> System.out.println(yellowText("Did not receive a valid number"));
            }
        }
    }

    private void customerBankAccountOptions(BankAccount bankAccount) {
        boolean flag = true;
        while (flag && exitFlag) {
            System.out.println();
            System.out.println("**************************Bank Account**************************");
            System.out.println("+--------------+-----------------+-----------------+------------+");
            System.out.println("|  Account ID  |  Account name   | Current Balance |   Status   |");
            System.out.println("+--------------+-----------------+-----------------+------------+");
            System.out.println(bankAccount);
            System.out.println("+--------------+-----------------+-----------------+------------+");
            System.out.println("Select from the options below:");
            System.out.print("Enter 1: Deposit\nEnter 2: Withdraw\nEnter 3: Transfer\nEnter 4: View transactions\n");
            System.out.println("Enter 8: Return");
            System.out.println("Enter 9: Exit");
            System.out.println("**************************************");
            String input = scanner.next();
            System.out.println();
            switch (input) {
                case "1" -> {
                    System.out.println("***********Bank Deposit***********");
                    System.out.println("Enter amount to deposit: ");
                    try {
                        double amount = scanner.nextDouble();
                        if (amount <= 0) {
                            System.out.println(yellowText("Cannot process deposit: " + amount));
                            continue;
                        }
                        dao.deposit(bankAccount, amount);
                    } catch (SQLException e) {
                        System.out.println(yellowText("Could not process your deposit"));
                    } catch (NullPointerException | InputMismatchException e) {
                        System.out.println(yellowText("Cannot process input."));
                        scanner.nextLine();
                    }
                }
                case "2" -> {
                    System.out.println("***********Bank Withdraw***********");
                    System.out.println("Enter amount to withdraw: ");
                    try {
                        double amount = scanner.nextDouble();
                        if (amount <= 0) {
                            System.out.println(yellowText("Cannot process withdraw: " + amount));
                            continue;
                        }
                        dao.withdraw(bankAccount, amount);
                    } catch (NullPointerException | InputMismatchException e) {
                        System.out.println(yellowText("Cannot process input."));
                        scanner.nextLine();
                    }
                }
                case "3" -> {
                    System.out.println("***********Bank Transfer***********");
                    try {
                        System.out.println("Enter amount to transfer: ");
                        double amount = scanner.nextDouble();
                        System.out.println("Enter account id to transfer to:");
                        int accountId = getInputInt();
                        if (amount <= 0) {
                            System.out.println(yellowText("Cannot process transfer"));
                            continue;
                        }
                        BankAccount bankAccountTo = dao.getBankAccountById(accountId);
                        dao.transfer(bankAccount, bankAccountTo, amount);
                    } catch (SQLException e) {
                        System.out.println(yellowText("Cannot process transfer"));
                    } catch (NullPointerException | InputMismatchException e) {
                        System.out.println(yellowText("Cannot process input."));
                        scanner.nextLine();
                    }
                }
                case "4" -> printAccountTransactions(bankAccount.getAccount_id());
                case "8" -> flag = false;
                case "9" -> exit();
                default -> System.out.println("Please enter a number from one of the choices");

            }
        }
    }

    private void printAccountTransactions(int id) {
        List<Transaction> transactions = dao.viewTransactionsByAccountId(id);
        System.out.println("***********Bank Transactions***********");
        System.out.format(
                "+--------------+------------+-----------------+------------+---------------+------------+---------------------+%n");
        System.out.format("|Transaction ID| Account ID |      Amount     |    Type    | To Account ID |   Status   | " +
                "        Date        |%n");
        System.out.format(
                "+--------------+------------+-----------------+------------+---------------+------------+---------------------+%n");
        for (Transaction transaction : transactions) {
            System.out.println(transaction);
        }
        System.out.format(
                "+--------------+------------+-----------------+------------+---------------+------------+---------------------+%n");
    }


    private Integer getInputInt() {
        try {
            return scanner.nextInt();
        } catch (InputMismatchException e) {
            scanner.nextLine();
        }
        return null;
    }

    private String blueText(String text) {
        final String ANSI_BLUE = "\u001B[34m";
        final String ANSI_RESET = "\u001B[0m";
        return ANSI_BLUE + text + ANSI_RESET;
    }

    private String yellowText(String text) {
        final String ANSI_YELLOW = "\u001B[33m";
        final String ANSI_RESET = "\u001B[0m";
        return ANSI_YELLOW + text + ANSI_RESET;
    }

    private void exit() {
        exitFlag = false;
    }
}
