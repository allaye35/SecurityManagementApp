package com.boulevardsecurity.securitymanagementapp.controller;
import com.boulevardsecurity.securitymanagementapp.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    // Envoi d'un email
    @PostMapping("/sendEmail")
    public ResponseEntity<String> sendEmail(@RequestParam String to, @RequestParam String subject, @RequestParam String content) {
        notificationService.sendEmail(to, subject, content);
        return ResponseEntity.ok("✅ Email envoyé avec succès à " + to);
    }

    // Envoi d'un SMS
    @PostMapping("/sendSMS")
    public ResponseEntity<String> sendSMS(@RequestParam String phoneNumber, @RequestParam String message) {
        notificationService.sendSMS(phoneNumber, message);
        return ResponseEntity.ok("✅ SMS envoyé avec succès au numéro " + phoneNumber);
    }
}

