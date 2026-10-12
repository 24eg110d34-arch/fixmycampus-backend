package com.fixmycampus.dto;

import com.fixmycampus.model.Status;

import java.time.LocalDateTime;

public record HistoryResponse(Status status, String note, String changedBy, LocalDateTime changedAt) {
}
