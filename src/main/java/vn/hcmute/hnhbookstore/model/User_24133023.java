package vn.hcmute.hnhbookstore.model;

import java.io.Serializable;
import java.time.LocalDateTime;

public final class User_24133023 implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private String email;
    private String fullname;
    private Integer phone;
    private String passwd;
    private String passwordSalt;
    private LocalDateTime signupDate;
    private LocalDateTime lastLogin;
    private boolean admin;
    private boolean verified;

    public User_24133023() {
    }

    public User_24133023(int id, String email, String fullname, Integer phone, String passwd,
                         String passwordSalt, LocalDateTime signupDate, LocalDateTime lastLogin,
                         boolean admin, boolean verified) {
        this.id = id;
        this.email = email;
        this.fullname = fullname;
        this.phone = phone;
        this.passwd = passwd;
        this.passwordSalt = passwordSalt;
        this.signupDate = signupDate;
        this.lastLogin = lastLogin;
        this.admin = admin;
        this.verified = verified;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public Integer getPhone() {
        return phone;
    }

    public void setPhone(Integer phone) {
        this.phone = phone;
    }

    public String getPasswd() {
        return passwd;
    }

    public void setPasswd(String passwd) {
        this.passwd = passwd;
    }

    public String getPasswordSalt() {
        return passwordSalt;
    }

    public void setPasswordSalt(String passwordSalt) {
        this.passwordSalt = passwordSalt;
    }

    public LocalDateTime getSignupDate() {
        return signupDate;
    }

    public void setSignupDate(LocalDateTime signupDate) {
        this.signupDate = signupDate;
    }

    public LocalDateTime getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(LocalDateTime lastLogin) {
        this.lastLogin = lastLogin;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }
}

