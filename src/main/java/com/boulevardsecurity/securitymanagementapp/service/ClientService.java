package com.boulevardsecurity.securitymanagementapp.service;

import com.boulevardsecurity.securitymanagementapp.model.Client;

import java.util.List;

public interface ClientService {

    // CREATE
    Client createClient(Client client);

    // READ - Un seul client
    Client getClientById(Long id);

    // READ - Tous les clients
    List<Client> getAllClients();

    // UPDATE
    Client updateClient(Long id, Client clientData);

    // DELETE
    void deleteClient(Long id);
}
