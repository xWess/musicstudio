package asm.org.MusicStudio.entity;

public class Admin extends User {
    public Admin() {
        super();
        setRole(Role.ADMIN);
    }

    public Admin(Integer id, String name, String email) {
        super();
        setId(id);
        setName(name);
        setEmail(email);
        setRole(Role.ADMIN);
    }
} 