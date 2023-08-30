package com.nagarro.si.pba.model;

import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public abstract class Transaction implements ExcelWritable {
    @Id
    private int id;
    private TransactionType type;
    private String category;
    private String name;
    private double amount;
    private String description;
    private Currency currency;
    private LocalDateTime addedDate;
    private RepetitionFlow repetitionFlow;
    private int userId;
    private Integer groupId;
    private double balanceAfterTransaction;

    public double getBalanceAfterTransaction() {
        return balanceAfterTransaction;
    }

    public void setBalanceAfterTransaction(double balanceAfterTransaction) {
        this.balanceAfterTransaction = balanceAfterTransaction;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public TransactionType getType() {
        return type;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public LocalDateTime getAddedDate() {
        return addedDate;
    }

    public void setAddedDate(LocalDateTime addedDate) {
        this.addedDate = addedDate;
    }

    public RepetitionFlow getRepetitionFlow() {
        return repetitionFlow;
    }

    public void setRepetitionFlow(RepetitionFlow repetitionFlow) {
        this.repetitionFlow = repetitionFlow;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public Integer getGroupId() {
        return groupId;
    }

    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Transaction that = (Transaction) o;

        if (id != that.id) return false;
        if (Double.compare(that.amount, amount) != 0) return false;
        if (userId != that.userId) return false;
        if (type != that.type) return false;
        if (!Objects.equals(category, that.category)) return false;
        if (!Objects.equals(name, that.name)) return false;
        if (!Objects.equals(description, that.description)) return false;
        if (currency != that.currency) return false;
        if (!Objects.equals(addedDate, that.addedDate)) return false;
        if (repetitionFlow != that.repetitionFlow) return false;
        return Objects.equals(groupId, that.groupId);
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = id;
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (category != null ? category.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        temp = Double.doubleToLongBits(amount);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (currency != null ? currency.hashCode() : 0);
        result = 31 * result + (addedDate != null ? addedDate.hashCode() : 0);
        result = 31 * result + (repetitionFlow != null ? repetitionFlow.hashCode() : 0);
        result = 31 * result + userId;
        result = 31 * result + (groupId != null ? groupId.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "id=" + id +
                ", type=" + type +
                ", category='" + category + '\'' +
                ", name='" + name + '\'' +
                ", amount=" + amount +
                ", description='" + description + '\'' +
                ", currency=" + currency +
                ", addedDate=" + addedDate +
                ", repetitionFlow=" + repetitionFlow +
                ", userId=" + userId +
                ", groupId=" + groupId +
                '}';
    }

    @Override
    public List<String> toExcelRow() {
        return Arrays.asList(
                getType().toString(),
                getCategory(),
                getName(),
                Double.toString(getAmount()),
                getDescription(),
                getCurrency().toString(),
                getAddedDate().toString(),
                Double.toString(getBalanceAfterTransaction())
                );
    }
}