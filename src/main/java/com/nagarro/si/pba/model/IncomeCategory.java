package com.nagarro.si.pba.model;

public enum IncomeCategory {
    SALARY("Salary"),
    BONUS("Bonus"),
    RENTAL_INCOME("Rental Income"),
    INVESTMENTS("Investments"),
    SIDE_JOB("Side Job"),
    SAVINGS("Savings"),
    RETIREMENT("Retirement"),
    PENSION("Pension"),
    DIVIDENDS("Dividends"),
    ALIMONY("Alimony"),
    CHILD_SUPPORT("Child Support"),
    CAPITAL_GAINS("Capital Gains"),
    LOTTERY_WINNINGS("Lottery Winnings"),
    INHERITANCE("Inheritance"),
    GIFT("Gift"),
    TAX_REFUND("Tax Refund"),
    FREELANCING("Freelancing"),
    REAL_ESTATE("Real Estate"),
    TRUST_FUND("Trust Fund"),
    STUDENT_LOAN("Student Loan"),
    INSURANCE_SETTLEMENT("Insurance Settlement"),
    HEALTH_SAVINGS_ACCOUNT("Health Savings Account"),
    ROYALTIES("Royalties"),
    SOCIAL_SECURITY("Social Security"),
    SCHOLARSHIP("Scholarship"),
    STIPEND("Stipend"),
    GRATUITY("Gratuity"),
    REIMBURSEMENT("Reimbursement"),
    FINANCIAL_AID("Financial Aid"),
    MISCELLANEOUS("Miscellaneous");
    private String value;

    IncomeCategory(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
