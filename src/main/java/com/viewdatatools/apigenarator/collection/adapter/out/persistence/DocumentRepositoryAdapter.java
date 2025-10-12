package com.viewdatatools.apigenarator.collection.adapter.out.persistence;

import com.viewdatatools.apigenarator.collection.adapter.out.persistence.entity.DocumentJpaEntity;
import com.viewdatatools.apigenarator.collection.domain.model.DocumentDomain;
import com.viewdatatools.apigenarator.collection.domain.port.out.DocumentRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class DocumentRepositoryAdapter implements DocumentRepositoryPort {

    private final DocumentRepository documentRepository;

    @Override
    public DocumentDomain save(DocumentDomain document) {
        if (document.getUuid() == null) {
            document.setUuid(UUID.randomUUID());
        }

        DocumentJpaEntity entity = DocumentJpaEntity.builder()
                .uuid(document.getUuid())
                .collectionUuid(document.getCollectionUuid())
                .documentData(document.getData())
                .createdAt(document.getCreatedAt())
                .updatedAt(document.getUpdatedAt())
                .build();

        DocumentJpaEntity saved = documentRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<DocumentDomain> findById(UUID documentUuid) {
        return documentRepository.findById(documentUuid)
                .map(this::toDomain);
    }

    @Override
    public List<DocumentDomain> findAllByCollectionUuid(UUID collectionUuid) {
        return documentRepository.findAllByCollectionUuid(collectionUuid).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(UUID documentUuid) {
        documentRepository.deleteById(documentUuid);
    }

    @Override
    @Transactional
    public void deleteAllByCollectionUuid(UUID collectionUuid) {
        documentRepository.deleteAllByCollectionUuid(collectionUuid);
    }

    @Override
    public long countByCollectionUuid(UUID collectionUuid) {
        return documentRepository.countByCollectionUuid(collectionUuid);
    }

    private DocumentDomain toDomain(DocumentJpaEntity entity) {
        return DocumentDomain.builder()
                .uuid(entity.getUuid())
                .collectionUuid(entity.getCollectionUuid())
                .data(entity.getDocumentData())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
