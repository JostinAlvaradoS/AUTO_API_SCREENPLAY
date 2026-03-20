package com.sofka.automation.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class EventResponse {
    private UUID id;
    private String name;
    private String description;
    private String eventDate;
    private String venue;
    private Integer maxCapacity;
    private Double basePrice;
    private String status;
    private String createdAt;
}
