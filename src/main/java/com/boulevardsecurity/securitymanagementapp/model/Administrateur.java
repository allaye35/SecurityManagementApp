//package com.boulevardsecurity.securitymanagementapp.model;
//
//import com.boulevardsecurity.securitymanagementapp.Enums.Role;
//import jakarta.persistence.*;
//import jakarta.validation.constraints.Email;
//import jakarta.validation.constraints.NotBlank;
//import lombok.*;
//
//@Entity
//@Table(name = "administrateurs")
//@Getter
//@Setter
//@NoArgsConstructor
//@AllArgsConstructor
//@Builder
//public class Administrateur {
//
//    private static final long serialVersionUID = 1L;
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @Column(nullable = false, unique = true)
//    @NotBlank(message = "Le nom d'utilisateur est obligatoire")
//    private String username;
//
//    @Column(nullable = false)
//    @NotBlank(message = "Le mot de passe est obligatoire")
//    private String password;
//
//    @Column(nullable = false)
//    @Email(message = "L'email doit être valide")
//    private String email;
//
//
//    @Enumerated(EnumType.STRING)
//    private Role role = Role.ADMIN; // Rôle par défaut ADMIN
//}
