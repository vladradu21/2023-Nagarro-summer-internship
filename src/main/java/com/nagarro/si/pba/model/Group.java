package com.nagarro.si.pba.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Group implements BalanceHolder {
    private Integer id;
    private String name;
	private double balance;
	private Currency defaultCurrency;
    private List<Transaction> transactions = new ArrayList<>();

    public Group() {
    }

    public Group(Integer id, String name, Currency defaultCurrency) {
        this.id = id;
        this.name = name;
        this.defaultCurrency = defaultCurrency;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

	public Currency getDefaultCurrency() {
		return defaultCurrency;
	}

	public void setDefaultCurrency(Currency defaultCurrency) {
		this.defaultCurrency = defaultCurrency;
	}

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Group group = (Group) o;
        return Objects.equals(id, group.id) && Objects.equals(name, group.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }
}