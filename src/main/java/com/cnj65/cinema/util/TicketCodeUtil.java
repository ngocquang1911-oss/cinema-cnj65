package com.cnj65.cinema.util;

import java.security.SecureRandom;

/**
 * Sinh ma ve (ticket code) duy nhat, de doc, dung de check-in tai rap.
 * Vi du: CNJ-7K3X9P
 */
public class TicketCodeUtil {

    private static final String CHARS = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789"; // bo O,0,I,1 de tranh nham lan
    private static final SecureRandom RANDOM = new SecureRandom();

    private TicketCodeUtil() {}

    public static String generate() {
        StringBuilder sb = new StringBuilder("CNJ-");
        for (int i = 0; i < 7; i++) {
            sb.append(CHARS.charAt(RANDOM.nextInt(CHARS.length())));
        }
        return sb.toString();
    }
}
