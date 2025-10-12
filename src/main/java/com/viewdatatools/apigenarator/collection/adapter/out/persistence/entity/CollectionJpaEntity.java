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
import java.util.UUID;

@Entity
@Table(name = "collections", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_uuid", "collection_name"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CollectionJpaEntity {

    @Id
    @Column(name = "uuid", nullable = false, unique = true)
    private UUID uuid;

    @Column(name = "collection_name", nullable = false)
    private String collectionName;

    @Column(name = "user_uuid", nullable = false)
    private UUID userUuid;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "schema", columnDefinition = "jsonb", nullable = false)
    private Map<String, Object> schema;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
