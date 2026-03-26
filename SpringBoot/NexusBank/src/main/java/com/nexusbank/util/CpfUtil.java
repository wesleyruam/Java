package com.nexusbank.util;

public final class CpfUtil {

    private CpfUtil() {}

    /**
     * Validates a Brazilian CPF number (format: 000.000.000-00 or 00000000000).
     */
    public static boolean isValid(String cpf) {
        if (cpf == null) return false;

        String digits = cpf.replaceAll("[^\\d]", "");

        if (digits.length() != 11) return false;

        // Reject all same-digit CPFs (e.g., 111.111.111-11)
        if (digits.matches("(\\d)\\1{10}")) return false;

        int sum = 0;
        for (int i = 0; i < 9; i++) {
            sum += Character.getNumericValue(digits.charAt(i)) * (10 - i);
        }
        int firstDigit = 11 - (sum % 11);
        if (firstDigit >= 10) firstDigit = 0;
        if (firstDigit != Character.getNumericValue(digits.charAt(9))) return false;

        sum = 0;
        for (int i = 0; i < 10; i++) {
            sum += Character.getNumericValue(digits.charAt(i)) * (11 - i);
        }
        int secondDigit = 11 - (sum % 11);
        if (secondDigit >= 10) secondDigit = 0;
        return secondDigit == Character.getNumericValue(digits.charAt(10));
    }

    public static String format(String raw) {
        String d = raw.replaceAll("[^\\d]", "");
        return d.replaceAll("(\\d{3})(\\d{3})(\\d{3})(\\d{2})", "$1.$2.$3-$4");
    }
}
