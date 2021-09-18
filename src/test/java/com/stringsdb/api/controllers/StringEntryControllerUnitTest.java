package com.stringsdb.api.controllers;

import com.stringsdb.api.dtos.StringEntryDto;
import com.stringsdb.api.dtos.StringEntrySaveDto;
import com.stringsdb.api.services.StringEntryService;
import com.stringsdb.generators.StringEntryGenerator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Unit tests for the string entry controller")
public class StringEntryControllerUnitTest {

    @Mock
    private StringEntryService stringEntryService;

    @InjectMocks
    private StringEntryController stringEntryController;

    @Test
    @DisplayName("Should return all entries retrieved by the service layer")
    public void testGetManyFromService() {
        Pageable pageable = PageRequest.of(0, 5, Sort.by("id"));
        List<StringEntryDto> entries = StringEntryGenerator.generateManyStringEntryDtos(5);
        when(stringEntryService.getMany(any(), any())).thenReturn(
            new PageImpl<>(entries, pageable, entries.size())
        );
        Page<StringEntryDto> result = this.stringEntryController.getMany(null, pageable);
        assertThat(result.getContent(), is(entries));
    }

    @Test
    @DisplayName("Should return the single entry retrieved by the service layer")
    public void testGetOneFromService() {
        StringEntryDto entry = StringEntryGenerator.generateStringEntryDto();
        when(stringEntryService.getOne(any())).thenReturn(Optional.of(entry));
        StringEntryDto result = this.stringEntryController.getOne(1L);
        assertThat(result, is(entry));
    }

    @Test
    @DisplayName("Should throw a not found exception if the entity does not exist")
    public void testThrowNotFoundExceptionForNotFoundEntry() {
        when(stringEntryService.getOne(any())).thenReturn(Optional.empty());

        ResponseStatusException throwable = assertThrows(ResponseStatusException.class, () -> this.stringEntryController.getOne(1L));
        assertThat(ResponseStatusException.class, is(throwable.getClass()));
        assertThat(throwable.getStatus(), is(HttpStatus.NOT_FOUND));
        assertThat(throwable.getMessage(), containsString("was not found"));
    }

    @Test
    @DisplayName("Should trigger the service layer to save a valid entry")
    public void testSaveValidEntry() {
        StringEntrySaveDto dataToSave = StringEntryGenerator.generateStringEntrySaveDto();
        StringEntryDto savedEntry = StringEntryGenerator.generateStringEntryDto();
        savedEntry.setValue(dataToSave.getValue());

        when(stringEntryService.save(dataToSave)).thenReturn(savedEntry);

        StringEntryDto result = this.stringEntryController.save(dataToSave);
        assertThat(result, is(savedEntry));
    }
}
