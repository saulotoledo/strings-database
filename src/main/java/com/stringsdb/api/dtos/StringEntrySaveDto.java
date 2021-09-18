package com.stringsdb.api.dtos;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

/**
 * DTO for saving a string entry.
 */
public @Data class StringEntrySaveDto {
    @NotBlank(message = "The string value is mandatory")
    @Pattern(
        regexp = "^[\\p{Space}\\p{Graph}]{1,255}$",
        message = "The value must be a string between 1 and 255 characters long"
    )
    private String value;
}
