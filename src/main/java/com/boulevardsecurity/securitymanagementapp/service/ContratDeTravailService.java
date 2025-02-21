package com.boulevardsecurity.securitymanagementapp.service;

import com.boulevardsecurity.securitymanagementapp.model.ContratDeTravail;
import com.boulevardsecurity.securitymanagementapp.repository.ContratDeTravailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class ContratDeTravailService {

    private final ContratDeTravailRepository contratRepository;

    @Autowired
    public ContratDeTravailService(ContratDeTravailRepository contratRepository) {
        this.contratRepository = contratRepository;
    }

    public List<ContratDeTravail> getAllContrats() {
        return contratRepository.findAll();
    }

    public Optional<ContratDeTravail> getContratById(Long id) {
        return contratRepository.findById(id);
    }

    public ContratDeTravail createContrat(ContratDeTravail contrat) {
        return contratRepository.save(contrat);
    }

    public ContratDeTravail updateContrat(Long id, ContratDeTravail updatedContrat) {
        return contratRepository.findById(id).map(contrat -> {
            contrat.setDateDebut(updatedContrat.getDateDebut());
            contrat.setDateFin(updatedContrat.getDateFin());
            contrat.setSignatureElectronique(updatedContrat.getSignatureElectronique());
            return contratRepository.save(contrat);
        }).orElse(null);
    }

    public void deleteContrat(Long id) {
        contratRepository.deleteById(id);
    }

    public boolean prolongerContrat(Long id, LocalDate nouvelleDateFin) {
        return contratRepository.findById(id).map(contrat -> {
            contrat.prolongerContrat(nouvelleDateFin);
            contratRepository.save(contrat);
            return true;
        }).orElse(false);
    }
}
