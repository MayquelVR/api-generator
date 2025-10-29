package com.viewdatatools.apigenarator.collection.domain.service;

import com.viewdatatools.apigenarator.collection.domain.exception.CollectionNotFoundException;
import com.viewdatatools.apigenarator.collection.domain.model.CollectionDomain;
import com.viewdatatools.apigenarator.collection.domain.port.out.CollectionRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteCollectionServiceTest {

    @Mock
    private CollectionRepositoryPort collectionRepositoryPort;

    @InjectMocks
    private DeleteCollectionService deleteCollectionService;

    private UUID collectionUuid;
    private CollectionDomain testCollection;

    @BeforeEach
    void setUp() {
        collectionUuid = UUID.randomUUID();
        testCollection = CollectionDomain.builder()
            .uuid(collectionUuid)
            .collectionName("test_collection")
            .username("testuser")
            .build();
    }

    @Test
    void deleteCollection_withExistingCollection_shouldDeleteSuccessfully() {
        when(collectionRepositoryPort.findByUsernameAndCollectionName("testuser", "test_collection"))
            .thenReturn(Optional.of(testCollection));

        assertDoesNotThrow(() ->
            deleteCollectionService.deleteCollection("testuser", "test_collection")
        );

        verify(collectionRepositoryPort).delete(collectionUuid);
    }

    @Test
    void deleteCollection_withNonExistentCollection_shouldThrowException() {
        when(collectionRepositoryPort.findByUsernameAndCollectionName("testuser", "nonexistent"))
            .thenReturn(Optional.empty());

        CollectionNotFoundException exception = assertThrows(
            CollectionNotFoundException.class,
            () -> deleteCollectionService.deleteCollection("testuser", "nonexistent")
        );

        assertTrue(exception.getMessage().contains("not found"));
        verify(collectionRepositoryPort, never()).delete(any());
    }

    @Test
    void deleteCollection_shouldCallRepositoryDelete() {
        when(collectionRepositoryPort.findByUsernameAndCollectionName(anyString(), anyString()))
            .thenReturn(Optional.of(testCollection));

        deleteCollectionService.deleteCollection("testuser", "test_collection");

        verify(collectionRepositoryPort, times(1)).delete(collectionUuid);
    }
}
