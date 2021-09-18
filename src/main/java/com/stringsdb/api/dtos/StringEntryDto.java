package com.stringsdb.api.dtos;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * DTO for a string entry.
 */
public @Data class StringEntryDto {
    private Long id;
    private String value;
    private LocalDateTime createdAt;
}
