package com.viewdatatools.apigenarator.collection.domain.service;

import com.viewdatatools.apigenarator.collection.domain.exception.CollectionNotFoundException;
import com.viewdatatools.apigenarator.collection.domain.model.CollectionDomain;
import com.viewdatatools.apigenarator.collection.domain.model.FieldDefinition;
import com.viewdatatools.apigenarator.collection.domain.port.out.CollectionRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetCollectionServiceTest {

    @Mock
    private CollectionRepositoryPort collectionRepositoryPort;

    @InjectMocks
    private GetCollectionService getCollectionService;

    private CollectionDomain testCollection;

    @BeforeEach
    void setUp() {
        Map<String, FieldDefinition> schema = new HashMap<>();
        schema.put("name", FieldDefinition.builder().type("STRING").required(true).build());

        testCollection = CollectionDomain.builder()
            .uuid(UUID.randomUUID())
            .collectionName("test_collection")
            .username("testuser")
            .schema(schema)
            .build();
    }

    @Test
    void getCollection_withExistingCollection_shouldReturnCollection() {
        when(collectionRepositoryPort.findByUsernameAndCollectionName("testuser", "test_collection"))
            .thenReturn(Optional.of(testCollection));

        CollectionDomain result = getCollectionService.getCollection("testuser", "test_collection");

        assertNotNull(result);
        assertEquals("test_collection", result.getCollectionName());
        assertEquals("testuser", result.getUsername());
        assertNotNull(result.getSchema());
    }

    @Test
    void getCollection_withNonExistentCollection_shouldThrowException() {
        when(collectionRepositoryPort.findByUsernameAndCollectionName(anyString(), anyString()))
            .thenReturn(Optional.empty());

        CollectionNotFoundException exception = assertThrows(
            CollectionNotFoundException.class,
            () -> getCollectionService.getCollection("testuser", "nonexistent")
        );

        assertTrue(exception.getMessage().contains("not found"));
    }

    @Test
    void getCollection_shouldReturnCollectionWithSchema() {
        when(collectionRepositoryPort.findByUsernameAndCollectionName("testuser", "test_collection"))
            .thenReturn(Optional.of(testCollection));

        CollectionDomain result = getCollectionService.getCollection("testuser", "test_collection");

        assertNotNull(result.getSchema());
        assertTrue(result.getSchema().containsKey("name"));
        assertEquals("STRING", result.getSchema().get("name").getType());
    }
}

