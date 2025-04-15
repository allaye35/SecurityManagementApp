package com.boulevardsecurity.securitymanagementapp.service;

import com.boulevardsecurity.securitymanagementapp.Enums.StatutDevis;
import com.boulevardsecurity.securitymanagementapp.model.Devis;
import com.boulevardsecurity.securitymanagementapp.repository.DevisRepository;
import com.boulevardsecurity.securitymanagementapp.service.DevisService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DevisServiceImpl implements DevisService {

    private final DevisRepository devisRepository;

    @Override
    public Devis createDevis(Devis devis) {
        // Logique métier si besoin (calcul, validations…)
        return devisRepository.save(devis);
    }

    @Override
    public Devis getDevisById(Long id) {
        return devisRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Devis introuvable : " + id));
    }

    @Override
    public List<Devis> getAllDevis() {
        return devisRepository.findAll();
    }

    @Override
    public Devis updateDevis(Long id, Devis devisData) {
        Devis existing = getDevisById(id);

        // Mise à jour des champs que tu souhaites gérer
        existing.setReferenceDevis(devisData.getReferenceDevis());
        existing.setDescription(devisData.getDescription());
        existing.setDateCreation(devisData.getDateCreation());
        existing.setDateValidite(devisData.getDateValidite());
        existing.setMontant(devisData.getMontant());
        existing.setMontantHT(devisData.getMontantHT());
        existing.setMontantTVA(devisData.getMontantTVA());
        existing.setMontantTTC(devisData.getMontantTTC());
        existing.setConditionsGenerales(devisData.getConditionsGenerales());
        existing.setStatut(devisData.getStatut());
        existing.setClient(devisData.getClient());
        existing.setEntreprise(devisData.getEntreprise());
        // Pour les missions, à toi de voir si tu les modifies ici :
        // existing.setMissions(devisData.getMissions());
        // Pareil pour le Contrat lié, etc.

        return devisRepository.save(existing);
    }

    @Override
    public void deleteDevis(Long id) {
        Devis existing = getDevisById(id);
        devisRepository.delete(existing);
    }

    /**
     * (Optionnel) Exemple d'acceptation de devis :
     */
    @Override
    public Devis accepterDevis(Long id) {
        Devis devis = getDevisById(id);
        devis.setStatut(StatutDevis.ACCEPTE_PAR_ENTREPRISE);
        return devisRepository.save(devis);
    }
}
