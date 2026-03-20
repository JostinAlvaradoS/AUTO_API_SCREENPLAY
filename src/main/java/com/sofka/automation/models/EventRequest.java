package com.sofka.automation.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EventRequest {
    private String name;
    private String description;
    private String eventDate;
    private String venue;
    private Integer maxCapacity;
    private Double basePrice;
}
