package com.example.firmadorservice.Entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Getter
@Setter
@Table(name = "certificados")
public class Certificado {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "cert_name")
    private String nombreCertificado;

    @Column(name = "cert_password")
    private String passCertificado;

    @Column(name = "id_firmante")
    private String idFirmante;

    @Column(name = "fecha_valido_desde")
    private Timestamp fechaValidoDesde;

    @Column(name = "fecha_valido_hasta")
    private Timestamp fechaValidoHasta;

    @Column(name = "url_sello_tiempo")
    private String urlSelloTiempo;

    @Column(name = "user_sello_tiempo")
    private String userSelloTiempo;

    @Column(name = "pass_sello_tiempo")
    private String passSelloTiempo;

    @Column(name = "estado_sello_tiempo")
    private Integer selloTiempo;

    @Column(name = "ltv")
    private Integer ltv;

    @Column(name = "cert_location")
    private String location;
}
