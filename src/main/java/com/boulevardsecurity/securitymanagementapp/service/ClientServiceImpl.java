package com.boulevardsecurity.securitymanagementapp.service;

import com.boulevardsecurity.securitymanagementapp.model.Client;
import com.boulevardsecurity.securitymanagementapp.repository.ClientRepository;
import com.boulevardsecurity.securitymanagementapp.service.ClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClientServiceImpl implements ClientService {

    private final ClientRepository clientRepository;

    /**
     * CREATE
     */
    @Override
    public Client createClient(Client client) {
        // Tu peux ajouter de la logique métier (validations, etc.)
        return clientRepository.save(client);
    }

    /**
     * READ - un seul client
     */
    @Override
    public Client getClientById(Long id) {
        return clientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Client introuvable : " + id));
    }

    /**
     * READ - liste de clients
     */
    @Override
    public List<Client> getAllClients() {
        return clientRepository.findAll();
    }

    /**
     * UPDATE
     */
    @Override
    public Client updateClient(Long id, Client clientData) {
        // On récupère le client existant
        Client existing = getClientById(id);

        // Mise à jour de tous les champs que tu souhaites autoriser
        existing.setTypeClient(clientData.getTypeClient());
        existing.setNom(clientData.getNom());
        existing.setPrenom(clientData.getPrenom());
        existing.setRaisonSociale(clientData.getRaisonSociale());
        existing.setNumeroSiret(clientData.getNumeroSiret());
        existing.setEmail(clientData.getEmail());
        existing.setTelephone(clientData.getTelephone());
        existing.setAdresse(clientData.getAdresse());
        existing.setCodePostal(clientData.getCodePostal());
        existing.setVille(clientData.getVille());
        existing.setPays(clientData.getPays());
        existing.setRemarques(clientData.getRemarques());
        existing.setModeContactPrefere(clientData.getModeContactPrefere());

        // Par défaut, on ne touche pas aux devisList et contrats,
        // sauf si tu souhaites explicitement les mettre à jour ici :
        // existing.setDevisList(clientData.getDevisList());
        // existing.setContrats(clientData.getContrats());

        // Sauvegarde après modification
        return clientRepository.save(existing);
    }

    /**
     * DELETE
     */
    @Override
    public void deleteClient(Long id) {
        Client existing = getClientById(id);
        clientRepository.delete(existing);
    }
}

