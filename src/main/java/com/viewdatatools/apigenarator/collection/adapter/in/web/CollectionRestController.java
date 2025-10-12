package com.viewdatatools.apigenarator.collection.adapter.in.web;

import com.viewdatatools.apigenarator.auth.domain.port.out.UserRepositoryPort;
import com.viewdatatools.apigenarator.collection.adapter.in.web.mapper.CollectionDtoMapper;
import com.viewdatatools.apigenarator.collection.adapter.in.web.mapper.DocumentDtoMapper;
import com.viewdatatools.apigenarator.collection.domain.model.CollectionDomain;
import com.viewdatatools.apigenarator.collection.domain.model.DocumentDomain;
import com.viewdatatools.apigenarator.collection.domain.port.in.*;
import com.viewdatatools.apigenarator.collection.dto.*;
import com.viewdatatools.apigenarator.util.UuidUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/collections")
@RequiredArgsConstructor
public class CollectionRestController {

    private final CreateCollectionUseCase createCollectionUseCase;
    private final ListCollectionsUseCase listCollectionsUseCase;
    private final GetCollectionUseCase getCollectionUseCase;
    private final DeleteCollectionUseCase deleteCollectionUseCase;
    private final CreateDocumentUseCase createDocumentUseCase;
    private final GetDocumentsUseCase getDocumentsUseCase;
    private final GetDocumentUseCase getDocumentUseCase;
    private final UpdateDocumentUseCase updateDocumentUseCase;
    private final DeleteDocumentUseCase deleteDocumentUseCase;
    private final CollectionDtoMapper collectionDtoMapper;
    private final DocumentDtoMapper documentDtoMapper;
    private final UserRepositoryPort userRepositoryPort;

    // ========== Collection Endpoints ==========

    @PostMapping
    public ResponseEntity<CollectionResponse> createCollection(
            @Valid @RequestBody CreateCollectionRequest request,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        UUID userUuid = userRepositoryPort.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"))
                .getUuid();

        CollectionDomain collection = createCollectionUseCase.createCollection(
                collectionDtoMapper.toDomain(request, userUuid, userDetails.getUsername())
        );
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(collectionDtoMapper.toResponse(collection));
    }

    @GetMapping
    public ResponseEntity<List<CollectionResponse>> listCollections(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        List<CollectionDomain> collections = listCollectionsUseCase.listCollections(userDetails.getUsername());
        List<CollectionResponse> response = collections.stream()
                .map(collectionDtoMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{collectionName}")
    public ResponseEntity<CollectionWithSchemaResponse> getCollection(
            @PathVariable String collectionName,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        CollectionDomain collection = getCollectionUseCase.getCollection(
                userDetails.getUsername(),
                collectionName
        );
        return ResponseEntity.ok(collectionDtoMapper.toResponseWithSchema(collection));
    }

    @DeleteMapping("/{collectionName}")
    public ResponseEntity<Void> deleteCollection(
            @PathVariable String collectionName,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        deleteCollectionUseCase.deleteCollection(userDetails.getUsername(), collectionName);
        return ResponseEntity.noContent().build();
    }

    // ========== Document Endpoints ==========

    @PostMapping("/{collectionName}/documents")
    public ResponseEntity<DocumentResponse> createDocument(
            @PathVariable String collectionName,
            @Valid @RequestBody CreateDocumentRequest request,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        DocumentDomain documentToCreate = DocumentDomain.builder()
                .uuid(UuidUtil.validateOrGenerateUuidV7(request.getUuid()))
                .data(request.getData())
                .build();

        DocumentDomain document = createDocumentUseCase.createDocument(
                userDetails.getUsername(),
                collectionName,
                documentToCreate
        );
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(documentDtoMapper.toResponse(document));
    }

    @GetMapping("/{collectionName}/documents")
    public ResponseEntity<List<DocumentResponse>> getDocuments(
            @PathVariable String collectionName,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        List<DocumentDomain> documents = getDocumentsUseCase.getDocuments(
                userDetails.getUsername(),
                collectionName
        );
        List<DocumentResponse> response = documents.stream()
                .map(documentDtoMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{collectionName}/documents/{documentUuid}")
    public ResponseEntity<DocumentResponse> getDocument(
            @PathVariable String collectionName,
            @PathVariable UUID documentUuid,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        DocumentDomain document = getDocumentUseCase.getDocument(
                userDetails.getUsername(),
                collectionName,
                documentUuid
        );
        return ResponseEntity.ok(documentDtoMapper.toResponse(document));
    }

    @PutMapping("/{collectionName}/documents/{documentUuid}")
    public ResponseEntity<DocumentResponse> updateDocument(
            @PathVariable String collectionName,
            @PathVariable UUID documentUuid,
            @Valid @RequestBody UpdateDocumentRequest request,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        DocumentDomain documentToUpdate = DocumentDomain.builder()
                .data(request.getData())
                .build();

        DocumentDomain document = updateDocumentUseCase.updateDocument(
                userDetails.getUsername(),
                collectionName,
                documentUuid,
                documentToUpdate
        );
        return ResponseEntity.ok(documentDtoMapper.toResponse(document));
    }

    @DeleteMapping("/{collectionName}/documents/{documentUuid}")
    public ResponseEntity<Void> deleteDocument(
            @PathVariable String collectionName,
            @PathVariable UUID documentUuid,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        deleteDocumentUseCase.deleteDocument(
                userDetails.getUsername(),
                collectionName,
                documentUuid
        );
        return ResponseEntity.noContent().build();
    }
}
