package com.boulevardsecurity.securitymanagementapp.controller;

import com.boulevardsecurity.securitymanagementapp.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    // 🔹 Envoi d'un email via JSON
    @PostMapping("/sendEmail")
    public ResponseEntity<String> sendEmail(@RequestBody Map<String, String> request) {
        String to = request.get("to");
        String subject = request.get("subject");
        String content = request.get("content");

        if (to == null || subject == null || content == null) {
            return ResponseEntity.badRequest().body("Les champs 'to', 'subject' et 'content' sont obligatoires.");
        }

        notificationService.sendEmail(to, subject, content);
        return ResponseEntity.ok("✅ Email envoyé avec succès à " + to);
    }

    // 🔹 Envoi d'un SMS
    // Envoi d'un SMS (Version qui accepte un JSON en entrée)
    @PostMapping("/sendSMS")
    public ResponseEntity<String> sendSMS(@RequestBody Map<String, String> request) {
        String phoneNumber = request.get("phoneNumber");
        String message = request.get("message");

        if (phoneNumber == null || message == null) {
            return ResponseEntity.badRequest().body("Les champs 'phoneNumber' et 'message' sont obligatoires.");
        }

        notificationService.sendSMS(phoneNumber, message);
        return ResponseEntity.ok(" SMS envoyé avec succès au numéro " + phoneNumber);
    }

}
