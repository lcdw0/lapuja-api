package com.lapuja.api.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable =false, unique = true)
    private String correo;

    @Column(nullable = false)
    private String password;

    @Column(length = 1000)
    private String fotoPerfil;

    private String telefono;

    private String ciudad;

    @Column(length = 500)
    private String biografia;

    private Double saldo;

    private LocalDateTime fechaRegistro;

    @Column(nullable = false)
    private Boolean correoVerificado = false;

    private LocalDateTime fechaVerificacionCorreo;

    public Usuario() {
    }

    public Usuario(String nombre, String correo, String password) {
        this.nombre = nombre;
        this.correo = correo;
        this.password = password;
    }

    @PrePersist
    public void prePersist() {

        this.fechaRegistro = LocalDateTime.now();

        if (this.saldo == null) {
            this.saldo = 0.0;
        }

        if (this.correoVerificado == null) {
            this.correoVerificado = false;
        }
    }

    public Long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFotoPerfil() {
        return fotoPerfil;
    }

    public void setFotoPerfil(String fotoPerfil) {
        this.fotoPerfil = fotoPerfil;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    public String getBiografia() {
        return biografia;
    }

    public void setBiografia(String biografia) {
        this.biografia = biografia;
    }

    public Double getSaldo() {
        return saldo;
    }

    public void setSaldo(Double saldo) {
        this.saldo = saldo;
    }

    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDateTime fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public Boolean getCorreoVerificado() {
        return correoVerificado;
    }

    public void setCorreoVerificado(Boolean correoVerificado) {
        this.correoVerificado = correoVerificado;
    }

    public LocalDateTime getFechaVerificacionCorreo() {
        return fechaVerificacionCorreo;
    }

    public void setFechaVerificacionCorreo(LocalDateTime fechaVerificacionCorreo) {
        this.fechaVerificacionCorreo = fechaVerificacionCorreo;
    }
}