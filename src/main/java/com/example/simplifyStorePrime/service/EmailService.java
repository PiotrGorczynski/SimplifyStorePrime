package com.example.simplifyStorePrime.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.frontend-url}")
    private String frontendUrl;

    public void sendPasswordResetEmail(String toEmail, String token) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom("noreply@simplifystore.com");
            helper.setTo(toEmail);
            helper.setSubject("Simplify Store Prime - Password Reset");
            helper.setText(buildResetEmailHtml(token), true);

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send email: " + e.getMessage());
        }
    }

    private String buildResetEmailHtml(String token) {
        String resetLink = frontendUrl + "/reset-password?token=" + token;

        return """
            <div style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px;">
                <div style="text-align: center; margin-bottom: 30px;">
                    <h1 style="color: #1a1a2e; margin: 0;">SIMPLIFY</h1>
                    <p style="color: #e74c3c; font-weight: bold; letter-spacing: 2px; margin: 5px 0;">STORE PRIME</p>
                </div>
            
                <h2 style="color: #333;">Password Reset Request</h2>
            
                <p style="color: #666; line-height: 1.6;">
                    You requested a password reset for your Simplify Store Prime account.
                    Click the button below to set a new password:
                </p>
                
                <div style="text-align: center; margin: 30px 0;">
                    <a href="%s" 
                       style="background-color: #3B82F6; color: white; padding: 14px 30px; 
                              text-decoration: none; border-radius: 8px; font-weight: bold;
                              display: inline-block;">
                        Reset Password
                    </a>
                </div>
                
                <p style="color: #999; font-size: 13px;">
                    This link will expire in <strong>30 minutes</strong>.<br>
                    If you didn't request this, you can safely ignore this email.
                </p>
                
                <hr style="border: none; border-top: 1px solid #eee; margin: 30px 0;">
                <p style="color: #bbb; font-size: 11px; text-align: center;">
                    Simplify Store Prime © 2026
                </p>
            </div>
            """.formatted(resetLink);
    }
}