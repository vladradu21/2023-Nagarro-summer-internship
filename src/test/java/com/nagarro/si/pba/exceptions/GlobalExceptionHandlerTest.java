package com.nagarro.si.pba.exceptions;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GlobalExceptionHandlerTest {
    private GlobalExceptionHandler handler;
    private ApiExceptionFormat apiExceptionFormat;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
    }

    @Test
    void testHandlePbaNotFoundException() {
        PbaNotFoundException ex = new PbaNotFoundException(ExceptionMessage.USER_NOT_FOUND.format("1"));
        apiExceptionFormat = handler.handlePbaNotFoundException(ex);
        assertEquals("User with ID 1 not found.", apiExceptionFormat.getMessage());
    }

    @Test
    void testHandlePbaInvalidInputException() {
        PbaInvalidInputException ex = new PbaInvalidInputException(ExceptionMessage.DUPLICATE_EMAIL.format("test@gmail.com"));
        apiExceptionFormat = handler.handlePbaInvalidInputException(ex);
        assertEquals("A user with the email test@gmail.com already exists.", apiExceptionFormat.getMessage());
    }

    @Test
    void testHandlePbaConflictException() {
        PbaConflictException ex = new PbaConflictException(ExceptionMessage.USERNAME_ALREADY_EXISTS.format("test"));
        apiExceptionFormat= handler.handlePbaConflictException(ex);
        assertEquals("A user with the username test already exists.", apiExceptionFormat.getMessage());
    }

    @Test
    void testHandleUnauthorizedException(){
        UnauthorizedException ex = new UnauthorizedException(ExceptionMessage.VERIFICATION_TOKEN_EXPIRED.format());
        apiExceptionFormat= handler.handleUnauthorizedException(ex);
        assertEquals("The verification token is expired.", apiExceptionFormat.getMessage());
    }

    @Test
    void testHandleForbiddenException(){
        ForbiddenException ex = new ForbiddenException(ExceptionMessage.ACCOUNT_NOT_VERIFIED.format());
        apiExceptionFormat= handler.handleForbiddenException(ex);
        assertEquals("Account has not been verified.", apiExceptionFormat.getMessage());
    }
    @Test
    void testHandleExcelCreationException(){
        ExcelCreationException ex = new ExcelCreationException(ExceptionMessage.ERROR_CREATING_EXCEL_FILE.format());
        apiExceptionFormat= handler.handleExcelCreationException(ex);
        assertEquals("Error creating the Excel file", apiExceptionFormat.getMessage());
    }
}