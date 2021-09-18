package com.stringsdb.api.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.stringsdb.api.dtos.StringEntryDto;
import com.stringsdb.api.dtos.StringEntrySaveDto;
import com.stringsdb.api.entities.StringEntry;
import com.stringsdb.api.services.StringEntryService;
import com.stringsdb.generators.StringEntryGenerator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = StringEntryController.class)
@DisplayName("Integration tests for the string entry controller with the MVC layer")
public class StringEntryControllerMvcTest {

    public static final String CONTENT_TYPE = "Content-Type";

    @MockBean
    private StringEntryService stringEntryService;

    @SpyBean
    private StringEntryController stringEntryController;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private List<StringEntryDto> convertCollectionJsonNodeResultContentToDtoArray(JsonNode contentJsonNode) throws IOException {
        List<StringEntryDto> resultList = new ArrayList<>();

        Iterator<JsonNode> elementsIterator = contentJsonNode.elements();
        while (elementsIterator.hasNext()) {
            resultList.add(
                this.objectMapper.treeToValue(elementsIterator.next(), StringEntryDto.class)
            );
        }

        return resultList;
    }

    @Test
    @DisplayName("Should return all entries retrieved by the service layer as JSON in a page with HTTP status 200 OK")
    public void testGetManyWithoutFilters() throws Exception {
        Pageable pageable = PageRequest.of(0, 5, Sort.by("id"));
        List<StringEntryDto> entries = StringEntryGenerator.generateManyStringEntryDtos(5);
        when(this.stringEntryService.getMany(any(), any(Pageable.class))).thenReturn(
            new PageImpl<>(entries, pageable, entries.size())
        );

        MvcResult result = mockMvc.perform(get("/strings"))
            .andExpect(status().isOk())
            .andExpect(header().string(CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
            .andReturn();

        String responseContent = result.getResponse().getContentAsString();
        JsonNode responseContentJsonNode = this.objectMapper.readTree(responseContent);

        assertThat(responseContentJsonNode.has("content"), is(true));
        assertThat(responseContentJsonNode.has("pageable"), is(true));
        assertThat(responseContentJsonNode.get("content").size(), is(entries.size()));

        List<StringEntryDto> returnedEntries = this.convertCollectionJsonNodeResultContentToDtoArray(
            responseContentJsonNode.get("content")
        );

        assertThat(returnedEntries.size(), is(entries.size()));
        assertThat(returnedEntries, containsInAnyOrder(entries.toArray()));
    }

    @Test
    @DisplayName("Should return HTTP status 201 CREATED after saving a valid entry")
    void testResponseOnValidCreate() throws Exception {
        StringEntrySaveDto entry = StringEntryGenerator.generateStringEntrySaveDto();

        mockMvc.perform(
            post("/strings")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(entry))
            )
            .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("Should return HTTP status 400 BAD_REQUEST if the payload does not contain the value of the string")
    void testBadRequestResponseOnInvalidValue() throws Exception {
        StringEntrySaveDto entry = new StringEntrySaveDto();

        MvcResult result = mockMvc.perform(
            post("/strings")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(entry))
            )
            .andExpect(status().isBadRequest())
            .andReturn();

        assertThat(Objects.requireNonNull(result.getResolvedException()).getMessage(), containsString("field 'value': rejected value [null]"));
    }

    @Test
    @DisplayName("Should ignore invalid request attributes and save an entry successfully, returning the HTTP status 201 CREATED")
    void testIgnoreInvalidAttributesDuringRequest() throws Exception {
        StringEntrySaveDto entry = StringEntryGenerator.generateStringEntrySaveDto();
        ObjectNode dataJsonNode = objectMapper.valueToTree(entry);
        dataJsonNode.put("invalidAttr", "invalidValue");

        mockMvc.perform(
            post("/strings")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(dataJsonNode))
            )
            .andExpect(status().isCreated());

        ArgumentCaptor<StringEntrySaveDto> argument = ArgumentCaptor.forClass(StringEntrySaveDto.class);
        Mockito.verify(this.stringEntryController).save(argument.capture());

        List<String> dtoFieldNames = Arrays.stream(argument.getValue().getClass().getDeclaredFields())
            .map(Field::getName)
            .collect(Collectors.toList());

        assertThat(dtoFieldNames, not(contains("invalidAttr")));
        assertThat(argument.getValue().getValue(), is(entry.getValue()));
    }

