package vn.hcmute.hnhbookstore.business;

import vn.hcmute.hnhbookstore.data.UserDao_24133023;
import vn.hcmute.hnhbookstore.model.OtpSessionData_24133023;
import vn.hcmute.hnhbookstore.model.User_24133023;
import vn.hcmute.hnhbookstore.util.PasswordUtil_24133023;

import java.sql.SQLException;
import java.util.regex.Pattern;

public final class AuthService_24133023 {
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    private static final int OTP_EXPIRE_SECONDS = 300; // 5 minutes
    private static final int OTP_RESEND_COOLDOWN_SECONDS = 60; // 60 seconds
    private static final int MAX_OTP_ATTEMPTS = 5;

    private final UserDao_24133023 userDao;
    private final MailService_24133023 mailService;

    public AuthService_24133023() {
        this(new UserDao_24133023(), new SmtpMailService_24133023());
    }

    public AuthService_24133023(UserDao_24133023 userDao, MailService_24133023 mailService) {
        this.userDao = userDao;
        this.mailService = mailService;
    }

    public OtpSessionData_24133023 register(String email, String fullname, String phoneStr,
                                           String password, String confirmPassword)
            throws AuthException_24133023, SQLException {
        if (email == null || email.isBlank()) {
            throw new AuthException_24133023("Email không được để trống.");
        }
        String cleanEmail = email.trim();
        if (cleanEmail.length() > 50) {
            throw new AuthException_24133023("Email không được vượt quá 50 ký tự.");
        }
        if (!EMAIL_PATTERN.matcher(cleanEmail).matches()) {
            throw new AuthException_24133023("Định dạng email không hợp lệ.");
        }

        if (fullname == null || fullname.isBlank()) {
            throw new AuthException_24133023("Họ và tên không được để trống.");
        }
        String cleanFullname = fullname.trim();
        if (cleanFullname.length() > 50) {
            throw new AuthException_24133023("Họ và tên không được vượt quá 50 ký tự.");
        }

        Integer phone = null;
        if (phoneStr != null && !phoneStr.isBlank()) {
            String cleanPhone = phoneStr.trim();
            try {
                long phoneLong = Long.parseLong(cleanPhone);
                if (phoneLong < 0 || phoneLong > Integer.MAX_VALUE) {
                    throw new AuthException_24133023(
                        "Số điện thoại vượt quá giới hạn kiểu INT của SQL Server (tối đa 2.147.483.647) và không hỗ trợ số 0 ở đầu."
                    );
                }
                phone = (int) phoneLong;
            } catch (NumberFormatException e) {
                throw new AuthException_24133023(
                    "Số điện thoại chỉ chấp nhận số nguyên hợp lệ trong giới hạn INT của SQL Server, không chứa ký tự lạ."
                );
            }
        }

        if (password == null || password.length() < 6) {
            throw new AuthException_24133023("Mật khẩu phải có ít nhất 6 ký tự.");
        }
        if (!password.equals(confirmPassword)) {
            throw new AuthException_24133023("Mật khẩu xác nhận không khớp.");
        }

        User_24133023 existing = userDao.findByEmail(cleanEmail);
        if (existing != null) {
            if (existing.isVerified()) {
                throw new AuthException_24133023("Email này đã được đăng ký trên hệ thống. Vui lòng đăng nhập.");
            } else {
                // If existing account was never verified, initiate OTP verification for it
                return initiateOtp(existing.getId(), existing.getEmail(), existing.getFullname());
            }
        }

        String salt = PasswordUtil_24133023.generateSalt();
        String hashedPassword = PasswordUtil_24133023.hashPassword(password, salt);

        User_24133023 newUser = new User_24133023();
        newUser.setEmail(cleanEmail);
        newUser.setFullname(cleanFullname);
        newUser.setPhone(phone);
        newUser.setPasswd(hashedPassword);
        newUser.setPasswordSalt(salt);
        newUser.setAdmin(false);
        newUser.setVerified(false);

        int userId = userDao.insert(newUser);

        return initiateOtp(userId, cleanEmail, cleanFullname);
    }

    public OtpSessionData_24133023 initiateOtp(int userId, String email, String fullname)
            throws AuthException_24133023 {
        String otpCode = PasswordUtil_24133023.generateOtp6();
        String hashedOtp = PasswordUtil_24133023.hashOtp(otpCode);
        long now = System.currentTimeMillis();

        try {
            mailService.sendOtpEmail(email, fullname, otpCode);
        } catch (Exception e) {
            throw new AuthException_24133023(
                "Không thể gửi email OTP: " + e.getMessage() + ". Vui lòng kiểm tra lại cấu hình SMTP hoặc liên hệ quản trị viên.",
                e
            );
        }

        return new OtpSessionData_24133023(
            userId,
            email,
            fullname,
            hashedOtp,
            now + OTP_EXPIRE_SECONDS * 1000L,
            MAX_OTP_ATTEMPTS,
            now
        );
    }

