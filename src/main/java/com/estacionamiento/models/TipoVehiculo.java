package com.estacionamiento.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/** Tipo de vehiculo (Auto, Moto, Camioneta). Se carga en DataInitializer y se elige de un desplegable. */
@Entity
@Table(name = "tipos_vehiculo")
public class TipoVehiculo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tipo")
    private Long idTipo;

    @Column(nullable = false, unique = true, length = 30)
    private String tipo;

    public TipoVehiculo() {
    }

    public TipoVehiculo(String tipo) {
        this.tipo = tipo;
    }

    public Long getIdTipo() {
        return idTipo;
    }

    public String getTipo() {
        return tipo;
    }
}
