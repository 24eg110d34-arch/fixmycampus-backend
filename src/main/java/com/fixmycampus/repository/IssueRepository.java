package com.fixmycampus.repository;

import com.fixmycampus.model.Issue;
import com.fixmycampus.model.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface IssueRepository extends JpaRepository<Issue, Long>, JpaSpecificationExecutor<Issue> {

    List<Issue> findByReportedByIdOrderByCreatedAtDesc(Long userId);

    long countByStatus(Status status);

    @Query("select i.category, count(i) from Issue i group by i.category")
    List<Object[]> countGroupedByCategory();
}
