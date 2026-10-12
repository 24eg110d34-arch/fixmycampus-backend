package com.fixmycampus.service;

import com.fixmycampus.dto.DepartmentResponse;
import com.fixmycampus.dto.HistoryResponse;
import com.fixmycampus.dto.IssueDetailResponse;
import com.fixmycampus.dto.IssueResponse;
import com.fixmycampus.dto.StatsResponse;
import com.fixmycampus.exception.BadRequestException;
import com.fixmycampus.exception.ForbiddenException;
import com.fixmycampus.exception.ResourceNotFoundException;
import com.fixmycampus.model.Category;
import com.fixmycampus.model.Department;
import com.fixmycampus.model.Issue;
import com.fixmycampus.model.IssueHistory;
import com.fixmycampus.model.Priority;
import com.fixmycampus.model.Role;
import com.fixmycampus.model.Status;
import com.fixmycampus.model.User;
import com.fixmycampus.repository.DepartmentRepository;
import com.fixmycampus.repository.IssueHistoryRepository;
import com.fixmycampus.repository.IssueRepository;
import com.fixmycampus.repository.IssueSpecifications;
import com.fixmycampus.repository.UserRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * All the business rules of FixMyCampus live here:
 * report -> assign -> in progress -> resolved (closed).
 */
@Service
@Transactional
public class IssueService {

    private final IssueRepository issues;
    private final IssueHistoryRepository history;
    private final UserRepository users;
    private final DepartmentRepository departments;
    private final FileStorageService fileStorage;

    public IssueService(IssueRepository issues,
                        IssueHistoryRepository history,
                        UserRepository users,
                        DepartmentRepository departments,
                        FileStorageService fileStorage) {
        this.issues = issues;
        this.history = history;
        this.users = users;
        this.departments = departments;
        this.fileStorage = fileStorage;
    }

    // ------------------------------------------------------------------ users

    public IssueResponse create(String email, String title, Category category, String location,
                                String description, Priority priority, MultipartFile image) {
        User reporter = getUser(email);

        title = requireText(title, "Title", 150);
        location = requireText(location, "Location", 150);
        description = requireText(description, "Description", 2000);

        Issue issue = new Issue();
        issue.setTitle(title);
        issue.setCategory(category);
        issue.setLocation(location);
        issue.setDescription(description);
        issue.setPriority(priority == null ? Priority.MEDIUM : priority);
        issue.setStatus(Status.REPORTED);
        issue.setReportedBy(reporter);
        issue.setImagePath(fileStorage.store(image));
        issues.save(issue);

        history.save(new IssueHistory(issue, Status.REPORTED, "Issue reported", reporter.getName()));
        return toResponse(issue);
    }

