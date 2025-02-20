package com.boulevardsecurity.securitymanagementapp.service;

import com.boulevardsecurity.securitymanagementapp.model.Client;
import com.boulevardsecurity.securitymanagementapp.repository.ClientRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ClientService {

    private final ClientRepository clientRepository;

    public ClientService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    public Client createClient(Client client) {
        return clientRepository.save(client);
    }

    public List<Client> getAllClients() {
        return clientRepository.findAll();
    }

    public Optional<Client> getClientById(Long id) {
        return clientRepository.findById(id);
    }

    public Client updateClient(Long id, Client updatedClient) {
        return clientRepository.findById(id)
                .map(client -> {
                    client.setNom(updatedClient.getNom());
                    client.setAdresse(updatedClient.getAdresse());
                    client.setEmail(updatedClient.getEmail());
                    client.setTelephone(updatedClient.getTelephone());
                    return clientRepository.save(client);
                })
                .orElse(null);
    }

    public void deleteClient(Long id) {
        clientRepository.deleteById(id);
    }
}
