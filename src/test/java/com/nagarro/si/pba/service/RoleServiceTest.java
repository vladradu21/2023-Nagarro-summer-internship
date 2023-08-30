package com.nagarro.si.pba.service;

import com.nagarro.si.pba.dto.RoleDTO;
import com.nagarro.si.pba.exceptions.PbaNotFoundException;
import com.nagarro.si.pba.mapper.RoleMapper;
import com.nagarro.si.pba.model.Role;
import com.nagarro.si.pba.model.RoleType;
import com.nagarro.si.pba.repository.RoleRepository;
import com.nagarro.si.pba.service.impl.RoleServiceImpl;
import com.nagarro.si.pba.utils.TestData;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RoleServiceTest {
    @Mock
    private RoleRepository roleRepository;

    @Mock
    private RoleMapper roleMapper;

    @InjectMocks
    private RoleServiceImpl roleService;

    @Test
    void testGetByTypeExisting() {
        Role role = TestData.returnRole();
        RoleDTO roleDTO = TestData.returnRoleDTO();

        when(roleRepository.findByType(RoleType.ADMIN.toString())).thenReturn(Optional.of(role));
        when(roleMapper.entityToDTO(role)).thenReturn(roleDTO);

        RoleDTO result = roleService.getByType(RoleType.ADMIN);

        assertEquals(roleDTO, result);
        verify(roleRepository).findByType(RoleType.ADMIN.toString());
        verify(roleMapper).entityToDTO(role);
    }

    @Test
    void testGetByTypeNotFound() {
        when(roleRepository.findByType(RoleType.ADMIN.toString())).thenReturn(Optional.empty());

        assertThrows(PbaNotFoundException.class, () -> roleService.getByType(RoleType.ADMIN));
    }
}