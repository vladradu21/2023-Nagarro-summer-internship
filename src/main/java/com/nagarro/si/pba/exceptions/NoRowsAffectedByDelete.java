package com.nagarro.si.pba.exceptions;

public class NoRowsAffectedByDelete extends RuntimeException {
    public NoRowsAffectedByDelete(String message) {
        super(message);
    }
}
