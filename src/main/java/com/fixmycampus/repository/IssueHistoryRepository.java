package com.fixmycampus.repository;

import com.fixmycampus.model.IssueHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IssueHistoryRepository extends JpaRepository<IssueHistory, Long> {
    List<IssueHistory> findByIssueIdOrderByChangedAtAscIdAsc(Long issueId);
}
