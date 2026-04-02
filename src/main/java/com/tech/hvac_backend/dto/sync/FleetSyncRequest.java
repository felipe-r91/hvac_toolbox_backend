package com.tech.hvac_backend.dto.sync;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class FleetSyncRequest {

    private List<VesselSyncDto> vessels;

    public FleetSyncRequest() {
    }

}