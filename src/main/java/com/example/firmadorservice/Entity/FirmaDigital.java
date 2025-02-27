package com.example.firmadorservice.Entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.Instant;

@Entity
@Getter
@Setter
@Table(name = "firmadigital")
public class FirmaDigital {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "dni", nullable = false, length = 8)
    private String dni;

    @Column(name = "rutafir", nullable = false)
    private String ruta;

    @Column(name = "nombre", nullable = false)
    private String nombre;

    @Column(name = "correlativo")
    private String correlativo;

    @Column(name = "avisoval")
    private String avisoval;

    @Column(name = "numexpediente")
    private String numexpediente;

    @Column(name = "fechahora")
    private Instant fechahora;

    public FirmaDigital(String dni, String ruta, String nombre, String correlativo, String avisoval, String numexpediente, Instant fechahora) {
        this.dni = dni;
        this.ruta = ruta;
        this.nombre = nombre;
        this.correlativo = correlativo;
        this.avisoval = avisoval;
        this.numexpediente = numexpediente;
        this.fechahora = fechahora;
    }

    public FirmaDigital() {

    }
}
