// src/main/java/com/boulevardsecurity/securitymanagementapp/util/TokenUtil.java
package com.boulevardsecurity.securitymanagementapp.util;

import java.security.SecureRandom;
import java.util.HexFormat;

public final class TokenUtil {
    private static final SecureRandom RNG = new SecureRandom();
    private TokenUtil() {}

    /** Token aléatoire binaire encodé hex – pour les liens. */
    public static String generateRawToken() {
        byte[] bytes = new byte[32];
        RNG.nextBytes(bytes);
        return HexFormat.of().formatHex(bytes);
    }

    /** SHA-256 en hex (64 chars) */
    public static String sha256Hex(String s) {
        try {
            var md = java.security.MessageDigest.getInstance("SHA-256");
            byte[] out = md.digest(s.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(out);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /** Génère un code numérique (ex: 6 chiffres). */
    public static String generateNumericCode(int length) {
        int min = (int) Math.pow(10, length - 1);   // 100000 pour 6
        int max = (int) Math.pow(10, length) - 1;   // 999999 pour 6
        int n = RNG.nextInt((max - min) + 1) + min;
        return Integer.toString(n);
    }
}
