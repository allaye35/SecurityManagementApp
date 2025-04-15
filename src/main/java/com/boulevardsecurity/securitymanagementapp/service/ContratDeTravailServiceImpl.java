package com.boulevardsecurity.securitymanagementapp.service;

import com.boulevardsecurity.securitymanagementapp.model.ContratDeTravail;
import com.boulevardsecurity.securitymanagementapp.repository.ContratDeTravailRepository;
import com.boulevardsecurity.securitymanagementapp.service.ContratDeTravailService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ContratDeTravailServiceImpl implements ContratDeTravailService {

    private final ContratDeTravailRepository contratRepository;

    @Override
    public ContratDeTravail createContrat(ContratDeTravail contrat) {
        return contratRepository.save(contrat);
    }

    @Override
    public Optional<ContratDeTravail> getContratById(Long id) {
        return contratRepository.findById(id);
    }

    @Override
    public List<ContratDeTravail> getAllContrats() {
        return contratRepository.findAll();
    }

    @Override
    public ContratDeTravail updateContrat(Long id, ContratDeTravail updatedContrat) {
        Optional<ContratDeTravail> existingOpt = getContratById(id);
        if (existingOpt.isEmpty()) {
            return null; // Ou lever une exception si tu préfères
        }
        ContratDeTravail existing = existingOpt.get();
        existing.setAgentDeSecurite(updatedContrat.getAgentDeSecurite());
        existing.setTypeContrat(updatedContrat.getTypeContrat());
        existing.setDateDebut(updatedContrat.getDateDebut());
        existing.setDateFin(updatedContrat.getDateFin());
        existing.setSalaireDeBase(updatedContrat.getSalaireDeBase());
        // Fiches de paie -> gérées ici si tu le souhaites

        return contratRepository.save(existing);
    }

    @Override
    public void deleteContrat(Long id) {
        contratRepository.deleteById(id);
    }

    @Override
    public boolean prolongerContrat(Long id, LocalDate nouvelleDateFin) {
        Optional<ContratDeTravail> optionalContrat = getContratById(id);
        if (optionalContrat.isPresent()) {
            ContratDeTravail contrat = optionalContrat.get();
            contrat.setDateFin(nouvelleDateFin);
            contratRepository.save(contrat);
            return true;
        }
        return false;
    }

    @Override
    public List<ContratDeTravail> getContratsByAgentId(Long agentId) {
        return contratRepository.findByAgentDeSecuriteId(agentId);
    }

}