    @Test
    @DisplayName("Should ignore id and createdAt attributes on request and save an entry successfully, returning the HTTP status 201 CREATED")
    void testIgnoreIdAndCreatedAtAttributesDuringRequest() throws Exception {
        StringEntry entry = StringEntryGenerator.generateStringEntry(true, true);

        mockMvc.perform(
            post("/strings")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(entry))
            )
            .andExpect(status().isCreated());

        ArgumentCaptor<StringEntrySaveDto> argument = ArgumentCaptor.forClass(StringEntrySaveDto.class);
        Mockito.verify(this.stringEntryController).save(argument.capture());

        List<String> dtoFieldNames = Arrays.stream(argument.getValue().getClass().getDeclaredFields())
            .map(Field::getName)
            .collect(Collectors.toList());

        assertThat(dtoFieldNames, not(containsInAnyOrder("id", "createdAt")));
        assertThat(argument.getValue().getValue(), is(entry.getValue()));
    }

    @Test
    @DisplayName("Should receive pagination and sorting parameters")
    void testPaginationAndSortParamsReceived() throws Exception {
        Integer page = 0;
        Integer size = 2;
        String firstSort = "id,asc";
        String secondSort = "name,desc";

        mockMvc.perform(
            get("/strings")
                .param("page", page.toString())
                .param("size", size.toString())
                .param("sort", firstSort)
                .param("sort", secondSort)
            )
            .andExpect(status().isOk())
            .andReturn();

        ArgumentCaptor<Pageable> pageableRequestArgument = ArgumentCaptor.forClass(Pageable.class);
        Mockito.verify(this.stringEntryController).getMany(any(), pageableRequestArgument.capture());

        Pageable pageableRequestInstance = pageableRequestArgument.getValue();
        assertThat(pageableRequestInstance.getPageNumber(), is(page));
        assertThat(pageableRequestInstance.getPageSize(), is(size));
        assertThat(pageableRequestInstance.getSort().isSorted(), is(true));
        assertThat(Objects.requireNonNull(pageableRequestInstance.getSort().getOrderFor("id")).getDirection(), is(Sort.Direction.ASC));
        assertThat(Objects.requireNonNull(pageableRequestInstance.getSort().getOrderFor("name")).getDirection(), is(Sort.Direction.DESC));
    }

    @Test
    @DisplayName("Should call getMany in the service layer informing a pagination object")
    void testPaginationObjectBeingSentToTheServiceLayer() throws Exception {
        int page = 0;
        int size = 2;
        String firstSort = "id,asc";
        String secondSort = "name,desc";

        Pageable pageableRequest = PageRequest.of(page, size, Sort.by(firstSort, secondSort));
        List<StringEntryDto> entries = StringEntryGenerator.generateManyStringEntryDtos(size);
        when(this.stringEntryService.getMany(any(), any(Pageable.class))).thenReturn(
            new PageImpl<>(entries, pageableRequest, entries.size())
        );

        MvcResult requestResult = mockMvc.perform(
            get("/strings")
                .param("page", Integer.toString(page))
                .param("size", Integer.toString(size))
                .param("sort", firstSort)
                .param("sort", secondSort)
            )
            .andExpect(status().isOk())
            .andReturn();

        String responseContent = requestResult.getResponse().getContentAsString();
        JsonNode responseContentJsonNode = this.objectMapper.readTree(responseContent);

        List<StringEntryDto> returnedEntries = this.convertCollectionJsonNodeResultContentToDtoArray(
            responseContentJsonNode.get("content")
        );

        // The response only contains the entries if the mocked getMany() containing the pagination object was invoked
        // Please note that this test does not guarantee that the pageable object informed to the service layer contains
        // the values informed during the request. If that is intended, we should use a Spy instead.
        assertThat(returnedEntries.size(), is(entries.size()));
        assertThat(returnedEntries, containsInAnyOrder(entries.toArray()));
    }
}
