package com.nagarro.si.pba.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.time.LocalDateTime;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ResponseBody
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(PbaNotFoundException.class)
    public ApiExceptionFormat handlePbaNotFoundException(PbaNotFoundException ex) {
        return buildResponseEntity(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(ForbiddenException.class)
    public ApiExceptionFormat handleForbiddenException(ForbiddenException ex) {
        return buildResponseEntity(HttpStatus.FORBIDDEN, ex.getMessage());
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(PbaInvalidInputException.class)
    public ApiExceptionFormat handlePbaInvalidInputException(PbaInvalidInputException ex) {
        return buildResponseEntity(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(PbaConflictException.class)
    public ApiExceptionFormat handlePbaConflictException(PbaConflictException ex) {
        return buildResponseEntity(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(UnauthorizedException.class)
    public ApiExceptionFormat handleUnauthorizedException(UnauthorizedException ex) {
        return buildResponseEntity(HttpStatus.UNAUTHORIZED, ex.getMessage());
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(PbaRemoteServiceException.class)
    public ApiExceptionFormat handlePbaRemoteServiceException(PbaRemoteServiceException ex) {
        return buildResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ApiExceptionFormat handleGenericException(Exception ex) {
        return buildResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
    }
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(InvalidInputException .class)
    public ApiExceptionFormat handleJsonInvalidExceptionException(InvalidInputException  ex) {
        return buildResponseEntity(HttpStatus.BAD_REQUEST, ex.getMessage());
    }
    @ResponseBody
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(ExcelCreationException .class)
    public ApiExceptionFormat handleExcelCreationException(ExcelCreationException  ex) {
        return buildResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
    }
    private ApiExceptionFormat buildResponseEntity(HttpStatus status, String message) {
        ApiExceptionFormat apiException = new ApiExceptionFormat();
        apiException.setStatus(status.value());
        apiException.setMessage(message);
        apiException.setTimestamp(LocalDateTime.now());
        return apiException;
    }
}
