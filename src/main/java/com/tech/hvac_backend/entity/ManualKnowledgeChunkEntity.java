package com.tech.hvac_backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "knowledge_chunks")
public class ManualKnowledgeChunkEntity {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "manual_id")
    private UUID manualId;

    @Column(name = "chunk_index", nullable = false)
    private Integer chunkIndex;

    private String section;

    @Column(name = "page_start")
    private Integer pageStart;

    @Column(name = "page_end")
    private Integer pageEnd;

    @Column(name = "topics", columnDefinition = "text[]")
    private String[] topics;

    @Column(name = "quality_score")
    private Integer qualityScore;

    @Column(columnDefinition = "text", nullable = false)
    private String content;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> metadata;

    @Column(name = "created_at")
    private OffsetDateTime createdAt;
}