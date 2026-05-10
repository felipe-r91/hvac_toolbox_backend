package com.tech.hvac_backend.dto.sync;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class DailyBatchSyncRequest {

    private List<DailySyncRequest> drafts;
}
