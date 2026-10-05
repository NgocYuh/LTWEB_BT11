package vn.hcmute.hnhbookstore.business;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

public final class SmtpMailService_24133023 implements MailService_24133023 {

    private static final class SmtpAuthenticator_24133023 extends Authenticator {
        private final String user;
        private final String pass;

        SmtpAuthenticator_24133023(String user, String pass) {
            this.user = user;
            this.pass = pass;
        }

        @Override
        protected PasswordAuthentication getPasswordAuthentication() {
            return new PasswordAuthentication(user, pass);
        }
    }

    private final String host;
    private final int port;
    private final String username;
    private final String password;
    private final String from;

    public SmtpMailService_24133023() {
        this(
            getEnvOrDefault("SMTP_HOST", "smtp.gmail.com"),
            Integer.parseInt(getEnvOrDefault("SMTP_PORT", "587")),
            System.getenv("SMTP_USERNAME"),
            System.getenv("SMTP_PASSWORD"),
            getEnvOrDefault("SMTP_FROM", System.getenv("SMTP_USERNAME"))
        );
    }

    public SmtpMailService_24133023(String host, int port, String username, String password, String from) {
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
        this.from = from;
    }

    @Override
    public void sendOtpEmail(String recipientEmail, String recipientName, String otpCode) throws Exception {
        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            throw new IllegalStateException(
                "Dịch vụ gửi email chưa được cấu hình. Vui lòng cấu hình biến môi trường SMTP_USERNAME và SMTP_PASSWORD."
            );
        }

        Properties props = new Properties();
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", String.valueOf(port));
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.connectiontimeout", "15000");
        props.put("mail.smtp.timeout", "15000");

        Session session = Session.getInstance(props, new SmtpAuthenticator_24133023(username, password));

        MimeMessage message = new MimeMessage(session);
        message.setFrom(new InternetAddress(from, "HNHBOOKSTORE", StandardCharsets.UTF_8.name()));
        message.setRecipient(Message.RecipientType.TO, new InternetAddress(recipientEmail, recipientName, StandardCharsets.UTF_8.name()));
        message.setSubject("Mã xác thực OTP đăng ký tài khoản — HNHBOOKSTORE", StandardCharsets.UTF_8.name());

        String htmlContent = """
            <!DOCTYPE html>
            <html>
            <head><meta charset="UTF-8"></head>
            <body style="font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif; background-color: #F8F9FA; margin: 0; padding: 24px; color: #111827;">
                <div style="max-width: 520px; margin: 0 auto; background: #FFFFFF; border-radius: 8px; border: 1px solid #E5E7EB; padding: 32px; box-shadow: 0 2px 4px rgba(0,0,0,0.05);">
                    <div style="border-bottom: 2px solid #2563EB; padding-bottom: 12px; margin-bottom: 20px;">
                        <span style="font-size: 20px; font-weight: bold; color: #2563EB;">HNH<span style="color: #111827;">BOOKSTORE</span></span>
                    </div>
                    <h2 style="font-size: 18px; margin-top: 0; color: #111827;">Mã xác thực tài khoản</h2>
                    <p style="font-size: 14px; line-height: 1.6; color: #374151;">Xin chào <strong>%s</strong>,</p>
                    <p style="font-size: 14px; line-height: 1.6; color: #374151;">Cảm ơn bạn đã đăng ký tài khoản tại <strong>HNHBOOKSTORE</strong>. Dưới đây là mã xác thực OTP của bạn:</p>
                    <div style="text-align: center; margin: 28px 0;">
                        <span style="display: inline-block; font-size: 32px; font-weight: bold; letter-spacing: 6px; padding: 12px 24px; background: #F1F5F9; color: #2563EB; border-radius: 8px; border: 1px dashed #2563EB;">
                            %s
                        </span>
                    </div>
                    <p style="font-size: 13px; color: #6B7280; line-height: 1.5;">Mã này có hiệu lực trong vòng <strong>5 phút</strong> và tối đa <strong>5 lần thử</strong>. Tuyệt đối không chia sẻ mã này cho bất kỳ ai.</p>
                    <div style="margin-top: 32px; border-top: 1px solid #E5E7EB; padding-top: 16px; font-size: 12px; color: #9CA3AF; text-align: center;">
                        Hoàng Ngọc Huy — 24133023 — Đề 02 — HNHBOOKSTORE
                    </div>
                </div>
            </body>
            </html>
            """.formatted(recipientName, otpCode);

        message.setContent(htmlContent, "text/html; charset=UTF-8");
        Transport.send(message);
    }

    private static String getEnvOrDefault(String name, String defaultValue) {
        String val = System.getenv(name);
        return (val != null && !val.isBlank()) ? val : defaultValue;
    }
}

