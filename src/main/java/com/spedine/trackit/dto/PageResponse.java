package com.spedine.trackit.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.data.domain.Page;

import java.util.List;

@Schema(name = "PageResponse", description = "Generic paginated response wrapper")
public record PageResponse<T>(
        @Schema(description = "Current page content")
        List<T> content,
        @Schema(description = "Current page number (0-based)", example = "0")
        int page,
        @Schema(description = "Page size", example = "10")
        int size,
        @Schema(description = "Total number of elements", example = "42")
        long totalElements,
        @Schema(description = "Total number of pages", example = "5")
        int totalPages
) {
    public PageResponse(Page<T> page) {
        this(page.getContent(), page.getNumber(), page.getSize(), page.getTotalElements(), page.getTotalPages());
    }
}
