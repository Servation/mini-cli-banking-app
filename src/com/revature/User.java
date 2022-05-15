package com.revature;

public class User {
    private String username;
    private String password;
    private String firstName;
    private String lastName;
    private String email;
    private int id;
    private boolean employee;

    public User(String userName, String password, String firstName, String lastName, String email) {
        this.username = userName;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    public User(String username, String firstName, String lastName, String email, int id, boolean employee) {
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.id = id;
        this.employee = employee;
    }

    public int getId() {
        return id;
    }

    public boolean isemployee() {
        return employee;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public String toString() {
        String leftAlightFormat = "| %15s | %15s | %15s | %10d |";
        return String.format(leftAlightFormat, firstName + " " + lastName, username, email, id);
//        return "Name: " + firstName + " | " +
//                "Username:" + username + " | " +
//                "Email:" + email + " | " +
//                "ID=" + id;
    }
}
