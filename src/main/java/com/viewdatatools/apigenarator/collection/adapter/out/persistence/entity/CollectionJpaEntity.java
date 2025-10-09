package com.viewdatatools.apigenarator.collection.adapter.out.persistence.entity;

import com.viewdatatools.apigenarator.auth.adapter.out.persistence.entity.UserJpaEntity;
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
@Table(name = "user_collections", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "collection_name"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CollectionJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "collection_name", nullable = false)
    private String collectionName;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserJpaEntity user;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "schema", columnDefinition = "jsonb", nullable = false)
    private Map<String, Object> schema;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}

