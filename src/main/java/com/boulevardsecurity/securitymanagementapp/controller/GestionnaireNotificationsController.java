package com.boulevardsecurity.securitymanagementapp.controller;

import com.boulevardsecurity.securitymanagementapp.model.GestionnaireNotifications;
import com.boulevardsecurity.securitymanagementapp.service.GestionnaireNotificationsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/notifications")
public class GestionnaireNotificationsController {

    @Autowired
    private GestionnaireNotificationsService notificationService;

    @GetMapping
    public List<GestionnaireNotifications> getAllNotifications() {
        return notificationService.getAllNotifications();
    }

    @GetMapping("/{id}")
    public ResponseEntity<GestionnaireNotifications> getNotificationById(@PathVariable Long id) {
        Optional<GestionnaireNotifications> notification = notificationService.getNotificationById(id);
        return notification.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public GestionnaireNotifications createNotification(@RequestBody GestionnaireNotifications notification) {
        return notificationService.createNotification(notification);
    }

    @PutMapping("/{id}")
    public ResponseEntity<GestionnaireNotifications> updateNotification(@PathVariable Long id, @RequestBody GestionnaireNotifications updatedNotification) {
        GestionnaireNotifications notif = notificationService.updateNotification(id, updatedNotification);
        return notif != null ? ResponseEntity.ok(notif) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNotification(@PathVariable Long id) {
        notificationService.deleteNotification(id);
        return ResponseEntity.noContent().build();
    }
}
