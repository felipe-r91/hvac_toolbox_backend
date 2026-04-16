package com.tech.hvac_backend.dto.response;

import lombok.Getter;

@Getter
public class MachineTimelineItemResponse {

    private final String id;
    private final String type;
    private final String date;
    private final String status;
    private final String title;
    private final String summary;

    public MachineTimelineItemResponse(
            String id,
            String type,
            String date,
            String status,
            String title,
            String summary
    ) {
        this.id = id;
        this.type = type;
        this.date = date;
        this.status = status;
        this.title = title;
        this.summary = summary;
    }

}