package com.boulevardsecurity.securitymanagementapp.security;

import com.boulevardsecurity.securitymanagementapp.security.jwt.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtFilter;

    @Bean
    public PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(); }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration cfg) throws Exception {
        return cfg.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http:
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        configuration.setExposedHeaders(List.of("Authorization"));
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers(HttpMethod.GET,  "/api/auth/verify-email").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/auth/verify-email/code").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/auth/refresh").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/auth/password-reset/**").permitAll()
                        .requestMatchers(HttpMethod.GET,  "/api/auth/password-reset/**").permitAll()
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                        .requestMatchers("/actuator/health").permitAll()

.requestMatchers(HttpMethod.GET, "/api/sites/**").hasAnyAuthority("ADMIN", "AGENT_SECURITE", "CLIENT")
                        .requestMatchers(HttpMethod.POST, "/api/sites/**").hasAnyAuthority("ADMIN", "AGENT_SECURITE")
                        .requestMatchers(HttpMethod.PUT, "/api/sites/**").hasAnyAuthority("ADMIN", "AGENT_SECURITE")
                        .requestMatchers(HttpMethod.PATCH, "/api/sites/**").hasAnyAuthority("ADMIN", "AGENT_SECURITE")
                        .requestMatchers(HttpMethod.DELETE, "/api/sites/**").hasAuthority("ADMIN")

.requestMatchers(HttpMethod.GET, "/api/missions/**").hasAnyAuthority("ADMIN", "AGENT_SECURITE", "CLIENT")
                        .requestMatchers(HttpMethod.POST, "/api/missions/**").hasAnyAuthority("ADMIN", "AGENT_SECURITE")
                        .requestMatchers(HttpMethod.PUT, "/api/missions/**").hasAnyAuthority("ADMIN", "AGENT_SECURITE")
                        .requestMatchers(HttpMethod.PATCH, "/api/missions/**").hasAnyAuthority("ADMIN", "AGENT_SECURITE")
                        .requestMatchers(HttpMethod.DELETE, "/api/missions/**").hasAuthority("ADMIN")

.requestMatchers(HttpMethod.GET, "/api/plannings/**").hasAnyAuthority("ADMIN", "AGENT_SECURITE", "CLIENT")
                        .requestMatchers(HttpMethod.POST, "/api/plannings/**").hasAnyAuthority("ADMIN", "AGENT_SECURITE")
                        .requestMatchers(HttpMethod.PUT, "/api/plannings/**").hasAnyAuthority("ADMIN", "AGENT_SECURITE")
                        .requestMatchers(HttpMethod.PATCH, "/api/plannings/**").hasAnyAuthority("ADMIN", "AGENT_SECURITE")
                        .requestMatchers(HttpMethod.DELETE, "/api/plannings/**").hasAuthority("ADMIN")

.requestMatchers(HttpMethod.GET, "/api/pointages/**").hasAnyAuthority("ADMIN", "AGENT_SECURITE", "CLIENT")
                        .requestMatchers(HttpMethod.POST, "/api/pointages/**").hasAnyAuthority("ADMIN", "AGENT_SECURITE")
                        .requestMatchers(HttpMethod.PUT, "/api/pointages/**").hasAnyAuthority("ADMIN", "AGENT_SECURITE")
                        .requestMatchers(HttpMethod.PATCH, "/api/pointages/**").hasAnyAuthority("ADMIN", "AGENT_SECURITE")
                        .requestMatchers(HttpMethod.DELETE, "/api/pointages/**").hasAuthority("ADMIN")

.requestMatchers(HttpMethod.GET, "/api/rapports/**").hasAnyAuthority("ADMIN", "AGENT_SECURITE", "CLIENT")
                        .requestMatchers(HttpMethod.POST, "/api/rapports/**").hasAnyAuthority("ADMIN", "AGENT_SECURITE")
                        .requestMatchers(HttpMethod.PUT, "/api/rapports/**").hasAnyAuthority("ADMIN", "AGENT_SECURITE")
                        .requestMatchers(HttpMethod.PATCH, "/api/rapports/**").hasAnyAuthority("ADMIN", "AGENT_SECURITE")
                        .requestMatchers(HttpMethod.DELETE, "/api/rapports/**").hasAnyAuthority("ADMIN", "AGENT_SECURITE")

.requestMatchers(HttpMethod.GET, "/api/devis/**").hasAnyAuthority("ADMIN", "AGENT_SECURITE", "CLIENT")
                        .requestMatchers(HttpMethod.POST, "/api/devis/**").hasAnyAuthority("ADMIN", "AGENT_SECURITE")
                        .requestMatchers(HttpMethod.PUT, "/api/devis/**").hasAnyAuthority("ADMIN", "AGENT_SECURITE")
                        .requestMatchers(HttpMethod.PATCH, "/api/devis/**").hasAnyAuthority("ADMIN", "AGENT_SECURITE")
                        .requestMatchers(HttpMethod.DELETE, "/api/devis/**").hasAuthority("ADMIN")

.requestMatchers(HttpMethod.GET, "/api/factures/**").hasAnyAuthority("ADMIN", "AGENT_SECURITE", "CLIENT")
                        .requestMatchers(HttpMethod.POST, "/api/factures/**").hasAnyAuthority("ADMIN", "AGENT_SECURITE")
                        .requestMatchers(HttpMethod.PUT, "/api/factures/**").hasAnyAuthority("ADMIN", "AGENT_SECURITE")
                        .requestMatchers(HttpMethod.PATCH, "/api/factures/**").hasAnyAuthority("ADMIN", "AGENT_SECURITE")
                        .requestMatchers(HttpMethod.DELETE, "/api/factures/**").hasAuthority("ADMIN")

.requestMatchers(HttpMethod.GET, "/api/contrats/**").hasAnyAuthority("ADMIN", "AGENT_SECURITE", "CLIENT")
                        .requestMatchers(HttpMethod.POST, "/api/contrats/**").hasAnyAuthority("ADMIN", "AGENT_SECURITE")
                        .requestMatchers(HttpMethod.PUT, "/api/contrats/**").hasAnyAuthority("ADMIN", "AGENT_SECURITE")
                        .requestMatchers(HttpMethod.PATCH, "/api/contrats/**").hasAnyAuthority("ADMIN", "AGENT_SECURITE")
                        .requestMatchers(HttpMethod.DELETE, "/api/contrats/**").hasAuthority("ADMIN")

.requestMatchers(HttpMethod.GET, "/api/contrats-travail/**").hasAnyAuthority("ADMIN", "AGENT_SECURITE")
                        .requestMatchers(HttpMethod.POST, "/api/contrats-travail/**").hasAnyAuthority("ADMIN", "AGENT_SECURITE")
                        .requestMatchers(HttpMethod.PUT, "/api/contrats-travail/**").hasAnyAuthority("ADMIN", "AGENT_SECURITE")
                        .requestMatchers(HttpMethod.PATCH, "/api/contrats-travail/**").hasAnyAuthority("ADMIN", "AGENT_SECURITE")
                        .requestMatchers(HttpMethod.DELETE, "/api/contrats-travail/**").hasAuthority("ADMIN")

.requestMatchers(HttpMethod.GET, "/api/disponibilites/**").hasAnyAuthority("ADMIN", "AGENT_SECURITE")
                        .requestMatchers(HttpMethod.POST, "/api/disponibilites/**").hasAnyAuthority("ADMIN", "AGENT_SECURITE")
                        .requestMatchers(HttpMethod.PUT, "/api/disponibilites/**").hasAnyAuthority("ADMIN", "AGENT_SECURITE")
                        .requestMatchers(HttpMethod.PATCH, "/api/disponibilites/**").hasAnyAuthority("ADMIN", "AGENT_SECURITE")
                        .requestMatchers(HttpMethod.DELETE, "/api/disponibilites/**").hasAuthority("ADMIN")

.requestMatchers(HttpMethod.GET, "/api/zones/**").hasAnyAuthority("ADMIN", "AGENT_SECURITE")
                        .requestMatchers(HttpMethod.POST, "/api/zones/**").hasAnyAuthority("ADMIN", "AGENT_SECURITE")
                        .requestMatchers(HttpMethod.PUT, "/api/zones/**").hasAnyAuthority("ADMIN", "AGENT_SECURITE")
                        .requestMatchers(HttpMethod.PATCH, "/api/zones/**").hasAnyAuthority("ADMIN", "AGENT_SECURITE")
                        .requestMatchers(HttpMethod.DELETE, "/api/zones/**").hasAuthority("ADMIN")

.requestMatchers(HttpMethod.GET, "/api/agents/**").hasAnyAuthority("ADMIN", "AGENT_SECURITE")
                        .requestMatchers(HttpMethod.POST, "/api/agents/**").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/agents/**").hasAnyAuthority("ADMIN", "AGENT_SECURITE")
                        .requestMatchers(HttpMethod.PATCH, "/api/agents/**").hasAnyAuthority("ADMIN", "AGENT_SECURITE")
                        .requestMatchers(HttpMethod.DELETE, "/api/agents/**").hasAuthority("ADMIN")

.requestMatchers(HttpMethod.GET, "/api/clients/**").hasAnyAuthority("ADMIN", "AGENT_SECURITE", "CLIENT")
                        .requestMatchers(HttpMethod.POST, "/api/clients/**").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/clients/**").hasAnyAuthority("ADMIN", "CLIENT")
                        .requestMatchers(HttpMethod.PATCH, "/api/clients/**").hasAnyAuthority("ADMIN", "CLIENT")
                        .requestMatchers(HttpMethod.DELETE, "/api/clients/**").hasAuthority("ADMIN")

.requestMatchers(HttpMethod.GET, "/api/entreprises/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/entreprises/**").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/entreprises/**").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/entreprises/**").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/entreprises/**").hasAuthority("ADMIN")

.requestMatchers(HttpMethod.GET, "/api/diplomes/**").hasAnyAuthority("ADMIN", "AGENT_SECURITE")
                        .requestMatchers(HttpMethod.POST, "/api/diplomes/**").hasAnyAuthority("ADMIN", "AGENT_SECURITE")
                        .requestMatchers(HttpMethod.PUT, "/api/diplomes/**").hasAnyAuthority("ADMIN", "AGENT_SECURITE")
                        .requestMatchers(HttpMethod.PATCH, "/api/diplomes/**").hasAnyAuthority("ADMIN", "AGENT_SECURITE")
                        .requestMatchers(HttpMethod.DELETE, "/api/diplomes/**").hasAuthority("ADMIN")

.requestMatchers(HttpMethod.GET, "/api/cartes-pro/**").hasAnyAuthority("ADMIN", "AGENT_SECURITE")
                        .requestMatchers(HttpMethod.POST, "/api/cartes-pro/**").hasAnyAuthority("ADMIN", "AGENT_SECURITE")
                        .requestMatchers(HttpMethod.PUT, "/api/cartes-pro/**").hasAnyAuthority("ADMIN", "AGENT_SECURITE")
                        .requestMatchers(HttpMethod.PATCH, "/api/cartes-pro/**").hasAnyAuthority("ADMIN", "AGENT_SECURITE")
                        .requestMatchers(HttpMethod.DELETE, "/api/cartes-pro/**").hasAuthority("ADMIN")

.requestMatchers(HttpMethod.GET, "/api/fiches-paie/**").hasAnyAuthority("ADMIN", "AGENT_SECURITE")
                        .requestMatchers(HttpMethod.POST, "/api/fiches-paie/**").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/fiches-paie/**").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/fiches-paie/**").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/fiches-paie/**").hasAuthority("ADMIN")

.requestMatchers(HttpMethod.GET, "/api/notifications/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/notifications/**").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/notifications/**").authenticated()
                        .requestMatchers(HttpMethod.PATCH, "/api/notifications/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/notifications/**").hasAuthority("ADMIN")

.requestMatchers("/api/admin/**").hasAuthority("ADMIN")

.anyRequest().authenticated()
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}