package com.stringsdb.api.entities;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * Describes a string entry.
 */
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "strings")
@Getter
@Setter
public class StringEntry {

    /**
     * The (auto-generated) ID of the entity.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    /**
     * The value of the string entry.
     */
    @Column(name = "value", nullable = false)
    private String value;

    /**
     * The string entry creation date.
     */
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
