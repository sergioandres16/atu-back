package com.example.firmadorservice.Model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Response {
    private String nombreArchivo;
    private byte[] contenido;
    private boolean estado;
    private String message;

    public Response(String nombre, byte[] contenido, boolean estado, String message) {
        this.nombreArchivo = nombre;
        this.contenido = contenido;
        this.estado = estado;
        this.message = message;
    }

    public Response() {
    }
}
