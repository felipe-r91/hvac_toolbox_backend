package com.tech.hvac_backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "photo_records")
public class PhotoRecordEntity {

    @Id
    private String id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private PhotoOwnerType ownerType;

    @Column(nullable = false)
    private String ownerId;

    @Column(nullable = false)
    private String machineId;

    private String taskId;

    private String filename;

    @Column(length = 2000)
    private String caption;

    @Column(nullable = false)
    private String createdAt;

    @Column(nullable = false, length = 4000)
    private String storageKey;

    @Column(length = 4000)
    private String previewUrl;

    public PhotoRecordEntity() {
    }
}