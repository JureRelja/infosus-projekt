package com.example.demo.services;

import com.example.demo.dtos.RoleDto;
import com.example.demo.exceptions.ResourceNotFoundException;
import com.example.demo.models.Role;
import com.example.demo.repositories.RoleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RoleServiceTest {

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private RoleService roleService;

    private static Role role(Integer id, String name) {
        Role r = new Role();
        r.setId(id);
        r.setName(name);
        return r;
    }

    @Test
    void findAll_returnsAllRoles() {
        when(roleRepository.findAll()).thenReturn(List.of(role(1, "Direktor"), role(2, "Menadžer")));

        List<RoleDto> result = roleService.findAll();

        assertThat(result).extracting(RoleDto::name).containsExactly("Direktor", "Menadžer");
    }

    @Test
    void findById_returnsRole() {
        when(roleRepository.findById(1)).thenReturn(Optional.of(role(1, "Direktor")));

        RoleDto result = roleService.findById(1);

        assertThat(result.id()).isEqualTo(1);
        assertThat(result.name()).isEqualTo("Direktor");
    }

    @Test
    void findById_throwsNotFoundWhenMissing() {
        when(roleRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> roleService.findById(99))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
