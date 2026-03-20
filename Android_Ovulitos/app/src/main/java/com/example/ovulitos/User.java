package com.example.ovulitos;

public class User {
    private String uid;
    private String email;
    private String nombre;
    private String avatar;

    public User() {
        // Constructor vacío necesario para Firebase
    }

    public User(String uid, String email, String nombre, String avatar) {
        this.uid = uid;
        this.email = email;
        this.nombre = nombre;
        this.avatar = avatar;
    }

    public String getUid() { return uid; }
    public void setUid(String uid) { this.uid = uid; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getAvatar() { return avatar; }
    public void setAvatar(String avatar) { this.avatar = avatar; }
}
