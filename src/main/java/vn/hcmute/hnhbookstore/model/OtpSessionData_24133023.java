package vn.hcmute.hnhbookstore.model;

import java.io.Serializable;

public final class OtpSessionData_24133023 implements Serializable {
    private static final long serialVersionUID = 1L;

    private int userId;
    private String email;
    private String fullname;
    private String hashedOtp;
    private long expireAt;
    private int attempts;
    private long lastSentAt;

    public OtpSessionData_24133023() {
    }

    public OtpSessionData_24133023(int userId, String email, String fullname, String hashedOtp,
                                  long expireAt, int attempts, long lastSentAt) {
        this.userId = userId;
        this.email = email;
        this.fullname = fullname;
        this.hashedOtp = hashedOtp;
        this.expireAt = expireAt;
        this.attempts = attempts;
        this.lastSentAt = lastSentAt;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getHashedOtp() {
        return hashedOtp;
    }

    public void setHashedOtp(String hashedOtp) {
        this.hashedOtp = hashedOtp;
    }

    public long getExpireAt() {
        return expireAt;
    }

    public void setExpireAt(long expireAt) {
        this.expireAt = expireAt;
    }

    public int getAttempts() {
        return attempts;
    }

    public void setAttempts(int attempts) {
        this.attempts = attempts;
    }

    public long getLastSentAt() {
        return lastSentAt;
    }

    public void setLastSentAt(long lastSentAt) {
        this.lastSentAt = lastSentAt;
    }
}

