package com.example.firmadorservice.Entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.Instant;

@Entity
@Getter
@Setter
@Table(name = "usuario")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "us_id", nullable = false)
    private Integer idUsuario;

    @Column(name = "us_firmante", nullable = false,length = 8)
    private String dniFirmante;

    @Column(name = "us_pin")
    private String pinFirmante;

    @Column(name = "us_estado", nullable = false)
    private Integer estado;

    @Column(name = "us_fecha_registro", nullable = false)
    private Instant fechaRegistro;

    @Column(name = "us_uuid")
    private String uuid;

    @Column(name = "us_fecha_uuid")
    private Instant fechaGeneracionUuid;

    @Column(name = "us_correo")
    private String correo;

    public Usuario(String dniFirmante, String pinFirmante, Integer estado, Instant fechaRegistro) {
        this.dniFirmante = dniFirmante;
        this.pinFirmante = pinFirmante;
        this.estado = estado;
        this.fechaRegistro = fechaRegistro;
    }

    public Usuario() {
    }
}
