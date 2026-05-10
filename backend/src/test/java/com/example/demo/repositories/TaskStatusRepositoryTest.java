package com.example.demo.repositories;

import com.example.demo.models.TaskStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class TaskStatusRepositoryTest {

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    private TestEntityManager em;

    private TaskStatus newStatus(String name, int orderIndex) {
        TaskStatus s = new TaskStatus();
        s.setName(name);
        s.setOrderIndex(orderIndex);
        return s;
    }

    @Test
    void findAllByOrderByOrderIndexAsc_returnsSorted() {
        em.persist(newStatus("Zatvoren", 3));
        em.persist(newStatus("Otvoren", 1));
        em.persist(newStatus("U radu", 2));
        em.flush();

        List<TaskStatus> result = taskStatusRepository.findAllByOrderByOrderIndexAsc();

        assertThat(result).extracting(TaskStatus::getName)
                .containsExactly("Otvoren", "U radu", "Zatvoren");
    }
}
