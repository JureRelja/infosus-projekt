package com.example.demo.repositories;

import com.example.demo.models.Priority;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PriorityRepository extends JpaRepository<Priority, Integer> {

    List<Priority> findAllByOrderByOrderIndexAsc();
}
