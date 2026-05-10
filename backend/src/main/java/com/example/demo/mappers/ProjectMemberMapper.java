package com.example.demo.mappers;

import com.example.demo.dtos.ProjectMemberDto;
import com.example.demo.models.ProjectMember;

public final class ProjectMemberMapper {

    private ProjectMemberMapper() {
    }

    public static ProjectMemberDto toDto(ProjectMember member) {
        return new ProjectMemberDto(
                member.getId(),
                member.getProject() != null ? member.getProject().getId() : null,
                member.getUser() != null ? member.getUser().getId() : null,
                member.getJoinedAt()
        );
    }
}
