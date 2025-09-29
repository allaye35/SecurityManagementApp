package com.boulevardsecurity.securitymanagementapp.util;

public final class EmailUtil {
    private EmailUtil() {}

    public static String normalize(String email) {
        return email == null ? null : email.trim().toLowerCase();
    }

    public static boolean equalsNormalized(String a, String b) {
        String aa = normalize(a), bb = normalize(b);
        return aa != null && aa.equals(bb);
    }
}
