package com.boulevardsecurity.securitymanagementapp.service;

import com.boulevardsecurity.securitymanagementapp.model.ContratDeTravail;
import com.boulevardsecurity.securitymanagementapp.repository.ContratDeTravailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ContratDeTravailService {

    private final ContratDeTravailRepository contratDeTravailRepository;

    @Autowired
    public ContratDeTravailService(ContratDeTravailRepository contratDeTravailRepository) {
        this.contratDeTravailRepository = contratDeTravailRepository;
    }

    public List<ContratDeTravail> getAllContrats() {
        return contratDeTravailRepository.findAll();
    }

    public Optional<ContratDeTravail> getContratById(Long id) {
        return contratDeTravailRepository.findById(id);
    }

    public ContratDeTravail createContrat(ContratDeTravail contratDeTravail) {
        return contratDeTravailRepository.save(contratDeTravail);
    }

    public ContratDeTravail updateContrat(Long id, ContratDeTravail updatedContrat) {
        return contratDeTravailRepository.findById(id)
                .map(contrat -> {
                    contrat.setDateDebut(updatedContrat.getDateDebut());
                    contrat.setDateFin(updatedContrat.getDateFin());
                    contrat.setSalaire(updatedContrat.getSalaire());
                    return contratDeTravailRepository.save(contrat);
                })
                .orElse(null);
    }

    public void deleteContrat(Long id) {
        contratDeTravailRepository.deleteById(id);
    }
}
