package com.fixmycampus.dto;

import com.fixmycampus.model.Category;
import com.fixmycampus.model.Priority;
import com.fixmycampus.model.Status;

import java.time.LocalDateTime;

public record IssueResponse(
        Long id,
        String title,
        Category category,
        String location,
        String description,
        Priority priority,
        Status status,
        String imageUrl,
        String reportedByName,
        String reportedByEmail,
        Long departmentId,
        String departmentName,
        String remarks,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        LocalDateTime resolvedAt) {
}
