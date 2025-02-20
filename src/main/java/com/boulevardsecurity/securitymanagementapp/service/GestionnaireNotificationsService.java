package com.boulevardsecurity.securitymanagementapp.service;

import com.boulevardsecurity.securitymanagementapp.model.GestionnaireNotifications;
import com.boulevardsecurity.securitymanagementapp.repository.GestionnaireNotificationsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GestionnaireNotificationsService {

    @Autowired
    private GestionnaireNotificationsRepository notificationRepository;

    public List<GestionnaireNotifications> getAllNotifications() {
        return notificationRepository.findAll();
    }

    public Optional<GestionnaireNotifications> getNotificationById(Long id) {
        return notificationRepository.findById(id);
    }

    public GestionnaireNotifications createNotification(GestionnaireNotifications notification) {
        return notificationRepository.save(notification);
    }

    public GestionnaireNotifications updateNotification(Long id, GestionnaireNotifications updatedNotification) {
        return notificationRepository.findById(id).map(notif -> {
            notif.setTitre(updatedNotification.getTitre());
            notif.setMessage(updatedNotification.getMessage());
            notif.setDestinataire(updatedNotification.getDestinataire());
            return notificationRepository.save(notif);
        }).orElse(null);
    }

    public void deleteNotification(Long id) {
        notificationRepository.deleteById(id);
    }
}
