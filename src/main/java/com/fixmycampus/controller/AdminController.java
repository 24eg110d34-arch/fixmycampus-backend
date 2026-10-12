package com.fixmycampus.controller;

import com.fixmycampus.dto.AssignRequest;
import com.fixmycampus.dto.DepartmentResponse;
import com.fixmycampus.dto.IssueResponse;
import com.fixmycampus.dto.StatsResponse;
import com.fixmycampus.dto.StatusRequest;
import com.fixmycampus.model.Category;
import com.fixmycampus.model.Priority;
import com.fixmycampus.model.Status;
import com.fixmycampus.service.IssueService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** Endpoints for administrators only (enforced in SecurityConfig: /api/admin/** needs ROLE_ADMIN). */
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final IssueService issueService;

    public AdminController(IssueService issueService) {
        this.issueService = issueService;
    }

    @GetMapping("/issues")
    public List<IssueResponse> issues(
            @RequestParam(value = "status", required = false) Status status,
            @RequestParam(value = "category", required = false) Category category,
            @RequestParam(value = "priority", required = false) Priority priority,
            @RequestParam(value = "q", required = false) String q) {
        return issueService.search(status, category, priority, q);
    }

    @PutMapping("/issues/{id}/assign")
    public IssueResponse assign(@PathVariable Long id,
                                @Valid @RequestBody AssignRequest request,
                                Authentication authentication) {
        return issueService.assign(id, request.departmentId(), request.remarks(), authentication.getName());
    }

    @PutMapping("/issues/{id}/status")
    public IssueResponse updateStatus(@PathVariable Long id,
                                      @Valid @RequestBody StatusRequest request,
                                      Authentication authentication) {
        return issueService.updateStatus(id, request.status(), request.remarks(), authentication.getName());
    }

    @GetMapping("/stats")
    public StatsResponse stats() {
        return issueService.stats();
    }

    @GetMapping("/departments")
    public List<DepartmentResponse> departments() {
        return issueService.departments();
    }
}
