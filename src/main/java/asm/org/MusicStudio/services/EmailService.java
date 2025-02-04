package asm.org.MusicStudio.services;

import asm.org.MusicStudio.entity.User;
import jakarta.mail.*;
import jakarta.mail.internet.*;
import java.math.BigDecimal;
import java.util.Properties;

public class EmailService {
    private final String username;
    private final String password;
    private final Session session;

    public EmailService(String username, String password) {
        this.username = username;
        this.password = password;
        
        Properties prop = new Properties();
        prop.put("mail.smtp.host", "smtp.gmail.com");
        prop.put("mail.smtp.port", "587");
        prop.put("mail.smtp.auth", "true");
        prop.put("mail.smtp.starttls.enable", "true");
        
        this.session = Session.getInstance(prop, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });
    }

    public void sendWelcomeEmail(User user) {
        String subject = "Welcome to Music Studio!";
        String content = String.format("""
            Dear %s,
            
            Welcome to Music Studio! We're excited to have you join us.
            
            Best regards,
            Music Studio Team
            """, user.getName());
            
        sendEmail(user.getEmail(), subject, content);
    }

    public void sendPasswordResetEmail(User user, String token) {
        String subject = "Password Reset Request";
        String content = String.format("""
            Dear %s,
            
            Click the link below to reset your password:
            http://localhost:8080/reset-password?token=%s
            
            This link will expire in 24 hours.
            
            Best regards,
            Music Studio Team
            """, user.getName(), token);
            
        sendEmail(user.getEmail(), subject, content);
    }

    public void sendPaymentConfirmation(String email, BigDecimal amount, String description) {
        String subject = "Payment Confirmation - Music Studio";
        String content = String.format("""
            Thank you for your payment!
            
            Amount: $%.2f
            Description: %s
            
            Best regards,
            Music Studio Team
            """, amount, description);
            
        sendEmail(email, subject, content);
    }

    private void sendEmail(String to, String subject, String content) {
        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(Message.RecipientType.TO, 
                InternetAddress.parse(to));
            message.setSubject(subject);
            message.setText(content);

            Transport.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send email", e);
        }
    }
} 