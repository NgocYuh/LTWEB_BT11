package vn.hcmute.hnhbookstore.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.HexFormat;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public final class PasswordUtil_24133023 {
    private static final int ITERATIONS = 65536;
    private static final int KEY_LENGTH_BITS = 128; // 16 bytes -> 32 hex chars fits passwd varchar(32)
    private static final int SALT_BYTES = 16;       // 16 bytes -> 32 hex chars fits password_salt varchar(64)
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private PasswordUtil_24133023() {
    }

    public static String generateSalt() {
        byte[] salt = new byte[SALT_BYTES];
        SECURE_RANDOM.nextBytes(salt);
        return HexFormat.of().formatHex(salt);
    }

    public static String hashPassword(String password, String saltHex) {
        if (password == null || saltHex == null) {
            throw new IllegalArgumentException("Password and salt must not be null");
        }
        try {
            byte[] salt = HexFormat.of().parseHex(saltHex);
            PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, ITERATIONS, KEY_LENGTH_BITS);
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            byte[] hash = factory.generateSecret(spec).getEncoded();
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new IllegalStateException("PBKDF2WithHmacSHA256 calculation error", e);
        }
    }

    public static boolean verifyPassword(String rawPassword, String saltHex, String expectedHashHex) {
        if (rawPassword == null || saltHex == null || expectedHashHex == null) {
            return false;
        }
        String computedHashHex = hashPassword(rawPassword, saltHex);
        return MessageDigest.isEqual(
                computedHashHex.getBytes(StandardCharsets.UTF_8),
                expectedHashHex.getBytes(StandardCharsets.UTF_8)
        );
    }

    public static String hashOtp(String otp) {
        if (otp == null) {
            throw new IllegalArgumentException("OTP must not be null");
        }
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(otp.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 algorithm unavailable", e);
        }
    }

    public static boolean verifyOtp(String inputOtp, String expectedOtpHash) {
        if (inputOtp == null || expectedOtpHash == null) {
            return false;
        }
        String computedHash = hashOtp(inputOtp.trim());
        return MessageDigest.isEqual(
                computedHash.getBytes(StandardCharsets.UTF_8),
                expectedOtpHash.getBytes(StandardCharsets.UTF_8)
        );
    }

    public static String generateOtp6() {
        int code = SECURE_RANDOM.nextInt(1_000_000);
        return String.format("%06d", code);
    }
}

