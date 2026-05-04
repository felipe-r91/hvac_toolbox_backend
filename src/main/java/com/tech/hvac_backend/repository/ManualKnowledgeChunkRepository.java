package com.tech.hvac_backend.repository;

import com.tech.hvac_backend.entity.ManualKnowledgeChunkEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ManualKnowledgeChunkRepository extends JpaRepository<ManualKnowledgeChunkEntity, UUID> {

    @Query(value = """
            SELECT kc.*
            FROM knowledge_chunks kc
            JOIN manuals m ON m.id = kc.manual_id
            WHERE LOWER(m.machine_model) = LOWER(:model)
              AND (
                    to_tsvector('english', kc.content) @@ plainto_tsquery('english', :query)
                    OR EXISTS (
                        SELECT 1
                        FROM unnest(kc.topics) AS topic
                        WHERE topic IN (:topics)
                    )
                  )
            ORDER BY
              (
                SELECT COUNT(*)
                FROM unnest(kc.topics) AS topic
                WHERE topic IN (:topics)
              ) DESC,
              kc.quality_score DESC,
              kc.chunk_index ASC
            LIMIT 6
            """, nativeQuery = true)
    List<ManualKnowledgeChunkEntity> searchRelevantChunks(
            String model,
            String query,
            List<String> topics
    );

    @Query(value = """
            SELECT kc.*
            FROM knowledge_chunks kc
            JOIN manuals m ON m.id = kc.manual_id
            WHERE LOWER(m.machine_model) = LOWER(:model)
            ORDER BY kc.quality_score DESC, kc.chunk_index ASC
            LIMIT 4
            """, nativeQuery = true)
    List<ManualKnowledgeChunkEntity> findBestChunksByModel(String model);

    @Query(value = """
            SELECT kc.*
            FROM knowledge_chunks kc
            WHERE to_tsvector('english', kc.content) @@ plainto_tsquery('english', :query)
            ORDER BY kc.quality_score DESC, kc.chunk_index ASC
            LIMIT 6
            """, nativeQuery = true)
    List<ManualKnowledgeChunkEntity> searchFallback(String query);
}