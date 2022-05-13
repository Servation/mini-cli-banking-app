package com.revature;

public class BankingDaoFactory {
    public static BankingDao dao;

    private BankingDaoFactory() {

    }

    public static BankingDao getUserDao() {
        if (dao == null) {
            dao = new BankingDaoImpl();
        }
        return dao;
    }
}
