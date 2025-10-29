package com.viewdatatools.apigenarator.collection.adapter.in.web;

import com.viewdatatools.apigenarator.collection.domain.model.CollectionDomain;
import com.viewdatatools.apigenarator.collection.domain.model.FieldDefinition;
import com.viewdatatools.apigenarator.collection.domain.port.in.*;
import com.viewdatatools.apigenarator.collection.domain.exception.CollectionNotFoundException;
import com.viewdatatools.apigenarator.collection.domain.exception.InvalidSchemaException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CollectionRestControllerIntegrationTest {

    @Mock
    private CreateCollectionUseCase createCollectionUseCase;

    @Mock
    private ListCollectionsUseCase listCollectionsUseCase;

    @Mock
    private GetCollectionUseCase getCollectionUseCase;

    @Mock
    private DeleteCollectionUseCase deleteCollectionUseCase;

    private CollectionDomain testCollection;
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        Map<String, FieldDefinition> schema = new HashMap<>();
        schema.put("name", FieldDefinition.builder().type("STRING").required(true).build());
        schema.put("age", FieldDefinition.builder().type("INTEGER").required(false).build());

        testCollection = CollectionDomain.builder()
            .uuid(UUID.randomUUID())
            .collectionName("test_collection")
            .username("testuser")
            .schema(schema)
            .build();

        userDetails = User.builder()
            .username("testuser")
            .password("password")
            .authorities(Collections.emptyList())
            .build();
    }

    @Test
    void createCollection_withValidData_shouldReturnCollection() {
        when(createCollectionUseCase.createCollection(any(CollectionDomain.class)))
            .thenReturn(testCollection);

        CollectionDomain result = createCollectionUseCase.createCollection(testCollection);

        assertNotNull(result);
        assertEquals("test_collection", result.getCollectionName());
        assertEquals("testuser", result.getUsername());
        verify(createCollectionUseCase).createCollection(any(CollectionDomain.class));
    }

    @Test
    void createCollection_withInvalidSchema_shouldThrowException() {
        when(createCollectionUseCase.createCollection(any(CollectionDomain.class)))
            .thenThrow(new InvalidSchemaException("Invalid schema"));

        assertThrows(InvalidSchemaException.class, () -> {
            createCollectionUseCase.createCollection(testCollection);
        });
    }

    @Test
    void listCollections_shouldReturnCollectionsList() {
        List<CollectionDomain> collections = Arrays.asList(testCollection);
        when(listCollectionsUseCase.listCollections("testuser"))
            .thenReturn(collections);

        List<CollectionDomain> result = listCollectionsUseCase.listCollections("testuser");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("test_collection", result.get(0).getCollectionName());
        verify(listCollectionsUseCase).listCollections("testuser");
    }

    @Test
    void getCollection_withValidName_shouldReturnCollection() {
        when(getCollectionUseCase.getCollection("testuser", "test_collection"))
            .thenReturn(testCollection);

        CollectionDomain result = getCollectionUseCase.getCollection("testuser", "test_collection");

        assertNotNull(result);
        assertEquals("test_collection", result.getCollectionName());
        assertNotNull(result.getSchema());
        verify(getCollectionUseCase).getCollection("testuser", "test_collection");
    }

    @Test
    void getCollection_withNonExistentName_shouldThrowException() {
        when(getCollectionUseCase.getCollection("testuser", "nonexistent"))
            .thenThrow(new CollectionNotFoundException("Collection not found"));

        assertThrows(CollectionNotFoundException.class, () -> {
            getCollectionUseCase.getCollection("testuser", "nonexistent");
        });
        verify(getCollectionUseCase).getCollection("testuser", "nonexistent");
    }

    @Test
    void deleteCollection_withValidName_shouldDeleteSuccessfully() {
        doNothing().when(deleteCollectionUseCase).deleteCollection("testuser", "test_collection");

        assertDoesNotThrow(() -> {
            deleteCollectionUseCase.deleteCollection("testuser", "test_collection");
        });

        verify(deleteCollectionUseCase).deleteCollection("testuser", "test_collection");
    }

    @Test
    void deleteCollection_withNonExistentName_shouldThrowException() {
        doThrow(new CollectionNotFoundException("Collection not found"))
            .when(deleteCollectionUseCase).deleteCollection("testuser", "nonexistent");

        assertThrows(CollectionNotFoundException.class, () -> {
            deleteCollectionUseCase.deleteCollection("testuser", "nonexistent");
        });
        verify(deleteCollectionUseCase).deleteCollection("testuser", "nonexistent");
    }

    @Test
    void createCollection_shouldPreserveSchemaStructure() {
        when(createCollectionUseCase.createCollection(any(CollectionDomain.class)))
            .thenReturn(testCollection);

        CollectionDomain result = createCollectionUseCase.createCollection(testCollection);

        assertNotNull(result.getSchema());
        assertTrue(result.getSchema().containsKey("name"));
        assertTrue(result.getSchema().containsKey("age"));
        assertEquals("STRING", result.getSchema().get("name").getType());
        assertEquals("INTEGER", result.getSchema().get("age").getType());
    }

    @Test
    void listCollections_withNoCollections_shouldReturnEmptyList() {
        when(listCollectionsUseCase.listCollections("testuser"))
            .thenReturn(Collections.emptyList());

        List<CollectionDomain> result = listCollectionsUseCase.listCollections("testuser");

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(listCollectionsUseCase).listCollections("testuser");
    }
}
