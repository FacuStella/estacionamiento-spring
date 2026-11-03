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
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "zonas")
public class Zona {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false, unique = true, length = 10)
    protected String letra;

    @NotBlank
    @Column(nullable = false)
    protected String tipoVehiculos;

    @Min(1)
    @Column(nullable = false)
    protected int profundidadGarage;

    @Min(1)
    @Column(nullable = false)
    protected int anchoGarage;

    /** Planta donde esta la zona: 0 = planta baja, 1 = piso 1, -1 = subsuelo 1. */
    @Column(nullable = false)
    protected int planta;

    /** Precio por hora (o fraccion) para los vehiculos que estacionan en esta zona. */
    @Positive
    @Column(nullable = false)
    protected double tarifaHora;

    @JsonIgnore
    @ManyToMany
    @JoinTable(name = "zona_empleado",
            joinColumns = @JoinColumn(name = "zona_id"),
            inverseJoinColumns = @JoinColumn(name = "empleado_id"))
    protected Set<Empleado> empleados = new HashSet<>();

    public Zona() {
    }

    public Zona(String letra, String tipoVehiculos, int profundidadGarage, int anchoGarage, int planta, double tarifaHora) {
        this.letra = letra;
        this.tipoVehiculos = tipoVehiculos;
        this.profundidadGarage = profundidadGarage;
        this.anchoGarage = anchoGarage;
        this.planta = planta;
        this.tarifaHora = tarifaHora;
    }

    public double getTarifaHora() {
        return tarifaHora;
    }

    public void setTarifaHora(double tarifaHora) {
        this.tarifaHora = tarifaHora;
    }

    public void asignarEmpleado(Empleado empleado) {
        empleados.add(empleado);
    }

    public void quitarEmpleado(Empleado empleado) {
        empleados.remove(empleado);
    }

    public Long getId() {
        return id;
    }

    public String getLetra() {
        return letra;
    }

    public void setLetra(String letra) {
        this.letra = letra;
    }

    public String getTipoVehiculos() {
        return tipoVehiculos;
    }

    public void setTipoVehiculos(String tipoVehiculos) {
        this.tipoVehiculos = tipoVehiculos;
    }

    public int getProfundidadGarage() {
        return profundidadGarage;
    }

    public void setProfundidadGarage(int profundidadGarage) {
        this.profundidadGarage = profundidadGarage;
    }

    public int getAnchoGarage() {
        return anchoGarage;
    }

    public void setAnchoGarage(int anchoGarage) {
        this.anchoGarage = anchoGarage;
    }

    public int getPlanta() {
        return planta;
    }

    public void setPlanta(int planta) {
        this.planta = planta;
    }

    public Set<Empleado> getEmpleados() {
        return empleados;
    }

    public void setEmpleados(Set<Empleado> empleados) {
        this.empleados = empleados;
    }
    
    
}
