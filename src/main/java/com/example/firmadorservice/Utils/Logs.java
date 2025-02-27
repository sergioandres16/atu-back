package com.example.firmadorservice.Utils;

import com.example.firmadorservice.Model.SSignerProperty;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Logs {
    private static final String rutaLogs = "/usr/local/tomcat/webapps/SSFD/ATU/ATULogs.txt";

    /**
     * Escribe un mensaje en ATULogs.txt (log general).
     * Lo llamas si quieres llevar un registro genérico de ciertos eventos.
     */
    public static void writeLogsFile(String message) throws IOException {
        File carpeta = new File("/logs");

        if (!carpeta.exists()) {
            if (carpeta.mkdir()) {
                System.out.println("Carpeta '/logs' creada exitosamente.");
            } else {
                System.out.println("No se pudo crear la carpeta '/logs'.");
            }
        } else {
            System.out.println("La carpeta '/logs' ya existe.");
        }
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        String fullMessage = timestamp.toString() + " " + message + "\n";

        FileWriter writer = new FileWriter(rutaLogs, true);
        BufferedWriter bufferedWriter = new BufferedWriter(writer);
        bufferedWriter.append(fullMessage);
        bufferedWriter.close();
    }

    /**
     * Escribe un log diario ATU_dd-MM-yyyy.txt con TODOS los parámetros de la petición
     * siempre que devuelvas estado=false.
     */
    public static void writeErrorParams(String mensajeError, SSignerProperty sSignerProperty) {
        try {
            String fechaHoy = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
            String horaAhora = new SimpleDateFormat("HH:mm:ss").format(new Date());
            String rutaLog = "/usr/local/tomcat/webapps/SSFD/ATU/logs-error/ATU_" + fechaHoy + ".txt";

            StringBuilder sb = new StringBuilder();
            sb.append("[").append(horaAhora).append("] ERROR: ").append(mensajeError).append("\n");
            sb.append("Parámetros recibidos:\n");
            sb.append("  - dni: ").append(sSignerProperty.getDni()).append("\n");
            sb.append("  - tipoFirma: ").append(sSignerProperty.getTipoFirma()).append("\n");
            sb.append("  - correlativo: ").append(sSignerProperty.getFirmaCorrelativo()).append("\n");
            sb.append("  - avisoVal: ").append(sSignerProperty.getFirmaAvisoVal()).append("\n");
            sb.append("  - numExpediente: ").append(sSignerProperty.getFirmaNumExpediente()).append("\n");
            sb.append("  - coordenadas: ").append(sSignerProperty.getCoordenadas()).append("\n");
            sb.append("  - motivo: ").append(sSignerProperty.getFirmaMotivo()).append("\n");
            sb.append("  - nomUsuario: ").append(sSignerProperty.getNomUsuario()).append("\n");
            sb.append("  - nroVistos: ").append(sSignerProperty.getNroVistos()).append("\n");
            sb.append("  - tamanoLetra: ").append(sSignerProperty.getTamanoLetra()).append("\n");
            sb.append("  - negritaLetra: ").append(sSignerProperty.getNegritaLetra()).append("\n");
            sb.append("  - tamanoImagen: ").append(sSignerProperty.getTamanoImagen()).append("\n");
            sb.append("------------------------------------\n");

            // Agrega la información al log diario
            try (FileWriter fw = new FileWriter(rutaLog, true);
                 BufferedWriter bw = new BufferedWriter(fw)) {
                bw.write(sb.toString());
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}