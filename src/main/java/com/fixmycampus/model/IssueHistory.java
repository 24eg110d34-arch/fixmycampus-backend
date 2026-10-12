package com.fixmycampus.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/** One row per change to an issue: reported, assigned, status updated, resolved. */
@Entity
@Table(name = "issue_history")
public class IssueHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "issue_id")
    private Issue issue;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Status status;

    @Column(length = 1000)
    private String note;

    @Column(nullable = false, length = 100)
    private String changedBy;

    @Column(nullable = false)
    private LocalDateTime changedAt;

    public IssueHistory() {}

    public IssueHistory(Issue issue, Status status, String note, String changedBy) {
        this.issue = issue;
        this.status = status;
        this.note = note;
        this.changedBy = changedBy;
        this.changedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public Issue getIssue() { return issue; }
    public Status getStatus() { return status; }
    public String getNote() { return note; }
    public String getChangedBy() { return changedBy; }
    public LocalDateTime getChangedAt() { return changedAt; }
}
