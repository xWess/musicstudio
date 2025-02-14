package asm.org.MusicStudio.entity;

import java.time.LocalDateTime;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import lombok.Data;

@Data
public abstract class User {
    protected User() {
        this.idProperty = new SimpleIntegerProperty();
        this.nameProperty = new SimpleStringProperty();
        this.emailProperty = new SimpleStringProperty();
        this.roleProperty = new SimpleObjectProperty<>();
        this.passwordProperty = new SimpleStringProperty();
        this.saltProperty = new SimpleStringProperty();
        this.activeProperty = new SimpleBooleanProperty();
        this.lastLoginProperty = new SimpleObjectProperty<>();
        this.resetToken = new SimpleStringProperty();
        this.resetTokenExpiry = new SimpleObjectProperty<>();
    }

    private final IntegerProperty idProperty;
    private final StringProperty nameProperty;
    private final StringProperty emailProperty;
    private final ObjectProperty<Role> roleProperty;
    private final StringProperty passwordProperty;
    private final StringProperty saltProperty;
    private final BooleanProperty activeProperty;
    private final ObjectProperty<LocalDateTime> lastLoginProperty;
    private final StringProperty resetToken;
    private final ObjectProperty<LocalDateTime> resetTokenExpiry;

    // Property getters
    public IntegerProperty idProperty() { return idProperty; }
    public StringProperty nameProperty() { return nameProperty; }
    public StringProperty emailProperty() { return emailProperty; }
    public ObjectProperty<Role> roleProperty() { return roleProperty; }
    public StringProperty passwordProperty() { return passwordProperty; }
    public StringProperty saltProperty() { return saltProperty; }
    public BooleanProperty activeProperty() { return activeProperty; }
    public ObjectProperty<LocalDateTime> lastLoginProperty() { return lastLoginProperty; }
    public StringProperty resetTokenProperty() { return resetToken; }
    public ObjectProperty<LocalDateTime> resetTokenExpiryProperty() { return resetTokenExpiry; }

    // Regular getters and setters
    public int getId() { return idProperty.get(); }
    public void setId(int id) { this.idProperty.set(id); }
    
    public String getName() { return nameProperty.get(); }
    public void setName(String name) { this.nameProperty.set(name); }
    
    public String getEmail() { return emailProperty.get(); }
    public void setEmail(String email) { this.emailProperty.set(email); }

    public void setRole(Role role) {
        if (role == null) {
            throw new IllegalArgumentException("Role cannot be null");
        }
        this.roleProperty.set(role);
    }

    public void setRole(String roleStr) {
        if (roleStr == null || roleStr.trim().isEmpty()) {
            throw new IllegalArgumentException("Role string cannot be null or empty");
        }
        setRole(Role.fromString(roleStr));
    }

    public Role getRole() {
        Role role = roleProperty.get();
        System.out.println("Getting role: " + role); // Debug log
        return role;
    }

    public String getPassword() { return passwordProperty.get(); }
    public void setPassword(String password) { this.passwordProperty.set(password); }
    
    public String getSalt() { return saltProperty.get(); }
    public void setSalt(String salt) { this.saltProperty.set(salt); }
    
    public boolean isActive() { return activeProperty.get(); }
    public void setActive(boolean active) { this.activeProperty.set(active); }
    
    public LocalDateTime getLastLogin() { return lastLoginProperty.get(); }
    public void setLastLogin(LocalDateTime lastLogin) { 
        this.lastLoginProperty.set(lastLogin); 
    }
    
    public String getResetToken() { return resetToken.get(); }
    public void setResetToken(String token) { this.resetToken.set(token); }
    
    public LocalDateTime getResetTokenExpiry() { return resetTokenExpiry.get(); }
    public void setResetTokenExpiry(LocalDateTime expiry) { 
        this.resetTokenExpiry.set(expiry); 
    }

    // Factory method for basic user creation
    protected static <T extends User> T createBasicUser(int id, String name, String email, Class<T> userClass) {
        try {
            T user = userClass.getDeclaredConstructor().newInstance();
            user.setId(id);
            user.setName(name);
            user.setEmail(email);
            return user;
        } catch (Exception e) {
            throw new RuntimeException("Failed to create user", e);
        }
    }
} 