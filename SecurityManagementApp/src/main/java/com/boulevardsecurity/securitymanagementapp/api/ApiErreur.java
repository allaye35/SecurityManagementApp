package com.boulevardsecurity.securitymanagementapp.api;

import java.time.Instant;

public record ApiErreur(
        Instant timestamp,
        int     status,
        String  erreur,
        String  message,
        String  path) {}

