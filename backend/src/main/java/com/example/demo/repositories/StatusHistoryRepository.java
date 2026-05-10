package com.example.demo.repositories;

import com.example.demo.models.StatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StatusHistoryRepository extends JpaRepository<StatusHistory, Integer> {
}
