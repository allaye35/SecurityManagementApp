package com.boulevardsecurity.securitymanagementapp.service;

import com.boulevardsecurity.securitymanagementapp.dto.GeolocalisationGpsCreateDto;
import com.boulevardsecurity.securitymanagementapp.dto.GeolocalisationGpsDto;

import java.util.List;
import java.util.Optional;

public interface GeolocalisationGpsService {
    List<GeolocalisationGpsDto> getAll();
    Optional<GeolocalisationGpsDto> getById(Long id);
    GeolocalisationGpsDto create(GeolocalisationGpsCreateDto dto);
    GeolocalisationGpsDto update(Long id, GeolocalisationGpsCreateDto dto);
    void delete(Long id);
    
    List<Long> addMission(Long gpsId, Long missionId);
}