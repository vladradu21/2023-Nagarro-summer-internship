package com.nagarro.si.pba.dto;

import com.nagarro.si.pba.model.RoleType;

public record RoleDTO(
        Integer id,
        RoleType type
) {
}
