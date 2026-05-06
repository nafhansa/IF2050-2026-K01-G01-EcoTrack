package com.ecotrack.entity;

public class User {
    private String idUser;
    private String nama;
    private String role;

    public User() {
    }

    public User(String idUser, String nama, String role) {
        this.idUser = idUser;
        this.nama = nama;
        this.role = role;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void pilihMenu(String menuTujuan) {
        // Navigasi ke halaman tujuan
    }
}
