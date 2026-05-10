package com.example.demo.services;

import com.example.demo.dtos.UserDto;
import com.example.demo.exceptions.ResourceNotFoundException;
import com.example.demo.models.Role;
import com.example.demo.models.User;
import com.example.demo.repositories.UserRepository;
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
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private static User user(Integer id) {
        User u = new User();
        u.setId(id);
        u.setUsername("user" + id);
        u.setPasswordHash("$2a$secret");
        u.setFirstName("First" + id);
        u.setLastName("Last" + id);
        u.setEmail("user" + id + "@example.com");
        Role role = new Role();
        role.setId(3);
        u.setRole(role);
        u.setActive(true);
        return u;
    }

    @Test
    void findAll_returnsAllUsers() {
        when(userRepository.findAll()).thenReturn(List.of(user(1), user(2)));

        List<UserDto> result = userService.findAll();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).username()).isEqualTo("user1");
        assertThat(result.get(0).roleId()).isEqualTo(3);
    }

    @Test
    void findAll_dtoDoesNotExposePasswordHash() {
        when(userRepository.findAll()).thenReturn(List.of(user(1)));

        UserDto dto = userService.findAll().get(0);

        // UserDto is a record — its components are id, username, firstName, lastName, email, roleId.
        // No accessor for passwordHash exists, ensuring it's not part of the API surface.
        assertThat(dto.getClass().getRecordComponents())
                .extracting(rc -> rc.getName())
                .doesNotContain("passwordHash");
    }

    @Test
    void findById_returnsUser() {
        when(userRepository.findById(1)).thenReturn(Optional.of(user(1)));

        assertThat(userService.findById(1).id()).isEqualTo(1);
    }

    @Test
    void findById_throwsNotFoundWhenMissing() {
        when(userRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.findById(99))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void getEntityById_throwsNotFoundWhenMissing() {
        when(userRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getEntityById(99))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
