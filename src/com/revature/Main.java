package com.revature;

public class Main {

    public static void main(String[] args) {
	// write your code here
        BankingDao dao = BankingDaoFactory.getUserDao();
        UIFactory.getUserInterface(dao);
    }
}
