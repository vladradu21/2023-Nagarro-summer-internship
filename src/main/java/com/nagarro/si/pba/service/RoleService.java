package com.nagarro.si.pba.service;

import com.nagarro.si.pba.dto.RoleDTO;
import com.nagarro.si.pba.model.RoleType;
import org.springframework.stereotype.Service;

@Service
public interface RoleService {
    RoleDTO getByType(RoleType type);
}