    public OtpSessionData_24133023 resendOtp(OtpSessionData_24133023 currentData)
            throws AuthException_24133023 {
        if (currentData == null) {
            throw new AuthException_24133023("Phiên xác thực đã hết hạn. Vui lòng đăng ký lại.");
        }
        long now = System.currentTimeMillis();
        long elapsedSeconds = (now - currentData.getLastSentAt()) / 1000;
        if (elapsedSeconds < OTP_RESEND_COOLDOWN_SECONDS) {
            long remaining = OTP_RESEND_COOLDOWN_SECONDS - elapsedSeconds;
            throw new AuthException_24133023("Vui lòng đợi " + remaining + " giây trước khi yêu cầu gửi lại mã OTP.");
        }

        String otpCode = PasswordUtil_24133023.generateOtp6();
        String hashedOtp = PasswordUtil_24133023.hashOtp(otpCode);

        try {
            mailService.sendOtpEmail(currentData.getEmail(), currentData.getFullname(), otpCode);
        } catch (Exception e) {
            throw new AuthException_24133023(
                "Không thể gửi lại email OTP: " + e.getMessage() + ". Vui lòng thử lại sau.",
                e
            );
        }

        currentData.setHashedOtp(hashedOtp);
        currentData.setExpireAt(now + OTP_EXPIRE_SECONDS * 1000L);
        currentData.setAttempts(MAX_OTP_ATTEMPTS);
        currentData.setLastSentAt(now);

        return currentData;
    }

    public void verifyOtp(OtpSessionData_24133023 sessionData, String inputOtp)
            throws AuthException_24133023, SQLException {
        if (sessionData == null) {
            throw new AuthException_24133023("Phiên xác thực đã hết hạn. Vui lòng đăng ký lại hoặc yêu cầu gửi mã mới.");
        }

        long now = System.currentTimeMillis();
        if (now > sessionData.getExpireAt()) {
            throw new AuthException_24133023("Mã OTP đã hết hiệu lực. Vui lòng bấm 'Gửi lại mã OTP'.");
        }

        if (sessionData.getAttempts() <= 0) {
            throw new AuthException_24133023("Bạn đã thử sai quá số lần cho phép (5 lần). Vui lòng yêu cầu gửi lại mã mới.");
        }

        if (inputOtp == null || inputOtp.isBlank()) {
            throw new AuthException_24133023("Vui lòng nhập mã OTP gồm 6 chữ số.");
        }

        String cleanOtp = inputOtp.trim();
        boolean valid = PasswordUtil_24133023.verifyOtp(cleanOtp, sessionData.getHashedOtp());
        if (!valid) {
            int remaining = sessionData.getAttempts() - 1;
            sessionData.setAttempts(remaining);
            if (remaining > 0) {
                throw new AuthException_24133023("Mã OTP không chính xác. Bạn còn " + remaining + " lần thử.");
            } else {
                throw new AuthException_24133023("Bạn đã nhập sai mã 5 lần. Vui lòng nhấn 'Gửi lại mã OTP'.");
            }
        }

        // OTP is correct! Update user verification status in database
        userDao.markVerified(sessionData.getUserId());
    }

    public User_24133023 login(String email, String password)
            throws AuthException_24133023, SQLException {
        if (email == null || email.isBlank() || password == null || password.isBlank()) {
            throw new AuthException_24133023("Vui lòng điền đầy đủ email và mật khẩu.");
        }

        User_24133023 user = userDao.findByEmail(email.trim());
        if (user == null) {
            throw new AuthException_24133023("Email hoặc mật khẩu không chính xác.");
        }

        boolean passwordMatches = PasswordUtil_24133023.verifyPassword(password, user.getPasswordSalt(), user.getPasswd());
        if (!passwordMatches) {
            throw new AuthException_24133023("Email hoặc mật khẩu không chính xác.");
        }

        if (!user.isVerified()) {
            throw new AuthException_24133023(
                "Tài khoản của bạn chưa được kích hoạt qua email OTP. Vui lòng hoàn tất xác thực OTP."
            );
        }

        userDao.updateLastLogin(user.getId());
        return user;
    }

    public OtpSessionData_24133023 prepareOtpForUnverifiedEmail(String email)
            throws AuthException_24133023, SQLException {
        if (email == null || email.isBlank()) {
            throw new AuthException_24133023("Email không hợp lệ.");
        }
        User_24133023 user = userDao.findByEmail(email.trim());
        if (user == null) {
            throw new AuthException_24133023("Không tìm thấy tài khoản tương ứng.");
        }
        if (user.isVerified()) {
            throw new AuthException_24133023("Tài khoản này đã được kích hoạt. Bạn có thể đăng nhập ngay.");
        }
        return initiateOtp(user.getId(), user.getEmail(), user.getFullname());
    }
}

