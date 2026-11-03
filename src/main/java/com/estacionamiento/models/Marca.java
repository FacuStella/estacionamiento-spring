package com.estacionamiento.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import java.util.HashSet;
import java.util.Set;

/**
 * Marca de vehiculo. Una marca puede fabricar varios tipos (Honda hace autos, camionetas y motos),
 * por eso la relacion con los tipos va en la tabla intermedia marca_tipo_vehiculo.
 */
@Entity
@Table(name = "marcas")
public class Marca {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_marca")
    private Long idMarca;

    @Column(nullable = false, unique = true, length = 40)
    private String marca;

    @JsonIgnore
    @ManyToMany
    @JoinTable(name = "marca_tipo_vehiculo",
            joinColumns = @JoinColumn(name = "id_marca"),
            inverseJoinColumns = @JoinColumn(name = "id_tipo"))
    private Set<TipoVehiculo> tipos = new HashSet<>();

    public Marca() {
    }

    public Marca(String marca, Set<TipoVehiculo> tipos) {
        this.marca = marca;
        this.tipos = tipos;
    }

    /** true si la marca fabrica vehiculos de ese tipo. */
    public boolean fabrica(TipoVehiculo tipo) {
        return tipos.stream().anyMatch(t -> t.getIdTipo().equals(tipo.getIdTipo()));
    }

    public Long getIdMarca() {
        return idMarca;
    }

    public String getMarca() {
        return marca;
    }

    public Set<TipoVehiculo> getTipos() {
        return tipos;
    }
}
