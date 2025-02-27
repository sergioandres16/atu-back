package com.example.firmadorservice.Model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "documento")
public class SSignerMasive {
    private String nomArchivo;
    private Integer tipoFirma;
    private String correlativo;
    private String avisoVal;
    private String numExpediente;
    private String coordenadas;
    private String nomUsuario;
    private Integer nroVistos;
    private String posicionTitulo;
    private String motivo;
    private String posicionSuperior;
    private String tamanoLetra;
    private String negrita;
    private String tamanoImagen;

    @XmlElement
    public String getnomArchivo() {
        return nomArchivo;
    }

    public void setnomArchivo(String nomArchivo) {
        this.nomArchivo = nomArchivo;
    }

    @XmlElement
    public Integer gettipoFirma() {
        return tipoFirma;
    }

    public void settipoFirma(Integer tipoFirma) {
        this.tipoFirma = tipoFirma;
    }

    @XmlElement
    public String getcorrelativo() {
        return correlativo;
    }

    public void setcorrelativo(String correlativo) {
        this.correlativo = correlativo;
    }

    @XmlElement
    public String getavisoVal() {
        return avisoVal;
    }

    public void setavisoVal(String avisoVal) {
        this.avisoVal = avisoVal;
    }
    @XmlElement
    public String getnumExpediente() {
        return numExpediente;
    }

    public void setnumExpediente(String numExpediente) {
        this.numExpediente = numExpediente;
    }
    @XmlElement
    public String getcoordenadas() {
        return coordenadas;
    }

    public void setcoordenadas(String coordenadas) {
        this.coordenadas = coordenadas;
    }

    @XmlElement
    public Integer getnroVistos() {
        return nroVistos;
    }

    public void setnroVistos(Integer nroVistos) {
        this.nroVistos = nroVistos;
    }

    @XmlElement
    public String getnomUsuario() {
        return nomUsuario;
    }

    public void setnomUsuario(String nomUsuario) {
        this.nomUsuario = nomUsuario;
    }

    @XmlElement
    public String getposicionTitulo() {
        return posicionTitulo;
    }

    public void setposicionTitulo(String posicionTitulo) {
        this.posicionTitulo = posicionTitulo;
    }

    @XmlElement
    public String getposicionSuperior() {
        return posicionSuperior;
    }

    public void setposicionSuperior(String posicionSuperior) {
        this.posicionSuperior = posicionSuperior;
    }

    @XmlElement
    public String getmotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    @XmlElement
    public String getTamanoLetra() {
        return tamanoLetra;
    }

    public void setTamanoLetra(String tamanoLetra) {
        this.tamanoLetra = tamanoLetra;
    }
    @XmlElement
    public String getNegrita() {
        return negrita;
    }

    public void setNegrita(String negrita) {
        this.negrita = negrita;
    }

    @XmlElement
    public String getTamanoImagen() {
        return tamanoImagen;
    }

    public void setTamanoImagen(String tamanoImagen) {
        this.tamanoImagen = tamanoImagen;
    }
}
