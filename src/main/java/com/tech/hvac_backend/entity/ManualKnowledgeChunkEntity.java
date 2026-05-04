package com.tech.hvac_backend.entity;

import com.vladmihalcea.hibernate.type.array.ListArrayType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;

import java.time.OffsetDateTime;
import java.util.List;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manual_id", nullable = false)
    private ManualEntity manual;

    @Column(name = "chunk_index", nullable = false)
    private Integer chunkIndex;

    private String section;

    @Column(name = "page_start")
    private Integer pageStart;

    @Column(name = "page_end")
    private Integer pageEnd;

    @Type(ListArrayType.class)
    @Column(name = "topics", columnDefinition = "text[]")
    private List<String> topics;

    @Column(name = "quality_score")
    private Integer qualityScore;

    @Column(columnDefinition = "text", nullable = false)
    private String content;

    @Column(columnDefinition = "jsonb")
    @Convert(converter = JsonbConverter.class)
    private Map<String, Object> metadata;

    @Column(name = "created_at")
    private OffsetDateTime createdAt;
}