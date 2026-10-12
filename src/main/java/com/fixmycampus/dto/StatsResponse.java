package com.fixmycampus.dto;

import java.util.Map;

public record StatsResponse(long total, Map<String, Long> byStatus, Map<String, Long> byCategory) {
}
