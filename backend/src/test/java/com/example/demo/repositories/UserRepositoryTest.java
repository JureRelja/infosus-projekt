package com.example.demo.repositories;

import com.example.demo.models.Role;
import com.example.demo.models.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestEntityManager em;

    private User newUser(String username, String email, Role role) {
        User u = new User();
        u.setUsername(username);
        u.setPasswordHash("hash");
        u.setFirstName("F");
        u.setLastName("L");
        u.setEmail(email);
        u.setRole(role);
        u.setActive(true);
        return u;
    }

    @Test
    void save_persistsAndAssignsId() {
        Role role = new Role();
        role.setName("Admin");
        em.persist(role);

        User saved = userRepository.save(newUser("alice", "alice@example.com", role));

        assertThat(saved.getId()).isNotNull();
    }

    @Test
    void findById_returnsPersistedUser() {
        Role role = new Role();
        role.setName("Admin");
        em.persist(role);

        Integer id = em.persistAndGetId(newUser("bob", "bob@example.com", role), Integer.class);

        Optional<User> found = userRepository.findById(id);

        assertThat(found).isPresent();
        assertThat(found.get().getUsername()).isEqualTo("bob");
    }

    @Test
    void save_duplicateUsernameViolatesUniqueConstraint() {
        Role role = new Role();
        role.setName("Admin");
        em.persist(role);
        em.persist(newUser("dup", "one@example.com", role));
        em.flush();

        // The violation may surface on save() or flush() depending on flush mode.
        assertThatThrownBy(() -> {
            userRepository.save(newUser("dup", "two@example.com", role));
            em.flush();
        }).isInstanceOfAny(
                DataIntegrityViolationException.class,
                jakarta.persistence.PersistenceException.class
        );
    }
}
