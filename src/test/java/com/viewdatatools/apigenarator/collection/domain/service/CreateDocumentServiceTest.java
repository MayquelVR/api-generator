package com.viewdatatools.apigenarator.collection.domain.service;

import com.viewdatatools.apigenarator.collection.domain.exception.CollectionNotFoundException;
import com.viewdatatools.apigenarator.collection.domain.model.CollectionDomain;
import com.viewdatatools.apigenarator.collection.domain.model.DocumentDomain;
import com.viewdatatools.apigenarator.collection.domain.model.FieldDefinition;
import com.viewdatatools.apigenarator.collection.domain.port.out.CollectionRepositoryPort;
import com.viewdatatools.apigenarator.collection.domain.port.out.DocumentRepositoryPort;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateDocumentServiceTest {

    @Mock
    private DocumentRepositoryPort documentRepositoryPort;

    @Mock
    private CollectionRepositoryPort collectionRepositoryPort;

    @Mock
    private SchemaValidatorService schemaValidatorService;

    @InjectMocks
    private CreateDocumentService createDocumentService;

    private UUID collectionUuid;
    private CollectionDomain testCollection;
    private Map<String, FieldDefinition> testSchema;
    private DocumentDomain testDocument;

    @BeforeEach
    void setUp() {
        collectionUuid = UUID.randomUUID();

        testSchema = new HashMap<>();
        testSchema.put("name", FieldDefinition.builder().type("STRING").required(true).build());
        testSchema.put("age", FieldDefinition.builder().type("INTEGER").required(false).build());

        testCollection = CollectionDomain.builder()
            .uuid(collectionUuid)
            .collectionName("users")
            .username("testuser")
            .schema(testSchema)
            .build();

        Map<String, Object> documentData = new HashMap<>();
        documentData.put("name", "John Doe");
        documentData.put("age", 30);

        testDocument = DocumentDomain.builder()
            .uuid(UUID.randomUUID())
            .data(documentData)
            .build();
    }

    @Test
    void createDocument_withValidData_shouldCreateSuccessfully() {
        when(collectionRepositoryPort.findByUsernameAndCollectionName("testuser", "users"))
            .thenReturn(Optional.of(testCollection));

        DocumentDomain savedDocument = DocumentDomain.builder()
            .uuid(UUID.randomUUID())
            .collectionUuid(collectionUuid)
            .data(testDocument.getData())
            .build();

        when(documentRepositoryPort.save(any(DocumentDomain.class)))
            .thenReturn(savedDocument);

        DocumentDomain result = createDocumentService.createDocument("testuser", "users", testDocument);

        assertNotNull(result);
        assertNotNull(result.getUuid());
        assertEquals(collectionUuid, result.getCollectionUuid());
        assertEquals(testDocument.getData(), result.getData());

        verify(schemaValidatorService).validateDocument(testDocument.getData(), testSchema);
        verify(documentRepositoryPort).save(any(DocumentDomain.class));
    }

    @Test
    void createDocument_withNonExistentCollection_shouldThrowException() {
        when(collectionRepositoryPort.findByUsernameAndCollectionName("testuser", "nonexistent"))
            .thenReturn(Optional.empty());

        CollectionNotFoundException exception = assertThrows(
            CollectionNotFoundException.class,
            () -> createDocumentService.createDocument("testuser", "nonexistent", testDocument)
        );

        assertTrue(exception.getMessage().contains("not found"));
        verify(documentRepositoryPort, never()).save(any());
    }

    @Test
    void createDocument_shouldValidateDocument() {
        when(collectionRepositoryPort.findByUsernameAndCollectionName("testuser", "users"))
            .thenReturn(Optional.of(testCollection));

        DocumentDomain savedDocument = DocumentDomain.builder()
            .uuid(UUID.randomUUID())
            .collectionUuid(collectionUuid)
            .data(testDocument.getData())
            .build();

        when(documentRepositoryPort.save(any(DocumentDomain.class)))
            .thenReturn(savedDocument);

        createDocumentService.createDocument("testuser", "users", testDocument);

        verify(schemaValidatorService).validateDocument(testDocument.getData(), testSchema);
    }

    @Test
    void createDocument_shouldSetCreatedAndUpdatedAt() {
        when(collectionRepositoryPort.findByUsernameAndCollectionName("testuser", "users"))
            .thenReturn(Optional.of(testCollection));

        when(documentRepositoryPort.save(any(DocumentDomain.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        DocumentDomain result = createDocumentService.createDocument("testuser", "users", testDocument);

        assertNotNull(result.getCreatedAt());
        assertNotNull(result.getUpdatedAt());
    }

    @Test
    void createDocument_shouldSetCollectionUuid() {
        when(collectionRepositoryPort.findByUsernameAndCollectionName("testuser", "users"))
            .thenReturn(Optional.of(testCollection));

        when(documentRepositoryPort.save(any(DocumentDomain.class)))
            .thenAnswer(invocation -> {
                DocumentDomain doc = invocation.getArgument(0);
                assertEquals(collectionUuid, doc.getCollectionUuid());
                return doc;
            });

        createDocumentService.createDocument("testuser", "users", testDocument);

        verify(documentRepositoryPort).save(argThat(doc ->
            doc.getCollectionUuid().equals(collectionUuid)
        ));
    }
}
