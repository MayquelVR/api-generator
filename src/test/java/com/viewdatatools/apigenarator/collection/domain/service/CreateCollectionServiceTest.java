package com.viewdatatools.apigenarator.collection.domain.service;

import com.viewdatatools.apigenarator.collection.domain.exception.CollectionAlreadyExistsException;
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
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateCollectionServiceTest {

    @Mock
    private CollectionRepositoryPort collectionRepositoryPort;

    @Mock
    private SchemaValidatorService schemaValidatorService;

    @InjectMocks
    private CreateCollectionService createCollectionService;

    private CollectionDomain testCollection;
    private Map<String, FieldDefinition> testSchema;

    @BeforeEach
    void setUp() {
        testSchema = new HashMap<>();
        testSchema.put("name", FieldDefinition.builder().type("STRING").required(true).build());
        testSchema.put("age", FieldDefinition.builder().type("INTEGER").required(false).build());

        testCollection = CollectionDomain.builder()
            .uuid(UUID.randomUUID())
            .collectionName("users")
            .username("testuser")
            .schema(testSchema)
            .build();
    }

    @Test
    void createCollection_withValidData_shouldCreateSuccessfully() {
        when(collectionRepositoryPort.existsByUsernameAndCollectionName(
            eq("testuser"), eq("users")
        )).thenReturn(false);
        when(collectionRepositoryPort.save(any(CollectionDomain.class)))
            .thenReturn(testCollection);

        CollectionDomain result = createCollectionService.createCollection(testCollection);

        assertNotNull(result);
        assertEquals("users", result.getCollectionName());
        assertEquals("testuser", result.getUsername());
        assertNotNull(result.getCreatedAt());
        assertNotNull(result.getUpdatedAt());

        verify(schemaValidatorService).validateSchema(testSchema);
        verify(collectionRepositoryPort).save(any(CollectionDomain.class));
    }

    @Test
    void createCollection_withEmptyName_shouldThrowException() {
        testCollection.setCollectionName("");

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> createCollectionService.createCollection(testCollection)
        );

        assertEquals("Collection name cannot be empty", exception.getMessage());
        verify(collectionRepositoryPort, never()).save(any());
    }

    @Test
    void createCollection_withNullName_shouldThrowException() {
        testCollection.setCollectionName(null);

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> createCollectionService.createCollection(testCollection)
        );

        assertEquals("Collection name cannot be empty", exception.getMessage());
        verify(collectionRepositoryPort, never()).save(any());
    }

    @Test
    void createCollection_withExistingCollection_shouldThrowException() {
        when(collectionRepositoryPort.existsByUsernameAndCollectionName(
            eq("testuser"), eq("users")
        )).thenReturn(true);

        CollectionAlreadyExistsException exception = assertThrows(
            CollectionAlreadyExistsException.class,
            () -> createCollectionService.createCollection(testCollection)
        );

        assertTrue(exception.getMessage().contains("already exists"));
        verify(collectionRepositoryPort, never()).save(any());
    }

    @Test
    void createCollection_shouldValidateSchema() {
        when(collectionRepositoryPort.existsByUsernameAndCollectionName(
            eq("testuser"), eq("users")
        )).thenReturn(false);
        when(collectionRepositoryPort.save(any(CollectionDomain.class)))
            .thenReturn(testCollection);

        createCollectionService.createCollection(testCollection);

        verify(schemaValidatorService).validateSchema(testSchema);
    }

    @Test
    void createCollection_shouldSetCreatedAndUpdatedAt() {
        when(collectionRepositoryPort.existsByUsernameAndCollectionName(
            eq("testuser"), eq("users")
        )).thenReturn(false);
        when(collectionRepositoryPort.save(any(CollectionDomain.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        CollectionDomain result = createCollectionService.createCollection(testCollection);

        assertNotNull(result.getCreatedAt());
        assertNotNull(result.getUpdatedAt());
        assertEquals(result.getCreatedAt(), result.getUpdatedAt());
    }

    @Test
    void createCollection_withWhitespaceName_shouldThrowException() {
        testCollection.setCollectionName("   ");

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> createCollectionService.createCollection(testCollection)
        );

        assertEquals("Collection name cannot be empty", exception.getMessage());
    }
}

