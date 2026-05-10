package com.example.demo.repositories;

import com.example.demo.models.ProjectMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface ProjectMemberRepository extends JpaRepository<ProjectMember, Integer> {

    List<ProjectMember> findByProjectId(Integer projectId);

    void deleteByProjectIdAndUserIdIn(Integer projectId, Collection<Integer> userIds);

    void deleteByProjectId(Integer projectId);
}
