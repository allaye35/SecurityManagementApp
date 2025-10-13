package com.boulevardsecurity.securitymanagementapp.model;

import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class GeoPoint {
    private double latitude;
    private double longitude;
}
