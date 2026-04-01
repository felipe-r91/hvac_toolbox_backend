package com.tech.hvac_backend.dto.sync;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class CorrectiveBatchSyncRequest {

    private List<CorrectiveSyncRequest> drafts;

    public CorrectiveBatchSyncRequest() {
    }

}