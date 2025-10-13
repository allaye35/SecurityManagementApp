package com.boulevardsecurity.securitymanagementapp.service.impl;

import com.boulevardsecurity.securitymanagementapp.dto.ClientCreateDto;
import com.boulevardsecurity.securitymanagementapp.dto.ClientDto;
import com.boulevardsecurity.securitymanagementapp.mapper.ClientMapper;
import com.boulevardsecurity.securitymanagementapp.model.Client;
import com.boulevardsecurity.securitymanagementapp.Enums.Role;
import com.boulevardsecurity.securitymanagementapp.repository.ClientRepository;
import com.boulevardsecurity.securitymanagementapp.service.ClientService;
import com.boulevardsecurity.securitymanagementapp.util.EmailUtil;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClientServiceImpl implements ClientService {

    private final ClientRepository repo;
    private final ClientMapper mapper;
    private final org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    @Override
    public ClientDto createClient(ClientCreateDto dto) {
        Client ent = mapper.toEntity(dto);
        ent.setEmail(EmailUtil.normalize(ent.getEmail()));
        ent.setPassword(passwordEncoder.encode(ent.getPassword()));
        ent.setEmailVerified(false);
        ent.setAdminApproved(false);
        ent.setPasswordChangedAt(Instant.now());
        Client saved = repo.save(ent);
        return mapper.toDto(saved);
    }

    @Override @Transactional(readOnly = true)
    public List<ClientDto> getAllClients() {
        return repo.findAll().stream().map(mapper::toDto).collect(Collectors.toList());
    }

    @Override @Transactional(readOnly = true)
    public Optional<ClientDto> getClientById(Long id) {
        return repo.findById(id).map(mapper::toDto);
    }

    @Override @Transactional(readOnly = true)
    public Optional<ClientDto> getClientByEmail(String email) {
        return repo.findByEmail(EmailUtil.normalize(email)).map(mapper::toDto);
    }

    @Override @Transactional(readOnly = true)
    public Optional<ClientDto> getClientByNom(String nom) {
        return repo.findByNom(nom).map(mapper::toDto);
    }

    @Override
    @Transactional
    public ClientDto updateClient(Long id, ClientDto dto) {
        Client existing = repo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Client introuvable : " + id));
        mapper.updateEntityFromDto(dto, existing);
        Client saved = repo.save(existing);
        return mapper.toDto(saved);
    }

    @Override
    @Transactional
    public ClientDto updateClientRole(Long id, String newRole) {
        Client existing = repo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Client introuvable : " + id));
        existing.setRole(Role.valueOf(newRole));
        Client saved = repo.save(existing);
        return mapper.toDto(saved);
    }

    @Override
    public void deleteClient(Long id) {
        if (!repo.existsById(id)) throw new EntityNotFoundException("Client introuvable : " + id);
        repo.deleteById(id);
    }

    @Override @Transactional(readOnly = true)
    public List<ClientDto> getPendingApprovalClients() {
        return repo.findByEmailVerifiedIsTrueAndAdminApprovedIsFalse()
                .stream().map(mapper::toDto).collect(Collectors.toList());
    }

    @Override
    public ClientDto approveClient(Long id) {
        Client c = repo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Client introuvable : " + id));
        c.setAdminApproved(true);
        return mapper.toDto(repo.save(c));
    }
}