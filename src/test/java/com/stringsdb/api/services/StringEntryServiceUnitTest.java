package com.stringsdb.api.services;

import com.stringsdb.api.dtos.StringEntryDto;
import com.stringsdb.api.dtos.StringEntrySaveDto;
import com.stringsdb.api.entities.StringEntry;
import com.stringsdb.api.mappers.StringEntryMapper;
import com.stringsdb.api.repositories.StringEntryRepository;
import com.stringsdb.generators.StringEntryGenerator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

@SuppressWarnings("ALL")
@ExtendWith(MockitoExtension.class)
@DisplayName("Unit tests for the string entry service")
public class StringEntryServiceUnitTest {

    @Mock
    private StringEntryRepository stringEntryRepository;

    @InjectMocks
    private StringEntryService stringEntryService;

    @Test
    @DisplayName("Should return all elements in a page when no filter is informed")
    public void testGetManyWithoutFilter() {
        Pageable pageable = PageRequest.of(0, 5, Sort.by("id"));
        List<StringEntry> allEntries = StringEntryGenerator.generateManyStringEntries(5, true, true);
        when(stringEntryRepository.findAll(any(Pageable.class))).thenReturn(
            new PageImpl<>(allEntries, pageable, allEntries.size())
        );

        Page<StringEntryDto> result = this.stringEntryService.getMany(null, pageable);

        assertThat(result.getContent(), containsInAnyOrder(allEntries.stream()
            .map(StringEntryMapper.INSTANCE::stringEntryToDto).toArray()));
    }

    @Test
    @DisplayName("Should return elements through repository filter method when a filter is informed")
    public void testGetManyWithFilter() {
        Pageable pageable = PageRequest.of(0, 5, Sort.by("id"));
        List<StringEntry> filteredEntries = StringEntryGenerator.generateManyStringEntries(2, true, true);
        when(stringEntryRepository.findByValueContaining(anyString(), any())).thenReturn(
            new PageImpl<>(filteredEntries, pageable, filteredEntries.size())
        );

        Page<StringEntryDto> result = this.stringEntryService.getMany("some_filter", pageable);

        assertThat(result.getContent(), containsInAnyOrder(filteredEntries.stream()
            .map(StringEntryMapper.INSTANCE::stringEntryToDto).toArray()));
    }

    @Test
    @DisplayName("Should return an entity instance if it exists")
    public void testGetOneExistingEntry() {
        StringEntry entry = StringEntryGenerator.generateStringEntry(true, true);
        when(stringEntryRepository.findById(any())).thenReturn(Optional.of(entry));
        @SuppressWarnings("OptionalGetWithoutIsPresent") StringEntryDto result = this.stringEntryService.getOne(1L).get();
        assertThat(result, is(StringEntryMapper.INSTANCE.stringEntryToDto(entry)));
    }

    @Test
    @DisplayName("Should return an empty optional if the instance does not exist")
    public void testGetOneNonexistentEntry() {
        when(stringEntryRepository.findById(any())).thenReturn(Optional.empty());
        Optional<StringEntryDto> result = this.stringEntryService.getOne(1L);
        assertThat(result.isEmpty(), is(true));
    }

    @Test
    @DisplayName("Should trigger the repository to save an entity successfully")
    public void testSaveValidEntry() {
        StringEntrySaveDto dataToSave = StringEntryGenerator.generateStringEntrySaveDto();
        StringEntry savedEntry = StringEntryGenerator.generateStringEntry(true, true);
        savedEntry.setValue(dataToSave.getValue());

        when(stringEntryRepository.save(any())).thenReturn(savedEntry);

        StringEntryDto result = this.stringEntryService.save(dataToSave);
        assertThat(result, is(StringEntryMapper.INSTANCE.stringEntryToDto(savedEntry)));
    }
}
