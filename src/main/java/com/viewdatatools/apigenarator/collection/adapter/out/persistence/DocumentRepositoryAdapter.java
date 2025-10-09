package com.viewdatatools.apigenarator.collection.adapter.out.persistence;

import com.viewdatatools.apigenarator.collection.adapter.out.persistence.entity.DocumentJpaEntity;
import com.viewdatatools.apigenarator.collection.domain.model.DocumentDomain;
import com.viewdatatools.apigenarator.collection.domain.port.out.DocumentRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class DocumentRepositoryAdapter implements DocumentRepositoryPort {

    private final DocumentRepository documentRepository;

    @Override
    public DocumentDomain save(DocumentDomain document) {
        DocumentJpaEntity entity = DocumentJpaEntity.builder()
                .id(document.getId())
                .collectionId(document.getCollectionId())
                .documentData(document.getData())
                .createdAt(document.getCreatedAt())
                .updatedAt(document.getUpdatedAt())
                .build();

        DocumentJpaEntity saved = documentRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<DocumentDomain> findById(Long documentId) {
        return documentRepository.findById(documentId)
                .map(this::toDomain);
    }

    @Override
    public List<DocumentDomain> findAllByCollectionId(Long collectionId) {
        return documentRepository.findAllByCollectionId(collectionId).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(Long documentId) {
        documentRepository.deleteById(documentId);
    }

    @Override
    @Transactional
    public void deleteAllByCollectionId(Long collectionId) {
        documentRepository.deleteAllByCollectionId(collectionId);
    }

    @Override
    public long countByCollectionId(Long collectionId) {
        return documentRepository.countByCollectionId(collectionId);
    }

    private DocumentDomain toDomain(DocumentJpaEntity entity) {
        return DocumentDomain.builder()
                .id(entity.getId())
                .collectionId(entity.getCollectionId())
                .data(entity.getDocumentData())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
