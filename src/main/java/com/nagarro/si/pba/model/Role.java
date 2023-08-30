package com.nagarro.si.pba.model;

import org.springframework.data.annotation.Id;

public class Role {
    @Id
    private Integer id;

    private RoleType type;

    public Role(Integer id, RoleType type) {
        this.id = id;
        this.type = type;
    }

    public Role() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public RoleType getType() {
        return type;
    }

    public void setType(RoleType type) {
        this.type = type;
    }
}
