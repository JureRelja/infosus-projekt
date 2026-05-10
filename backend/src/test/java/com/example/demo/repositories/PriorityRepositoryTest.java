package com.example.demo.repositories;

import com.example.demo.models.Priority;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class PriorityRepositoryTest {

    @Autowired
    private PriorityRepository priorityRepository;

    @Autowired
    private TestEntityManager em;

    private Priority newPriority(String name, int orderIndex) {
        Priority p = new Priority();
        p.setName(name);
        p.setOrderIndex(orderIndex);
        return p;
    }

    @Test
    void findAllByOrderByOrderIndexAsc_returnsSorted() {
        // insert out of order
        em.persist(newPriority("High", 3));
        em.persist(newPriority("Low", 1));
        em.persist(newPriority("Medium", 2));
        em.flush();

        List<Priority> result = priorityRepository.findAllByOrderByOrderIndexAsc();

        assertThat(result).extracting(Priority::getName)
                .containsExactly("Low", "Medium", "High");
        assertThat(result).extracting(Priority::getOrderIndex)
                .containsExactly(1, 2, 3);
    }

    @Test
    void findAllByOrderByOrderIndexAsc_emptyTableReturnsEmpty() {
        assertThat(priorityRepository.findAllByOrderByOrderIndexAsc()).isEmpty();
    }
}
