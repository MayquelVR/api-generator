package com.viewdatatools.apigenarator.collection.adapter.out.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;

@Entity
@Table(name = "collection_documents")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "collection_id", nullable = false)
    private Long collectionId;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "document_data", columnDefinition = "jsonb", nullable = false)
    private Map<String, Object> documentData;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