    @Transactional(readOnly = true)
    public List<IssueResponse> myIssues(String email) {
        User user = getUser(email);
        return issues.findByReportedByIdOrderByCreatedAtDesc(user.getId())
                .stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public IssueDetailResponse detail(Long id, String email) {
        User user = getUser(email);
        Issue issue = getIssue(id);

        boolean isOwner = issue.getReportedBy().getId().equals(user.getId());
        if (!isOwner && user.getRole() != Role.ADMIN) {
            throw new ForbiddenException("You can only view your own issues");
        }

        List<HistoryResponse> timeline = history.findByIssueIdOrderByChangedAtAscIdAsc(id).stream()
                .map(h -> new HistoryResponse(h.getStatus(), h.getNote(), h.getChangedBy(), h.getChangedAt()))
                .toList();
        return new IssueDetailResponse(toResponse(issue), timeline);
    }

    // ------------------------------------------------------------------ admin

    @Transactional(readOnly = true)
    public List<IssueResponse> search(Status status, Category category, Priority priority, String q) {
        return issues.findAll(IssueSpecifications.filter(status, category, priority, q),
                        Sort.by(Sort.Direction.DESC, "createdAt"))
                .stream().map(this::toResponse).toList();
    }

    public IssueResponse assign(Long id, Long departmentId, String remarks, String adminEmail) {
        User admin = getUser(adminEmail);
        Issue issue = getIssue(id);
        ensureOpen(issue);

        Department department = departments.findById(departmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found"));

        issue.setDepartment(department);
        if (issue.getStatus() == Status.REPORTED) {
            issue.setStatus(Status.ASSIGNED);
        }
        String note = clean(remarks);
        if (note != null) {
            issue.setRemarks(note);
        }
        issues.save(issue);

        String text = "Assigned to " + department.getName() + (note == null ? "" : ": " + note);
        history.save(new IssueHistory(issue, issue.getStatus(), text, admin.getName()));
        return toResponse(issue);
    }

    public IssueResponse updateStatus(Long id, Status newStatus, String remarks, String adminEmail) {
        User admin = getUser(adminEmail);
        Issue issue = getIssue(id);
        ensureOpen(issue);

        if (newStatus == Status.REPORTED) {
            throw new BadRequestException("An issue cannot be moved back to Reported");
        }
        if (newStatus.ordinal() <= issue.getStatus().ordinal()) {
            throw new BadRequestException("Status can only move forward from " + issue.getStatus());
        }
        if (issue.getDepartment() == null) {
            throw new BadRequestException("Assign a department before changing the status");
        }

        String note = clean(remarks);
        if (newStatus == Status.RESOLVED) {
            if (note == null) {
                throw new BadRequestException("Add a resolution note before closing the issue");
            }
            issue.setResolvedAt(LocalDateTime.now());
        }

        issue.setStatus(newStatus);
        if (note != null) {
            issue.setRemarks(note);
        }
        issues.save(issue);

        history.save(new IssueHistory(issue, newStatus, note == null ? "Status updated" : note, admin.getName()));
        return toResponse(issue);
    }

    @Transactional(readOnly = true)
    public StatsResponse stats() {
        Map<String, Long> byStatus = new LinkedHashMap<>();
        long total = 0;
        for (Status s : Status.values()) {
            long count = issues.countByStatus(s);
            byStatus.put(s.name(), count);
            total += count;
        }

        Map<String, Long> byCategory = new LinkedHashMap<>();
        for (Category c : Category.values()) {
            byCategory.put(c.name(), 0L);
        }
        for (Object[] row : issues.countGroupedByCategory()) {
            byCategory.put(((Category) row[0]).name(), (Long) row[1]);
        }
        return new StatsResponse(total, byStatus, byCategory);
    }

    @Transactional(readOnly = true)
    public List<DepartmentResponse> departments() {
        return departments.findAll(Sort.by("name")).stream()
                .map(d -> new DepartmentResponse(d.getId(), d.getName()))
                .toList();
    }

    // ---------------------------------------------------------------- helpers

    private User getUser(String email) {
        return users.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private Issue getIssue(Long id) {
        return issues.findById(id).orElseThrow(() -> new ResourceNotFoundException("Issue not found"));
    }

    private void ensureOpen(Issue issue) {
        if (issue.getStatus() == Status.RESOLVED) {
            throw new BadRequestException("This issue is resolved and closed");
        }
    }

    private String requireText(String value, String field, int maxLength) {
        String text = clean(value);
        if (text == null) {
            throw new BadRequestException(field + " is required");
        }
        if (text.length() > maxLength) {
            throw new BadRequestException(field + " must be " + maxLength + " characters or fewer");
        }
        return text;
    }

    private String clean(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }

    private IssueResponse toResponse(Issue i) {
        Department d = i.getDepartment();
        return new IssueResponse(
                i.getId(),
                i.getTitle(),
                i.getCategory(),
                i.getLocation(),
                i.getDescription(),
                i.getPriority(),
                i.getStatus(),
                i.getImagePath() == null ? null : "/uploads/" + i.getImagePath(),
                i.getReportedBy().getName(),
                i.getReportedBy().getEmail(),
                d == null ? null : d.getId(),
                d == null ? null : d.getName(),
                i.getRemarks(),
                i.getCreatedAt(),
                i.getUpdatedAt(),
                i.getResolvedAt());
    }
}
