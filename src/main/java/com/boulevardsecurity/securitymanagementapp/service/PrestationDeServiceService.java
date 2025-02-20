package com.boulevardsecurity.securitymanagementapp.service;

import com.boulevardsecurity.securitymanagementapp.model.PrestationDeService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class PrestationDeServiceService {
    private final List<PrestationDeService> prestations = new ArrayList<>();
    private final AtomicLong counter = new AtomicLong(1);

    public List<PrestationDeService> getAllPrestations() {
        return prestations;
    }

    public Optional<PrestationDeService> getPrestationById(Long id) {
        return prestations.stream()
                .filter(prestation -> prestation.getId().equals(id))
                .findFirst();
    }

    public PrestationDeService createPrestation(PrestationDeService prestation) {
        prestation.setId(counter.getAndIncrement());
        prestations.add(prestation);
        return prestation;
    }

    public PrestationDeService updatePrestation(Long id, PrestationDeService updatedPrestation) {
        Optional<PrestationDeService> prestationOptional = getPrestationById(id);
        if (prestationOptional.isPresent()) {
            PrestationDeService prestation = prestationOptional.get();
            prestation.setDescription(updatedPrestation.getDescription());
            prestation.setDateDebut(updatedPrestation.getDateDebut());
            prestation.setDateFin(updatedPrestation.getDateFin());
            prestation.setMontant(updatedPrestation.getMontant());
            return prestation;
        }
        return null;
    }

    public boolean deletePrestation(Long id) {
        return prestations.removeIf(prestation -> prestation.getId().equals(id));
    }
}
