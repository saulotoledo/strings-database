package com.stringsdb.api.repositories;

import com.stringsdb.api.entities.StringEntry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for string entries.
 */
@Repository
public interface StringEntryRepository extends JpaRepository<StringEntry, Long> {

    /**
     * Returns the values containing an informed string.
     *
     * @param str The string to search.
     * @param pageable Object containing pagination information.
     * @return A list of values matching the informed string.
     */
    Page<StringEntry> findByValueContaining(String str, Pageable pageable);
}
