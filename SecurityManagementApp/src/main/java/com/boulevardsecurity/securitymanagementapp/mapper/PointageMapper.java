    // src/main/java/com/boulevardsecurity/securitymanagementapp/mapper/PointageMapper.java
    package com.boulevardsecurity.securitymanagementapp.mapper;

    import com.boulevardsecurity.securitymanagementapp.dto.PointageCreateDto;
    import com.boulevardsecurity.securitymanagementapp.dto.PointageDto;
    import com.boulevardsecurity.securitymanagementapp.model.GeoPoint;
    import com.boulevardsecurity.securitymanagementapp.model.Pointage;
    import com.boulevardsecurity.securitymanagementapp.repository.MissionRepository;
    import lombok.RequiredArgsConstructor;
    import org.springframework.stereotype.Component;

    @Component
    @RequiredArgsConstructor
    public class PointageMapper {

        private final MissionRepository missionRepo;

        public PointageDto toDto(Pointage ent) {
            var pos = ent.getPositionActuelle();
            return PointageDto.builder()
                    .id(ent.getId())
                    .datePointage(ent.getDatePointage())
                    .estPresent(ent.isEstPresent())
                    .estRetard(ent.isEstRetard())
                    .latitude(pos != null ? pos.getLatitude() : 0.0)
                    .longitude(pos != null ? pos.getLongitude() : 0.0)
                    .missionId(ent.getMission() != null ? ent.getMission().getId() : null)
                    .build();
        }

        public Pointage toEntity(PointageCreateDto dto) {
            GeoPoint point = GeoPoint.builder()
                    .latitude(dto.getLatitude())
                    .longitude(dto.getLongitude())
                    .build();

            var mission = missionRepo.findById(dto.getMissionId())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Mission introuvable, id=" + dto.getMissionId()));

            return Pointage.builder()
                    .datePointage(dto.getDatePointage())
                    .estPresent(dto.isEstPresent())
                    .estRetard(dto.isEstRetard())
                    .positionActuelle(point)
                    .mission(mission)
                    .build();
        }

        public void updateEntity(PointageCreateDto dto, Pointage ent) {
            if (dto.getDatePointage() != null) {
                ent.setDatePointage(dto.getDatePointage());
            }
            ent.setEstPresent(dto.isEstPresent());
            ent.setEstRetard(dto.isEstRetard());

            if (ent.getPositionActuelle() == null) {
                ent.setPositionActuelle(new GeoPoint());
            }
            ent.getPositionActuelle().setLatitude(dto.getLatitude());
            ent.getPositionActuelle().setLongitude(dto.getLongitude());

            if (dto.getMissionId() != null
                    && (ent.getMission() == null || !dto.getMissionId().equals(ent.getMission().getId()))) {
                var mission = missionRepo.findById(dto.getMissionId())
                        .orElseThrow(() -> new IllegalArgumentException(
                                "Mission introuvable, id=" + dto.getMissionId()));
                ent.setMission(mission);
            }
        }
    }
