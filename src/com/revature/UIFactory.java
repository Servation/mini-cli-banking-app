package com.revature;

public class UIFactory {

    public static UI UI;

    private UIFactory() {

    }

    public static void getUserInterface(BankingDao dao) {
        if (UI == null) {
            UI = new UI(dao);
        }
    }
}
