package com.fixmycampus.controller;

import com.fixmycampus.dto.IssueDetailResponse;
import com.fixmycampus.dto.IssueResponse;
import com.fixmycampus.model.Category;
import com.fixmycampus.model.Priority;
import com.fixmycampus.service.IssueService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/** Endpoints used by students and staff. */
@RestController
@RequestMapping("/api/issues")
public class IssueController {

    private final IssueService issueService;

    public IssueController(IssueService issueService) {
        this.issueService = issueService;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<IssueResponse> create(
            Authentication authentication,
            @RequestParam("title") String title,
            @RequestParam("category") Category category,
            @RequestParam("location") String location,
            @RequestParam("description") String description,
            @RequestParam(value = "priority", defaultValue = "MEDIUM") Priority priority,
            @RequestParam(value = "image", required = false) MultipartFile image) {

        IssueResponse created = issueService.create(
                authentication.getName(), title, category, location, description, priority, image);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/my")
    public List<IssueResponse> myIssues(Authentication authentication) {
        return issueService.myIssues(authentication.getName());
    }

    @GetMapping("/{id}")
    public IssueDetailResponse detail(@PathVariable Long id, Authentication authentication) {
        return issueService.detail(id, authentication.getName());
    }
}
