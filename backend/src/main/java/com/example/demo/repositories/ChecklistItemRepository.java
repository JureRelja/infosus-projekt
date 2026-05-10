package com.example.demo.repositories;

import com.example.demo.models.ChecklistItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChecklistItemRepository extends JpaRepository<ChecklistItem, Integer> {
}
