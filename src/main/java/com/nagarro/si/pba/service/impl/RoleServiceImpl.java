package com.nagarro.si.pba.service.impl;

import com.nagarro.si.pba.dto.RoleDTO;
import com.nagarro.si.pba.exceptions.ExceptionMessage;
import com.nagarro.si.pba.exceptions.PbaNotFoundException;
import com.nagarro.si.pba.mapper.RoleMapper;
import com.nagarro.si.pba.model.RoleType;
import com.nagarro.si.pba.repository.RoleRepository;
import com.nagarro.si.pba.service.RoleService;
import org.springframework.stereotype.Service;

@Service
public class RoleServiceImpl implements RoleService {
    private final RoleRepository roleRepository;

    private final RoleMapper roleMapper;

    public RoleServiceImpl(RoleRepository roleRepository, RoleMapper roleMapper) {
        this.roleRepository = roleRepository;
        this.roleMapper = roleMapper;
    }

    @Override
    public RoleDTO getByType(RoleType type) {
        return roleRepository.findByType(type.toString())
                .map(roleMapper::entityToDTO)
                .orElseThrow(() -> new PbaNotFoundException(ExceptionMessage.ROLE_NOT_FOUND.format(type)));
    }
}
