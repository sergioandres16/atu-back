package com.example.firmadorservice.Entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Timestamp;

@Entity
@Getter
@Setter
@Table(name = "firmantes")
public class Firmante {

    @Id
    @Column(name = "dni", nullable = false, length = 8)
    private String dni;

    @Column(name = "nombres")
    private String nombres;

    @Column(name = "estado")
    private Integer estado;

    @Column(name = "fecha_registro")
    private Timestamp fechaRegistro;
}
