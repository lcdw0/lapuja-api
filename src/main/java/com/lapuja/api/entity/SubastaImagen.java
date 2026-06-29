package com.lapuja.api.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "subasta_imagenes")
public class SubastaImagen {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long subastaId;

    @Column(length = 1000, nullable = false)
    private String url;

    private Integer orden;

    private Boolean principal;

    public SubastaImagen() {
    }

    public Long getId() {
        return id;
    }

    public Long getSubastaId() {
        return subastaId;
    }

    public void setSubastaId(Long subastaId) {
        this.subastaId = subastaId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Integer getOrden() {
        return orden;
    }

    public void setOrden(Integer orden) {
        this.orden = orden;
    }

    public Boolean getPrincipal() {
        return principal;
    }

    public void setPrincipal(Boolean principal) {
        this.principal = principal;
    }
}