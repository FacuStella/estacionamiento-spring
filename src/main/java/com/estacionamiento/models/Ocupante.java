package com.estacionamiento.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

/**
 * Una estadia: un vehiculo ocupando un garage. Se crea cuando el vehiculo entra y se cierra al salir,
 * cuando se calcula lo que paga. Mientras "salida" sea null, el vehiculo sigue adentro.
 */
@Entity
@Table(name = "ocupantes")
public class Ocupante {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "vehiculo_id", nullable = false)
    private Vehiculo vehiculo;

    @ManyToOne(optional = false)
    @JoinColumn(name = "garage_id", nullable = false)
    private Garage garage;

    @Column(nullable = false)
    private LocalDateTime entrada;

    private LocalDateTime salida;

    /** Horas cobradas (hora o fraccion). */
    private Integer horas;

    /** Porcentaje de descuento aplicado (por ser socio, o 100 si uso el garage de su abono). */
    private Double descuento;

    /** Lo que se cobro al salir. */
    private Double importe;

    public Ocupante() {
    }

    public Ocupante(Vehiculo vehiculo, Garage garage, LocalDateTime entrada) {
        this.vehiculo = vehiculo;
        this.garage = garage;
        this.entrada = entrada;
    }

    public boolean estaAdentro() {
        return salida == null;
    }

    /** Registra la salida y lo cobrado. */
    public void cerrar(LocalDateTime salida, int horas, double descuento, double importe) {
        this.salida = salida;
        this.horas = horas;
        this.descuento = descuento;
        this.importe = importe;
    }

    public Long getId() {
        return id;
    }

    public Vehiculo getVehiculo() {
        return vehiculo;
    }

    public Garage getGarage() {
        return garage;
    }

    public LocalDateTime getEntrada() {
        return entrada;
    }

    public LocalDateTime getSalida() {
        return salida;
    }

    public Integer getHoras() {
        return horas;
    }

    public Double getDescuento() {
        return descuento;
    }

    public Double getImporte() {
        return importe;
    }
}
