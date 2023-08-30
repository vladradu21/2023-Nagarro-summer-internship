package com.nagarro.si.pba.model;

import org.springframework.data.annotation.Id;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class User implements BalanceHolder{
    @Id
    private Integer id;
    private String email;
    private String username;
    private String password;
    private String firstName;
    private String lastName;
    private String country;
    private Integer age;
    private boolean isVerified;
    private double balance;
	private Currency defaultCurrency;

    public User(Integer id, String email, String username, String password, String firstName, String lastName,
            String country, Integer age, boolean isVerified, double balance, Currency defaultCurrency) {
        this.id = id;
        this.email = email;
        this.username = username;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.country = country;
        this.age = age;
        this.isVerified = isVerified;
		this.balance = balance;
        this.defaultCurrency = defaultCurrency;
    }

    public User() {
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Currency getDefaultCurrency() {
        return defaultCurrency;
    }

    public void setDefaultCurrency(Currency defaultCurrency) {
        this.defaultCurrency = defaultCurrency;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public boolean getIsVerified() {
        return isVerified;
    }

    public void setIsVerified(boolean verified) {
        isVerified = verified;
    }


    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", country='" + country + '\'' +
                ", age=" + age +
                ", isVerified=" + isVerified +
                ", balance=" + balance +
				", defaultCurrency=" + defaultCurrency +
                '}';
    }
}
