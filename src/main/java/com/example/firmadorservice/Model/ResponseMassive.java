package com.example.firmadorservice.Model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponseMassive {
    private String nomArchivo;
    private boolean estado;
    private String message;

    public ResponseMassive(String nomArchivo, boolean estado, String message) {
        this.nomArchivo = nomArchivo;
        this.estado = estado;
        this.message = message;
    }

    public ResponseMassive() {
    }
}
