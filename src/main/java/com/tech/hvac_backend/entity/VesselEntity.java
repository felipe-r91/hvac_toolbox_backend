package com.tech.hvac_backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "vessels")
public class VesselEntity {

    @Id
    private String id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String imoNumber;

    private String vesselType;
    private String ownerCustomer;
    private String vesselContact;

    @Column(length = 4000)
    private String description;

    public VesselEntity() {
    }
}
