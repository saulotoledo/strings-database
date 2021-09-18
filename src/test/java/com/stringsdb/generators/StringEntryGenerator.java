package com.stringsdb.generators;

import com.github.javafaker.Faker;
import com.stringsdb.api.dtos.StringEntryDto;
import com.stringsdb.api.dtos.StringEntrySaveDto;
import com.stringsdb.api.entities.StringEntry;
import com.stringsdb.api.mappers.StringEntryMapper;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Generates entities and DTOs instances for string entries.
 */
public class StringEntryGenerator {

    /**
     * Fake data generator instance.
     */
    private static final Faker faker = new Faker();

    /**
     * Generates a string entry entity.
     *
     * @param generateId Informs if the generated entity should have an ID.
     * @param generateCreationDate Informs if the generated entity should have a creation date.
     * @return An instance of the entity.
     */
    public static StringEntry generateStringEntry(boolean generateId, boolean generateCreationDate) {
        StringEntrySaveDto entryToSave = StringEntryGenerator.generateStringEntrySaveDto();
        StringEntry result = StringEntryMapper.INSTANCE.savingStringEntryDtoToEntity(entryToSave);
        if (generateId) {
            result.setId(1L);
        }

        if (generateCreationDate) {
            result.setCreatedAt(
                LocalDateTime.ofInstant(
                    faker.date().past(1, TimeUnit.DAYS).toInstant(),
                    ZoneId.of("Europe/Paris")
                )
            );
        }

        return result;
    }

    /**
     * Generates many string entry entities.
     *
     * @param quantity The amount of instances to generate.
     * @param generateId Informs if the generated entities should have IDs.
     * @param generateCreationDate Informs if the generated entities should have creation dates.
     * @return A list of entity instances.
     */
    public static List<StringEntry> generateManyStringEntries(int quantity, boolean generateId, boolean generateCreationDate) {
        List<StringEntry> entries = new ArrayList<>();

        for (int i = 0; i < quantity; i++) {
            StringEntry entry = StringEntryGenerator.generateStringEntry(generateId, generateCreationDate);
            if (generateId) {
                entry.setId((long) (i + 1));
            }
            entries.add(entry);
        }

        return entries;
    }

    /**
     * Generates an instance of the DTO used for saving string entries.
     *
     * @return An instance of the requested DTO.
     */
    public static StringEntrySaveDto generateStringEntrySaveDto() {
        StringEntrySaveDto entryToSave = new StringEntrySaveDto();
        entryToSave.setValue(faker.lorem().sentence());

        return entryToSave;
    }

    /**
     * Generates an instance of the DTO used to return data to clients.
     *
     * @return An instance of the requested DTO.
     */
    public static StringEntryDto generateStringEntryDto() {
        StringEntryDto entry = new StringEntryDto();
        entry.setId(1L);
        entry.setValue(faker.lorem().sentence());
        entry.setCreatedAt(
            LocalDateTime.ofInstant(
                faker.date().past(1, TimeUnit.DAYS).toInstant(),
                ZoneId.of("Europe/Paris")
            )
        );

        return entry;
    }

    /**
     * Generates many instances of the DTO used to return data to clients.
     *
     * @param quantity The amount of instances to generate.
     * @return A list of the requested DTO instances.
     */
    public static List<StringEntryDto> generateManyStringEntryDtos(int quantity) {
        List<StringEntryDto> entries = new ArrayList<>();

        for (int i = 0; i < quantity; i++) {
            StringEntryDto entry = StringEntryGenerator.generateStringEntryDto();
            entry.setId((long) (i + 1));
            entries.add(entry);
        }

        return entries;
    }
}
