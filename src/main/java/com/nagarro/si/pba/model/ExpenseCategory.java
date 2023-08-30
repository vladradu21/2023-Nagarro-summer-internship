package com.nagarro.si.pba.model;

public enum ExpenseCategory {
    GROCERIES("Groceries"),
    VACATION("Vacation"),
    BILLS("Bills"),
    TRANSPORT("Transport"),
    UTILITIES("Utilities"),
    RENT("Rent"),
    MORTGAGE("Mortgage"),
    RESTAURANTS("Restaurants"),
    CLOTHING("Clothing"),
    ENTERTAINMENT("Entertainment"),
    HEALTHCARE("Healthcare"),
    INSURANCE("Insurance"),
    EDUCATION("Education"),
    PERSONAL_CARE("Personal Care"),
    GIFTS("Gifts"),
    CHARITY("Charity"),
    SAVINGS("Savings"),
    INVESTMENTS("Investments"),
    LOAN_PAYMENTS("Loan Payments"),
    CREDIT_CARD("Credit Card"),
    TAXES("Taxes"),
    HOME_MAINTENANCE("Home Maintenance"),
    CAR_MAINTENANCE("Car Maintenance"),
    PETS("Pets"),
    ELECTRONICS("Electronics"),
    SPORTS("Sports"),
    CHILD_CARE("Child Care"),
    SUBSCRIPTIONS("Subscriptions"),
    BOOKS("Books"),
    HOBBIES("Hobbies"),
    MISCELLANEOUS("Miscellaneous");
    private String value;

    ExpenseCategory(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
