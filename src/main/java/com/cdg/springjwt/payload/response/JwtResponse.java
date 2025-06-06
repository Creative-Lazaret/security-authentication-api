package com.cdg.springjwt.payload.response;

import lombok.ToString;

import java.util.List;

@ToString
public class JwtResponse {
    private String token;
    private String type = "Bearer";
    private Long id;
    private String username;
    private String nom;
    private String prenom;
    private String email;
    private List<String> filiales;
    private List<String> roles;

    public JwtResponse(String accessToken, Long id, String username, String email, List<String> roles) {
        this.token = accessToken;
        this.id = id;
        this.username = username;
        this.email = email;
        this.roles = roles;
    }

    public JwtResponse(Long id, String username, String email, String nom, String prenom, List<String> roles, List<String> filiales) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.filiales = filiales;
        this.roles = roles;
        this.nom = nom;
        this.prenom = prenom;
    }

    public String getAccessToken() {
        return token;
    }

    public void setAccessToken(String accessToken) {
        this.token = accessToken;
    }

    public String getTokenType() {
        return type;
    }

    public void setTokenType(String tokenType) {
        this.type = tokenType;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<String> getRoles() {
        return roles;
    }

    public List<String> getFiliales() {
        return filiales;
    }

    public void setFiliales(List<String> filiales) {
        this.filiales = filiales;
    }
}
