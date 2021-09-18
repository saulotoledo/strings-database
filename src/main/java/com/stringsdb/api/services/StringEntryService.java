package com.stringsdb.api.services;

import com.stringsdb.api.dtos.StringEntryDto;
import com.stringsdb.api.dtos.StringEntrySaveDto;
import com.stringsdb.api.entities.StringEntry;
import com.stringsdb.api.mappers.StringEntryMapper;
import com.stringsdb.api.repositories.StringEntryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * A service for string entries.
 */
@Service
@RequiredArgsConstructor
public class StringEntryService {

    /**
     * The string entries repository.
     */
    private final StringEntryRepository repository;

    /**
     * Returns all the items in the database.
     *
     * @param filter The filter for the strings.
     * @param pageable Object containing pagination information.
     * @return A list of items.
     */
    public Page<StringEntryDto> getMany(String filter, Pageable pageable) {
        Page<StringEntry> dbResult;
        if (filter == null) {
            dbResult = this.repository.findAll(pageable);
        } else {
            dbResult = this.repository.findByValueContaining(filter, pageable);
        }

        List<StringEntryDto> dtos = dbResult.getContent().stream()
            .map(StringEntryMapper.INSTANCE::stringEntryToDto)
            .collect(Collectors.toList());

        return new PageImpl<>(dtos, pageable, dbResult.getTotalElements());
    }

    /**
     * Returns a single item.
     *
     * @param id The ID of the item to return.
     * @return A single item or an empty optional object if the item was not found.
     */
    public Optional<StringEntryDto> getOne(Long id) {
        Optional<StringEntry> dbResult = this.repository.findById(id);

        return dbResult.isEmpty()
            ? Optional.empty()
            : Optional.of(
                StringEntryMapper.INSTANCE.stringEntryToDto(dbResult.get())
            );
    }

    /**
     * Saves an item.
     *
     * @param item The item to save.
     * @return The saved item.
     */
    public StringEntryDto save(StringEntrySaveDto item) {
        StringEntry savedItem = this.repository.save(
            StringEntryMapper.INSTANCE.savingStringEntryDtoToEntity(item)
        );

        return StringEntryMapper.INSTANCE.stringEntryToDto(savedItem);
    }
}
