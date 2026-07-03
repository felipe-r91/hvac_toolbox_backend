package com.tech.hvac_backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@Entity
@Table(name = "parts")
public class PartEntity {

    @Id
    private String id;

    @Column(nullable = false)
    private String jciPartNumber;

    @Column(nullable = false)
    private String manufacturerModel;

    @Column(nullable = false)
    private String manufacturerCode;

    @Column(nullable = false)
    private String tag;

    @Convert(converter = StringListJsonConverter.class)
    @Column(nullable = false, columnDefinition = "text")
    private List<String> machinesModelHavingIt = new ArrayList<>();

    @Column(nullable = false, length = 4000)
    private String description;

    @Column(length = 36)
    private String partPhotoId;

    @Column(length = 4000)
    private String partPhotoPreviewUrl;

    public PartEntity() {
    }
}
