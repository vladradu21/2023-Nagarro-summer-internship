package com.nagarro.si.pba.exceptions;

public enum ExceptionMessage {
    USER_NOT_FOUND("User with ID %s not found."),
    DUPLICATE_EMAIL("A user with the email %s already exists."),
    EMAIL_NOT_FOUND("User with email %s not found."),
    USERNAME_ALREADY_EXISTS("A user with the username %s already exists."),
    VERIFICATION_TOKEN_EXPIRED("The verification token is expired."),
    VERIFICATION_TOKEN_NOT_FOUND_OR_ALREADY_USED("The verification token was not found in the db or has already been used."),
    ACCOUNT_NOT_VERIFIED("Account has not been verified."),
    EXPIRED_TOKEN("The token has expired"),
    JSON_INVALID("Error while processing JSON"),
    UNAUTHORIZED("Unauthorized"),
    CATEGORY_NOT_FOUND("Category with id %d not found"),
    DEFAULT_CATEGORY("Default categories can't be modified!"),
    INVALID_RECURRING_INCOME("You try to add an one time income as a recurring one."),
    INVALID_RECURRING_EXPENSE("Recurring expenses cannot be one-time"),
    INSUFFICIENT_FUNDS("Insufficient funds."),
    USERNAME_NOT_FOUND("User with username %s not found."),
    ERROR_CREATING_EXCEL_FILE("Error creating the Excel file"),
    GROUP_NOT_FOUND("Group with username %s not found."),
    ROLE_NOT_FOUND("Role with type %s not found.");

    private final String value;

    ExceptionMessage(String messageFormat) {
        this.value = messageFormat;
    }

    public String format(Object... args) {
        return String.format(value, args);
    }

    public String format() {
        return value;
    }
}

