package com.example.demo.repositories;

import com.example.demo.models.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskStatusRepository extends JpaRepository<TaskStatus, Integer> {

    List<TaskStatus> findAllByOrderByOrderIndexAsc();
}
