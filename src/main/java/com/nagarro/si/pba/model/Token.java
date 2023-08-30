package com.nagarro.si.pba.model;

import org.springframework.data.annotation.Id;

public class Token {
    @Id
    private Integer id;

    private String token;


    public Token(String token) {
        this.token = token;
    }

    public Token() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
