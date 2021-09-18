package com.stringsdb.api.repositories;

import com.stringsdb.api.entities.StringEntry;
import com.stringsdb.generators.StringEntryGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.*;
import java.util.function.Function;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@DataJpaTest
@EnableJpaAuditing
@DisplayName("Integration tests for the string entry repository with the database layer")
public class StringEntryRepositoryJpaTest {

    @Autowired
    private StringEntryRepository stringEntryRepository;

    private final List<StringEntry> defaultEntries = StringEntryGenerator.generateManyStringEntries(5, false, false);

    private void sortStringEntriesListInPlaceBasedOn(@SuppressWarnings("rawtypes") Function method, String order, List<StringEntry> listToSort) {
        @SuppressWarnings({"rawtypes", "unchecked"}) Comparator comparator = Comparator.comparing(method);

        if (order.equals("desc")) {
            comparator.reversed();
        }

        //noinspection unchecked
        listToSort.sort(comparator);
    }

    private Sort getSortObjectBy(String attr, String order) {
        Sort requestSort = Sort.by(attr);
        if (order.equals("desc")) {
            requestSort.descending();
        }
        return requestSort;
    }

    @BeforeEach
    public void setUp() {
        this.defaultEntries.forEach(entry -> {
            // The saved entries should not have the creation date predefined
            this.stringEntryRepository.save(entry);
        });
    }

    @Test
    @DisplayName("Should automatically set the creation entity creation date while saving it")
    public void testAutomaticCreationDateGeneration() {
        List<StringEntry> result = this.stringEntryRepository.findAll();
        result.forEach(entry -> assertThat(entry.getCreatedAt(), is(not(nullValue()))));
    }

    @Test
    @DisplayName("Should return all elements saved in the database")
    public void testFindAllAfterSaving() {
        List<StringEntry> result = this.stringEntryRepository.findAll();
        assertThat(result.size(), is(this.defaultEntries.size()));
        assertThat(result, containsInAnyOrder(this.defaultEntries.toArray()));
    }

    @Test
    @DisplayName("Should return a specific item from the database")
    public void testFindOneExistingItem() {
        List<StringEntry> allItems = this.stringEntryRepository.findAll();
        StringEntry itemToCompare = allItems.get(2);
        Long idToFind = itemToCompare.getId();

        @SuppressWarnings("OptionalGetWithoutIsPresent") StringEntry singleItem = this.stringEntryRepository.findById(idToFind).get();
        assertThat(singleItem, is(not(nullValue())));
        assertThat(singleItem.getValue(), is(itemToCompare.getValue()));
        assertThat(singleItem.getCreatedAt(), is(itemToCompare.getCreatedAt()));
    }

    @Test
    @DisplayName("Should return an empty optional if an item does not exist in the database")
    public void testFindOneNonExistingItem() {
        Optional<StringEntry> singleItem = this.stringEntryRepository.findById(1000L);
        assertThat(singleItem.isEmpty(), is(true));
    }

    @Test
    @DisplayName("Should return only entries whose value contains the substring in the filter")
    public void testFindByValueContaining() {
        List<StringEntry> newEntries = StringEntryGenerator.generateManyStringEntries(3, false, false);
        newEntries.get(0).setValue("Contains a very specific first value");
        newEntries.get(1).setValue("Contains a very specific second value");

        newEntries.forEach(entry -> this.stringEntryRepository.save(entry));

        Page<StringEntry> result = this.stringEntryRepository.findByValueContaining(
            "a very specific",
            PageRequest.of(0, 5, Sort.by("id"))
        );

        assertThat(result.getTotalElements(), is(2L));
        assertThat(result, containsInAnyOrder(newEntries.get(0), newEntries.get(1)));
    }

    @Test
    @DisplayName("Should return the second page for a request without filters with the correct elements regardless of the sorting informed")
    public void testReturnTheSecondPageWithoutInformingFilter() {
        // By default, Spring starts counting the page from 0. Thus, page 1 is the second page
        int page = 1;
        int size = 2;

        Map<String, Function<StringEntry, Object>> attrsToMethodsToSort = new HashMap<>() {{
            put("id,asc", StringEntry::getId);
            put("id,desc", StringEntry::getId);
            put("value,asc", StringEntry::getValue);
            put("value,desc", StringEntry::getValue);
            put("createdAt,asc", StringEntry::getCreatedAt);
            put("createdAt,desc", StringEntry::getCreatedAt);
        }};

        for (Map.Entry<String, Function<StringEntry, Object>> entry : attrsToMethodsToSort.entrySet()) {
            String attr = entry.getKey().split(",")[0];
            String order = entry.getKey().split(",")[1];
            @SuppressWarnings("rawtypes") Function method = entry.getValue();

            this.sortStringEntriesListInPlaceBasedOn(method, order, this.defaultEntries);

            Sort requestSort = getSortObjectBy(attr, order);

            // The default entries array is already sorted properly.
            List<StringEntry> elementsToReturn = this.defaultEntries.subList(page * size, page * size + size);

            Page<StringEntry> result = this.stringEntryRepository.findAll(
                PageRequest.of(page, size, requestSort)
            );

            assertThat(result.getNumberOfElements(), is(size));
            assertThat(result.getTotalElements(), is((long) this.defaultEntries.size()));
            assertThat(result, contains(elementsToReturn.get(0), elementsToReturn.get(1)));
        }
    }

    @Test
    @DisplayName("Should return the second page for a request with filters with the correct elements regardless of the sorting informed")
    public void testPaginatedResultsWithFilter() {
        List<StringEntry> newEntries = StringEntryGenerator.generateManyStringEntries(10, false, false);

        for (int i = 0; i < newEntries.size(); i++) {
            StringEntry entry = newEntries.get(i);
            entry.setValue("Contains a very specific value " + i);
            this.stringEntryRepository.save(entry);
        }

        // By default, Spring starts counting the page from 0. Thus, page 1 is the second page
        int page = 1;
        int size = 2;

        Map<String, Function<StringEntry, Object>> attrsToMethodsToSort = new HashMap<>() {{
            put("id,asc", StringEntry::getId);
            put("id,desc", StringEntry::getId);
            put("value,asc", StringEntry::getValue);
            put("value,desc", StringEntry::getValue);
            put("createdAt,asc", StringEntry::getCreatedAt);
            put("createdAt,desc", StringEntry::getCreatedAt);
        }};

        for (Map.Entry<String, Function<StringEntry, Object>> entry : attrsToMethodsToSort.entrySet()) {
            String attr = entry.getKey().split(",")[0];
            String order = entry.getKey().split(",")[1];
            @SuppressWarnings("rawtypes") Function method = entry.getValue();

            this.sortStringEntriesListInPlaceBasedOn(method, order, newEntries);

            Sort requestSort = getSortObjectBy(attr, order);

            // The default entries array is already sorted properly.
            List<StringEntry> elementsToReturn = newEntries.subList(page * size, page * size + size);

            Page<StringEntry> result = this.stringEntryRepository.findByValueContaining(
                "a very specific",
                PageRequest.of(page, size, requestSort)
            );

            assertThat(result.getNumberOfElements(), is(size));
            assertThat(result.getTotalElements(), is((long) newEntries.size()));
            assertThat(result, contains(elementsToReturn.get(0), elementsToReturn.get(1)));
        }

    }
}
