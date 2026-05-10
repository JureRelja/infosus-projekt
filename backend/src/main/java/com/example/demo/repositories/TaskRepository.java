package com.example.demo.repositories;

import com.example.demo.models.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Integer> {

    List<Task> findByProjectId(Integer projectId);
}
