package com.estacionamiento.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

@Entity
@Table(name = "garages")
public class Garage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(nullable = false, unique = true)
    private Integer numero;

    @Column(nullable = false)
    private double lecturaContadorLuz;

    @Column(nullable = false)
    private boolean mantenimientoContratado;

    @ManyToOne(optional = false)
    @JoinColumn(name = "zona_id", nullable = false)
    private Zona zona;

    /** Propietario del garage (puede no tener). */
    @ManyToOne
    @JoinColumn(name = "socio_id")
    private Socio socio;

    private LocalDate fechaCompra;

    public Garage() {
    }

    public Garage(Integer numero, Zona zona) {
        this.numero = numero;
        this.zona = zona;
        this.lecturaContadorLuz = 0;
        this.mantenimientoContratado = false;
    }

    public boolean tienePropietario() {
        return socio != null;
    }

    public Integer getNumero() {
        return numero;
    }
    
    public void setNumero(Integer numero) {
        this.numero = numero;
    }

    public double getLecturaContadorLuz() {
        return lecturaContadorLuz;
    }

    public void setLecturaContadorLuz(double lecturaContadorLuz) {
        this.lecturaContadorLuz = lecturaContadorLuz;
    }

    public boolean isMantenimientoContratado() {
        return mantenimientoContratado;
    }

    public void setMantenimientoContratado(boolean mantenimientoContratado) {
        this.mantenimientoContratado = mantenimientoContratado;
    }

    public LocalDate getFechaCompra() {
        return fechaCompra;
    }

    public void setFechaCompra(LocalDate fechaCompra) {
        this.fechaCompra = fechaCompra;
    }

    public Long getId() {
        return id;
    }

    public Zona getZona() {
        return zona;
    }

    public void setZona(Zona zona) {
        this.zona = zona;
    }

    public Socio getSocio() {
        return socio;
    }  
    
    public void setSocio(Socio socio) {
        this.socio = socio;
    }  
}
