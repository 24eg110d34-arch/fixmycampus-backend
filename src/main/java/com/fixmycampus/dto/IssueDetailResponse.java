package com.fixmycampus.dto;

import java.util.List;

public record IssueDetailResponse(IssueResponse issue, List<HistoryResponse> history) {
}
