package com.cnj65.cinema.util;

import org.mindrot.jbcrypt.BCrypt;

/**
 * Ma hoa va kiem tra mat khau bang BCrypt.
 * KHONG BAO GIO luu mat khau dang plain-text trong DB.
 */
public class PasswordUtil {

    private PasswordUtil() {}

    public static String hash(String plainPassword) {
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(10));
    }

    public static boolean verify(String plainPassword, String hashedPassword) {
        try {
            return BCrypt.checkpw(plainPassword, hashedPassword);
        } catch (IllegalArgumentException e) {
            return false; // hash khong hop le
        }
    }
}
