package com.example.firmadorservice.Model;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.Objects;

@Getter
@Setter
public class SSignerProperty {
    public String filesToSign = "";
    public String fileSigned = "";
    public String dni;
    private String rawCoordenadas;

    public String rootPath = "/usr/local/tomcat/webapps/SSFD/ATU"; //    /usr/local/tomcat/webapps/SSFD/ATU   -   C:/SSFDAlonso/ATU

    public String originFolderPath = rootPath+"/ATU_XXXXXXXX/porfirmar";
    public String tempFolderPath = rootPath+"/ATU_XXXXXXXX/firmados_ltv";
    public String destinationFolderPath = rootPath+"/ATU_XXXXXXXX/firmados";
    public String modificadoGlosaFolderPath = rootPath+"/ATU_XXXXXXXX/modificados_glosa";
    public String modificadoTituloFolderPath = rootPath+"/ATU_XXXXXXXX/modificados_titulo";
    public String certPath = rootPath+"/certificates";

    public String imageSignPath = rootPath+"/sign_images";
    public Float imageFirmaWidth = 50.0f;
    public Float imageFirmaHeight = 49.0f;
    public Float imageVisadoWidth = 51.0f;
    public Float imageVisadoHeight = 51.0f;

    public Integer tipoFirma = 0;
    public Integer posicionTitulo = 2;
    public Integer posicionSuperior = 0;
    public Integer nroVistos = 0;
    public String nomUsuario = "";


    public Integer tamanoLetra = 0;
    public Integer negritaLetra = 0;
    public Integer tamanoImagen = 0;

    public String firmaCorrelativo = "";
    public String firmaAvisoVal = "";
    public String firmaNumExpediente = "";
    public String firmaMotivo = "Soy el autor del documento";
    public Float  firmaAlto = 62f;
    public Float  firmaAncho = 112f;
    public String firmaImageName = "Firma.png";
    public String firmaConTituloImageName = "FirmaConTitulo.png";

    public String visadoAvisoVal = "";
    public Integer visadoAlto = 70;
    public Integer visadoAncho = 200;
    public String visadoImageName = "Visado.png";

    public float posX = 350;
    public float posY = 573;
    public double factorConversion = 0.35277777778;
    public SSignerProperty(String dniCliente,String tipoFirma, String correlativo, String avisoVal, String numExpediente,
                           String coordenadas,String nombreArchivo, String nombreUsuario, String numeroVistos, String posicionTitulo, String posicionSuperior, String motivo,
                           String tamanoLetra, String negrita, String tamanoImagen) {
        this.originFolderPath = this.originFolderPath.replace("XXXXXXXX",dniCliente);
        this.tempFolderPath = this.tempFolderPath.replace("XXXXXXXX",dniCliente);
        this.destinationFolderPath = this.destinationFolderPath.replace("XXXXXXXX",dniCliente);
        this.modificadoGlosaFolderPath = this.modificadoGlosaFolderPath.replace("XXXXXXXX",dniCliente);
        this.modificadoTituloFolderPath = this.modificadoTituloFolderPath.replace("XXXXXXXX",dniCliente);

        this.dni = dniCliente;
        this.filesToSign = nombreArchivo;
        this.fileSigned = nombreArchivo.split("\\.")[0]+"_signed.pdf";
        this.rawCoordenadas = coordenadas;
        if (tipoFirma.equals("1") || tipoFirma.equals("2")) {
            this.tipoFirma = Integer.parseInt(tipoFirma);

            this.firmaCorrelativo = Objects.requireNonNullElse(correlativo, "");
            this.firmaAvisoVal = Objects.requireNonNullElse(avisoVal, "");
            this.firmaNumExpediente = Objects.requireNonNullElse(numExpediente, "");

            try{
                this.nroVistos = Integer.parseInt(numeroVistos);
                this.posicionTitulo =  posicionTitulo != null ? Integer.parseInt(posicionTitulo) : 2;
                this.posicionSuperior = posicionSuperior != null ? Integer.parseInt(posicionSuperior) : 0;

                String[] ubicaciones = coordenadas.split("-");
                this.posX = (float) (Float.parseFloat(ubicaciones[0]) / factorConversion);
                this.posY = (float) (Float.parseFloat(ubicaciones[1]) / factorConversion);
            }catch (Exception e){
                throw new RuntimeException("[*] "+ Instant.now()+": Se produjo un error : "+ e.getMessage());
            }
        } else if(tipoFirma.equals("3") || tipoFirma.equals("4")){
            this.tipoFirma = Integer.parseInt(tipoFirma);

            this.visadoAvisoVal = Objects.requireNonNullElse(avisoVal, "");
            this.nomUsuario = Objects.requireNonNullElse(nombreUsuario, "");

            try{
                String[] ubicaciones = coordenadas.split("-");
                this.posX = (float) (Float.parseFloat(ubicaciones[0]) / factorConversion);
                this.posY = (float) (Float.parseFloat(ubicaciones[1]) / factorConversion);
            }catch (Exception e){
                throw new RuntimeException("[*] "+ Instant.now()+": Se produjo un error al parsear las coordenadas: "+ e.getMessage());
            }
        } else if(tipoFirma.equals("5")){
            this.tipoFirma = Integer.parseInt(tipoFirma);

            try{
                String[] ubicaciones = coordenadas.split("-");
                this.posX = (float) (Float.parseFloat(ubicaciones[0]) / factorConversion);
                this.posY = (float) (Float.parseFloat(ubicaciones[1]) / factorConversion);

                this.firmaMotivo = motivo == null ? "" : motivo;

                this.tamanoLetra = Integer.parseInt(tamanoLetra);
                this.negritaLetra = Integer.parseInt(negrita);
                this.tamanoImagen = Integer.parseInt(tamanoImagen);

            }catch (Exception e){
                throw new RuntimeException("[*] "+ Instant.now()+": Se produjo un error : "+ e.getMessage());
            }
        } else{
            throw new RuntimeException("[*] "+ Instant.now()+": Se produjo un error debido a que no se ingreso un tipoFirma valido");
        }
    }

    public SSignerProperty() {
    }
    public String getCoordenadas() {
        return rawCoordenadas;
    }
}
