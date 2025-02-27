package com.example.firmadorservice.Entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Getter
@Setter
@Table(name = "registrofirma")
public class RegistroFirma {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;


    @Column(name = "firmadigital_id", nullable = false)
    private Integer firmadigitalId;

    @Column(name = "dni")
    private String dni;

    @Column(name = "tipo_firma")
    private String tipoFirma;

    @Column(name = "posicion_titulo")
    private String posicionTitulo;

    @Column(name = "posicion_superior")
    private String posicionSuperior;

    @Column(name = "coordenadas")
    private String coordenadas;

    @Column(name = "aviso_val")
    private String avisoVal;

    @Column(name = "nom_usuario")
    private String nomUsuario;

    @Column(name = "correlativo")
    private String correlativo;

    @Column(name = "num_expediente")
    private String numExpediente;

    @Column(name = "motivo")
    private String motivo;

    @Column(name = "nro_vistos")
    private String nroVistos;

    @Column(name = "tamano_letra")
    private String tamanoLetra;

    @Column(name = "negrita_letra")
    private String negritaLetra;

    @Column(name = "tamano_imagen")
    private String tamanoImagen;


    @Column(name = "fecha_hora")
    private Instant fechaHora;

    public RegistroFirma() {
    }
}