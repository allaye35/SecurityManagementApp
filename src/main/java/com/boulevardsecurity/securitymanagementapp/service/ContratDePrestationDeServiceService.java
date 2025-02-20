package com.boulevardsecurity.securitymanagementapp.service;

import com.boulevardsecurity.securitymanagementapp.model.ContratDePrestationDeService;
import com.boulevardsecurity.securitymanagementapp.repository.ContratDePrestationDeServiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ContratDePrestationDeServiceService {

    private final ContratDePrestationDeServiceRepository repository;

    public List<ContratDePrestationDeService> getAllContrats() {
        return repository.findAll();
    }

    public Optional<ContratDePrestationDeService> getContratById(Long id) {
        return repository.findById(id);
    }

    public ContratDePrestationDeService createContrat(ContratDePrestationDeService contrat) {
        return repository.save(contrat);
    }

    public ContratDePrestationDeService updateContrat(Long id, ContratDePrestationDeService updatedContrat) {
        return repository.findById(id)
                .map(contrat -> {
                    contrat.setDateDebut(updatedContrat.getDateDebut());
                    contrat.setDateFin(updatedContrat.getDateFin());
                    contrat.setMontant(updatedContrat.getMontant());
                    contrat.setDescription(updatedContrat.getDescription());
                    return repository.save(contrat);
                })
                .orElse(null);
    }

    public void deleteContrat(Long id) {
        repository.deleteById(id);
    }
}
