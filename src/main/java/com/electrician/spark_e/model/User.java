package com.electrician.spark_e.model;

import jakarta.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    private String role; // e.g., "ROLE_ELECTRICIAN", "ROLE_ADMIN"

    @OneToOne
    @JoinColumn(name = "electrician_id")
    private Electrician electrician;

    // No-args constructor (required by JPA)
    public User() {}

    // All-args constructor (optional, useful for testing)
    public User(String username, String password, String role, Electrician electrician) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.electrician = electrician;
    }

    // Getters and setters for all fields
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public Electrician getElectrician() { return electrician; }
    public void setElectrician(Electrician electrician) { this.electrician = electrician; }

    // Optional: override equals, hashCode, toString
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", role='" + role + '\'' +
                '}';
    }
}