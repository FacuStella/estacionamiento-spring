package com.estacionamiento.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "vehiculos")
public class Vehiculo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false, unique = true)
    protected String patente;

    /** Marca, elegida de la tabla marcas (debe fabricar vehiculos del tipo elegido). */
    @ManyToOne(optional = false)
    @JoinColumn(name = "id_marca", nullable = false)
    protected Marca marca;

    /** Tipo de vehiculo, elegido de la tabla tipos_vehiculo. */
    @ManyToOne(optional = false)
    @JoinColumn(name = "id_tipo", nullable = false)
    protected TipoVehiculo tipo;

    @NotBlank
    @Column(nullable = false)
    protected String dimensiones;

    // Las relaciones se guardan en tablas intermedias (vehiculo_socio, vehiculo_empleado),
    // no como columnas de la tabla vehiculos. Cada vehiculo aparece a lo sumo una vez en cada tabla.
    // El garage que ocupa no esta aca: cada entrada y salida es un Ocupante.

    /** Socio dueño del vehiculo. Puede no tener: los vehiculos de paso no registran al conductor. */
    @ManyToOne
    @JoinTable(name = "vehiculo_socio",
            joinColumns = @JoinColumn(name = "vehiculo_id", unique = true),
            inverseJoinColumns = @JoinColumn(name = "socio_id", nullable = false))
    protected Socio propietario;

    @ManyToOne
    @JoinTable(name = "vehiculo_empleado",
            joinColumns = @JoinColumn(name = "vehiculo_id", unique = true),
            inverseJoinColumns = @JoinColumn(name = "empleado_id", nullable = false))
    protected Empleado empleadoAsignado;

    public Vehiculo() {
    }

    public Vehiculo(String patente, Marca marca, TipoVehiculo tipo, String dimensiones, Socio propietario) {
        this.patente = patente;
        this.marca = marca;
        this.tipo = tipo;
        this.dimensiones = dimensiones;
        this.propietario = propietario;
    }

    public boolean tieneEmpleadoAsignado() {
        return empleadoAsignado != null;
    }

    public Long getId() {
        return id;
    }

    public String getPatente() {
        return patente;
    }

    public void setPatente(String patente) {
        this.patente = patente;
    }

    public Marca getMarca() {
        return marca;
    }

    public void setMarca(Marca marca) {
        this.marca = marca;
    }

    public TipoVehiculo getTipo() {
        return tipo;
    }

    public void setTipo(TipoVehiculo tipo) {
        this.tipo = tipo;
    }

    public String getDimensiones() {
        return dimensiones;
    }

    public void setDimensiones(String dimensiones) {
        this.dimensiones = dimensiones;
    }

    public Socio getPropietario() {
        return propietario;
    }

    public void setPropietario(Socio propietario) {
        this.propietario = propietario;
    }

    public Empleado getEmpleadoAsignado() {
        return empleadoAsignado;
    }

    public void setEmpleadoAsignado(Empleado empleadoAsignado) {
        this.empleadoAsignado = empleadoAsignado;
    }
    
    
}
