package com.fixmycampus.repository;

import com.fixmycampus.model.Category;
import com.fixmycampus.model.Issue;
import com.fixmycampus.model.Priority;
import com.fixmycampus.model.Status;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

/** Builds the admin dashboard filter: every argument is optional. */
public final class IssueSpecifications {

    private IssueSpecifications() {}

    public static Specification<Issue> filter(Status status, Category category, Priority priority, String q) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }
            if (category != null) {
                predicates.add(cb.equal(root.get("category"), category));
            }
            if (priority != null) {
                predicates.add(cb.equal(root.get("priority"), priority));
            }
            if (q != null && !q.isBlank()) {
                String like = "%" + q.trim().toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("title")), like),
                        cb.like(cb.lower(root.get("location")), like)));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
