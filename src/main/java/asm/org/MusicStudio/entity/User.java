package asm.org.MusicStudio.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public abstract class User {
    private Integer id;
    private String name;
    private String email;
    private Role role;

    public void setRole(String roleStr) {
        try {
            this.role = Role.valueOf(roleStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Role must be either 'student', 'teacher', or 'artist'");
        }
    }
} 