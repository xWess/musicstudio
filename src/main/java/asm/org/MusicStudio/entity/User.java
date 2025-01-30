package asm.org.MusicStudio.entity;

import javafx.beans.property.*;

public abstract class User {
    private final IntegerProperty id = new SimpleIntegerProperty();
    private final StringProperty name = new SimpleStringProperty();
    private final StringProperty email = new SimpleStringProperty();
    private final ObjectProperty<Role> role = new SimpleObjectProperty<>();

    // Property getters
    public IntegerProperty idProperty() { return id; }
    public StringProperty nameProperty() { return name; }
    public StringProperty emailProperty() { return email; }
    public ObjectProperty<Role> roleProperty() { return role; }

    // Regular getters and setters
    public int getId() { return id.get(); }
    public void setId(int id) { this.id.set(id); }
    
    public String getName() { return name.get(); }
    public void setName(String name) { this.name.set(name); }
    
    public String getEmail() { return email.get(); }
    public void setEmail(String email) { this.email.set(email); }

    public void setRole(String roleStr) {
        try {
            this.role.set(Role.valueOf(roleStr.toUpperCase()));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Role must be either 'student', 'teacher', or 'artist'");
        }
    }

    public User() {
        // Default constructor
    }

    public User(Integer id, String name, String email, Role role) {
        setId(id);
        setName(name);
        setEmail(email);
        this.role.set(role);
    }

    public Role getRole() { return role.get(); }
    public void setRole(Role role) { this.role.set(role); }
} 