package com.stringsdb.api.mappers;

import com.stringsdb.api.dtos.StringEntryDto;
import com.stringsdb.api.dtos.StringEntrySaveDto;
import com.stringsdb.api.entities.StringEntry;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * Mapper to transform string entries entities into DTOs and vice-versa.
 */
@Mapper(componentModel = "spring")
public interface StringEntryMapper {

    /**
     * The instance of the mapper.
     */
    StringEntryMapper INSTANCE = Mappers.getMapper(StringEntryMapper.class);

    /**
     * Transforms an entity into a DTO that represents it.
     *
     * @param entity The entity to transform.
     * @return The instance of the DTO representing the informed instance.
     */
    StringEntryDto stringEntryToDto(StringEntry entity);

    /**
     * Transforms a DTO for saving an entry into an entity to be stored in the database.
     *
     * @param savingEntry The DTO instance to transform.
     * @return An instance of the entity containing the data from the DTO informed.
     */
    StringEntry savingStringEntryDtoToEntity(StringEntrySaveDto savingEntry);
}
