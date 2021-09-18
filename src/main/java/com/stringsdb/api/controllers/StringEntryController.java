package com.stringsdb.api.controllers;

import com.stringsdb.api.dtos.StringEntryDto;
import com.stringsdb.api.dtos.StringEntrySaveDto;
import com.stringsdb.api.services.StringEntryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;

/**
 * String entries REST controller.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/strings")
public class StringEntryController {

    /**
     * The string entry service.
     */
    private final StringEntryService stringEntryService;

    /**
     * Returns many items.
     *
     * @param pageable Pageable object build by Spring to control page, size and sort attributes.
     * @return A list of items.
     */
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Page<StringEntryDto> getMany(
        @RequestParam(name = "filter", required = false) String filter,
        Pageable pageable
    ) {
        return this.stringEntryService.getMany(filter, pageable);
    }

    /**
     * Returns a single item.
     *
     * @param id The ID of the item to return.
     * @return The requested item.
     * @throws ResponseStatusException If the informed item was not found.
     */
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public StringEntryDto getOne(@PathVariable("id") Long id) throws ResponseStatusException {
        return this.stringEntryService.getOne(id).orElseThrow(
            () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "The item was not found")
        );
    }

    /**
     * Saves an item.
     *
     * @param entry The entry to save.
     * @return The saved item.
     */
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public StringEntryDto save(@RequestBody @Valid StringEntrySaveDto entry) throws ResponseStatusException {
        return this.stringEntryService.save(entry);
    }
}
