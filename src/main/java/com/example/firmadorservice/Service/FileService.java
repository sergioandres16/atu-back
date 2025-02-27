package com.example.firmadorservice.Service;

import com.example.firmadorservice.Model.*;
import com.itextpdf.io.font.FontConstants;
import com.itextpdf.text.*;
import com.itextpdf.text.Font;
import com.itextpdf.text.pdf.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

@Service
public class FileService {
    private final String rootArchivos = "/usr/local/tomcat/webapps/SSFD/ATU/"; // /usr/local/tomcat/webapps/SSFD/ATU/ - C:/SSFDAlonso/ATU/
    private final String rutaArchivosDescomprimidos = rootArchivos+"masive_docs/";

    public void createFirmaConTituloImageFile(SSignerProperty sSignerProperty, String nombres, String motivo, String fecha) throws  IOException{
        /*BufferedImage image = ImageIO.read(new File(sSignerProperty.getImageSignPath() + "/"+sSignerProperty.getFirmaConTituloImageName()));


        BufferedImage nuevaImagen = new BufferedImage(image.getWidth(), image.getHeight()-200+sSignerProperty.getPosicionSuperior(),BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = nuevaImagen.createGraphics();
        g2d.drawImage(image, 115,7, image.getWidth()-200, image.getHeight(), null);

        g2d.setFont(new java.awt.Font(FontConstants.HELVETICA_BOLD,  java.awt.Font.PLAIN, 26));
        g2d.setColor(Color.BLACK);

        String[] datosNombre = nombres.split(" ");
        String textoNombres = "Firmado digitalmente por:\n";
        for(int i=0; i< datosNombre.length; i++){
            textoNombres += datosNombre[i];
            if ((i + 1) % 2 == 0 && (i + 1) < datosNombre.length) {
                textoNombres += "\n";
            } else if ((i + 1) < datosNombre.length) {
                textoNombres += " ";
            }
        }
        textoNombres+= "\n";

        String[] datosMotivo = motivo.split(" ");
        String textoFirma = textoNombres +
                "Motivo:"+datosMotivo[0]+" "+datosMotivo[1]+" "+datosMotivo[2]+" "+datosMotivo[3]+"\n"+datosMotivo[4]+"\n" +"Fecha:"+fecha;
        int xTexto = 1635;
        int yTexto = datosNombre.length <4 ? 55 : 40;
        String[] lineas = textoFirma.split("\n");
        for(String linea: lineas){
            g2d.drawString(linea, xTexto, yTexto);
            yTexto += 28;
        }

        String titulo = sSignerProperty.getFirmaCorrelativo();
        java.awt.Font font = new java.awt.Font(FontConstants.HELVETICA_BOLD,java.awt.Font.CENTER_BASELINE, 38);
        g2d.setFont(font);
        int anchoImagen = image.getWidth();
        int altoImagen = image.getHeight();
        //int yTextoTitulo = altoImagen - 220 - sSignerProperty.getPosicionSuperior();
        int yTextoTitulo = altoImagen - 220 + sSignerProperty.getPosicionSuperior();

        int xTextoTitulo;
        if(sSignerProperty.getPosicionTitulo() == 2){
            xTextoTitulo = anchoImagen / 2 - (g2d.getFontMetrics(font).stringWidth(titulo)) / 2;
            xTextoTitulo = xTextoTitulo - 30;
        }else{
            xTextoTitulo = 100;
        }

        g2d.drawString(titulo, xTextoTitulo, yTextoTitulo);

        int xFinalTitulo = xTextoTitulo + g2d.getFontMetrics(font).stringWidth(titulo);
        int yLinea = yTextoTitulo + 5;
        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(2));
        g2d.drawLine(xTextoTitulo, yLinea, xFinalTitulo, yLinea);

        ImageIO.write(nuevaImagen, "png", new File(sSignerProperty.getImageSignPath() + "/" +
                sSignerProperty.getFilesToSign()));
        sSignerProperty.setFirmaImageName( sSignerProperty.getFilesToSign());

        g2d.dispose();*/
        BufferedImage image = ImageIO.read(new File(sSignerProperty.getImageSignPath() + "/"+sSignerProperty.getFirmaConTituloImageName()));


        BufferedImage nuevaImagen = new BufferedImage(image.getWidth(), image.getHeight()-200+sSignerProperty.getPosicionSuperior(),BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = nuevaImagen.createGraphics();
        g2d.drawImage(image, 115,7, image.getWidth()-200, image.getHeight(), null);

        g2d.setFont(new java.awt.Font(FontConstants.HELVETICA_BOLD,  java.awt.Font.PLAIN, 26));
        g2d.setColor(Color.BLACK);

        String[] datosNombre = nombres.split(" ");
        String textoNombres = "Firmado digitalmente por:\n";
        for(int i=0; i< datosNombre.length; i++){
            textoNombres += datosNombre[i];
            if ((i + 1) % 2 == 0 && (i + 1) < datosNombre.length) {
                textoNombres += "\n";
            } else if ((i + 1) < datosNombre.length) {
                textoNombres += " ";
            }
        }
        textoNombres+= "\n";

        String[] datosMotivo = motivo.split(" ");
        String textoFirma = textoNombres +
                "Motivo:"+datosMotivo[0]+" "+datosMotivo[1]+" "+datosMotivo[2]+" "+datosMotivo[3]+"\n"+datosMotivo[4]+"\n" +"Fecha:"+fecha;
        int xTexto = 1635;
        int yTexto = datosNombre.length <4 ? 55 : (datosNombre.length == 4 ? 50 : 35); //20
        String[] lineas = textoFirma.split("\n");
        for(String linea: lineas){
            g2d.drawString(linea, xTexto, yTexto);
            yTexto += 28;
        }

        String titulo = sSignerProperty.getFirmaCorrelativo();
        java.awt.Font font = new java.awt.Font(FontConstants.HELVETICA_BOLD,java.awt.Font.CENTER_BASELINE, 38);
        g2d.setFont(font);
        int anchoImagen = image.getWidth();
        int altoImagen = image.getHeight();
        //int yTextoTitulo = altoImagen - 220 - sSignerProperty.getPosicionSuperior();
        int yTextoTitulo = altoImagen - 220 + sSignerProperty.getPosicionSuperior();

        int xTextoTitulo;
        if(sSignerProperty.getPosicionTitulo() == 2){
            xTextoTitulo = anchoImagen / 2 - (g2d.getFontMetrics(font).stringWidth(titulo)) / 2;
            xTextoTitulo = xTextoTitulo - 30;
        }else{
            xTextoTitulo = 100;
        }

        g2d.drawString(titulo, xTextoTitulo, yTextoTitulo);

        int xFinalTitulo = xTextoTitulo + g2d.getFontMetrics(font).stringWidth(titulo);
        int yLinea = yTextoTitulo + 5;
        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(2));
        g2d.drawLine(xTextoTitulo, yLinea, xFinalTitulo, yLinea);

        ImageIO.write(nuevaImagen, "png", new File(sSignerProperty.getImageSignPath() + "/" +
                sSignerProperty.getFilesToSign()));
        sSignerProperty.setFirmaImageName( sSignerProperty.getFilesToSign());

        g2d.dispose();
    }

    public void createVisadoImageFile(SSignerProperty sSignerProperty) throws IOException {
        BufferedImage image = ImageIO.read(new File(sSignerProperty.getImageSignPath() + "/"+sSignerProperty.getVisadoImageName()));

        Graphics2D g2d = image.createGraphics();
        g2d.setFont(new java.awt.Font(FontConstants.HELVETICA,  java.awt.Font.PLAIN, 26));
        g2d.setColor(Color.BLACK);
        if(sSignerProperty.getNomUsuario().length() > 11){
            String primero = sSignerProperty.getNomUsuario().substring(0,10);
            int anchoTexto = g2d.getFontMetrics().stringWidth(primero);
            int x = (image.getWidth() - anchoTexto) / 2;
            g2d.drawString(primero, x, 165);

            String segundo = sSignerProperty.getNomUsuario().substring(10);
            int anchoTexto2 = g2d.getFontMetrics().stringWidth(segundo);
            int x2 = (image.getWidth() - anchoTexto2) / 2;
            g2d.drawString(segundo, x2, 190);
        }else{
            int anchoTexto = g2d.getFontMetrics().stringWidth(sSignerProperty.getNomUsuario());
            int x = (image.getWidth() - anchoTexto) / 2;
            g2d.drawString(sSignerProperty.getNomUsuario(), x, 175);
        }

        ImageIO.write(image, "png", new File(sSignerProperty.getImageSignPath() + "/" + sSignerProperty.getNomUsuario()+".png"));
        g2d.dispose();
        sSignerProperty.setVisadoImageName(sSignerProperty.getNomUsuario()+".png");
    }

    /*public void createFirmaImageFile(SSignerProperty sSignerProperty, String nombres, String motivo, String fecha) throws IOException {
        int ancho = 315;
        int alto = 80;
        BufferedImage image = new BufferedImage(ancho, alto, BufferedImage.TYPE_INT_ARGB);
        BufferedImage imageImagen = ImageIO.read(new File(sSignerProperty.getImageSignPath() + "/" + sSignerProperty.getFirmaImageName()));

        int x = 1;
        int y = 2;
        Graphics2D g2d = image.createGraphics();
        g2d.drawImage(imageImagen, x,y, null);
        g2d.setColor(Color.BLACK);
        int rectanguloX = imageImagen.getWidth() + 2;

        java.awt.Font font = new java.awt.Font("Dialog", sSignerProperty.getNegritaLetra() == 1 ? Font.BOLD : Font.NORMAL,
                sSignerProperty.getTamanoLetra() != 0 ? sSignerProperty.getTamanoLetra() : 12);
        g2d.setFont(font);

        String[] datosNombre = nombres.split(" ");
        String textoNombres = "Firmado digitalmente por:\n";
        for(int i=0; i< datosNombre.length; i++){
            textoNombres += datosNombre[i];
            if ((i + 1) % 2 == 0 && (i + 1) < datosNombre.length) {
                textoNombres += "\n";
            } else if ((i + 1) < datosNombre.length) {
                textoNombres += " ";
            }
        }
        textoNombres+= "\n";

        String textoFirma = textoNombres + "Motivo:"+motivo+"\n"+"Fecha:"+fecha;

        int xTexto = rectanguloX + 3;
        int yTexto = datosNombre.length <4 ? 22 : 15; // 16 - 9

        String[] lineas = textoFirma.split("\n");
        for(String linea: lineas){
            g2d.drawString(linea, xTexto, yTexto);
            yTexto += sSignerProperty.getTamanoLetra() != 0 ? (sSignerProperty.getTamanoLetra() + 3) : 15;
        }
        if(sSignerProperty.getTipoFirma() == 5){
            ImageIO.write(image, "png", new File(sSignerProperty.getImageSignPath() + "/" +
                    sSignerProperty.getFilesToSign()+".png"));
            sSignerProperty.setFirmaImageName(sSignerProperty.getFilesToSign()+".png");
        }else{
            ImageIO.write(image, "png", new File(sSignerProperty.getImageSignPath() + "/" +
                    sSignerProperty.getFilesToSign()));
            sSignerProperty.setFirmaImageName(sSignerProperty.getFilesToSign());
        }

        g2d.dispose();
    }*/

    public void createFirmaImageFile(SSignerProperty sSignerProperty, String nombres, String motivo, String fecha) throws IOException {
        if(sSignerProperty.getTipoFirma() == 5){
            String[] datosNombre = nombres.split(" ");
            String textoNombres = "Firmado digitalmente por:\n";
            for (int i = 0; i < datosNombre.length; i++) {
                textoNombres += datosNombre[i];
                if ((i + 1) % 2 == 0 && (i + 1) < datosNombre.length) {
                    textoNombres += "\n";
                } else if ((i + 1) < datosNombre.length) {
                    textoNombres += " ";
                }
            }
            textoNombres += "\n";
            String textoFirma = textoNombres + "Motivo:" + motivo + "\n" + "Fecha:" + fecha;

            // Configuración de fuente
            int tamanoLetra = sSignerProperty.getTamanoLetra() != 0 ? sSignerProperty.getTamanoLetra() : 20;
            java.awt.Font font = new java.awt.Font("Dialog", sSignerProperty.getNegritaLetra() == 1 ? Font.BOLD : Font.NORMAL, tamanoLetra);

            BufferedImage tempImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
            Graphics2D tempGraphics = tempImage.createGraphics();
            tempGraphics.setFont(font);
            FontMetrics metrics = tempGraphics.getFontMetrics();

            int lineHeight = metrics.getHeight();
            String[] lineas = textoFirma.split("\n");
            int maxLineWidth = 0;
            for (String linea : lineas) {
                int lineWidth = metrics.stringWidth(linea);
                if (lineWidth > maxLineWidth) {
                    maxLineWidth = lineWidth;
                }
            }
            tempGraphics.dispose();

            BufferedImage imageImagen = ImageIO.read(new File(sSignerProperty.getImageSignPath() + "/" + sSignerProperty.getFirmaImageName()));

            int tamanoImagen = sSignerProperty.getTamanoImagen();
            int anchoImagen = imageImagen.getWidth() + tamanoImagen;
            int altoImagen = imageImagen.getHeight() + tamanoImagen;

            BufferedImage imagenAjustada = new BufferedImage(anchoImagen, altoImagen, BufferedImage.TYPE_INT_ARGB);
            Graphics2D gAjustada = imagenAjustada.createGraphics();
            gAjustada.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            gAjustada.drawImage(imageImagen, 0, 0, anchoImagen, altoImagen, null);
            gAjustada.dispose();

            int margenHorizontalTexto = 15;
            int margenVertical = 10;
            int anchoTotal = anchoImagen + maxLineWidth + margenHorizontalTexto;
            int altoTotal = Math.max(altoImagen, (lineHeight * lineas.length)) + margenVertical * 2;

            sSignerProperty.setFirmaAlto((float) altoTotal / (float)(1.5));
            sSignerProperty.setFirmaAncho((float) anchoTotal / (float)(1.5));

            BufferedImage imagenFinal = new BufferedImage(anchoTotal, altoTotal, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = imagenFinal.createGraphics();
            g2d.setFont(font);
            g2d.setColor(Color.BLACK);

            int xImagen = 10;
            int yImagen = (altoTotal - altoImagen) / 2;
            g2d.drawImage(imagenAjustada, xImagen, yImagen, null);

            int xTexto = anchoImagen + margenHorizontalTexto;
            int yTexto = (altoTotal - (lineHeight * lineas.length)) / 2 + lineHeight;
            for (String linea : lineas) {
                g2d.drawString(linea, xTexto, yTexto);
                yTexto += lineHeight;
            }
            String outputFileName = sSignerProperty.getTipoFirma() == 5
                    ? sSignerProperty.getFilesToSign() + ".png"
                    : sSignerProperty.getFilesToSign();
            ImageIO.write(imagenFinal, "png", new File(sSignerProperty.getImageSignPath() + "/" + outputFileName));
            sSignerProperty.setFirmaImageName(outputFileName);

            g2d.dispose();
        }else{
            /*int ancho = 315;
            int alto = 80;
            BufferedImage image = new BufferedImage(ancho, alto, BufferedImage.TYPE_INT_ARGB);
            BufferedImage imageImagen = ImageIO.read(new File(sSignerProperty.getImageSignPath() + "/" + sSignerProperty.getFirmaImageName()));

            int x = 1;
            int y = 2;
            Graphics2D g2d = image.createGraphics();
            g2d.drawImage(imageImagen, x,y, null);
            g2d.setColor(Color.BLACK);
            int rectanguloX = imageImagen.getWidth() + 2;

            java.awt.Font font = new java.awt.Font("Dialog", sSignerProperty.getNegritaLetra() == 1 ? Font.BOLD : Font.NORMAL,
                    sSignerProperty.getTamanoLetra() != 0 ? sSignerProperty.getTamanoLetra() : 12);
            g2d.setFont(font);

            String[] datosNombre = nombres.split(" ");
            String textoNombres = "Firmado digitalmente por:\n";
            for(int i=0; i< datosNombre.length; i++){
                textoNombres += datosNombre[i];
                if ((i + 1) % 2 == 0 && (i + 1) < datosNombre.length) {
                    textoNombres += "\n";
                } else if ((i + 1) < datosNombre.length) {
                    textoNombres += " ";
                }
            }
            textoNombres+= "\n";

            String textoFirma = textoNombres + "Motivo:"+motivo+"\n"+"Fecha:"+fecha;

            int xTexto = rectanguloX + 3;
            int yTexto = datosNombre.length <4 ? 22 : 15; // 16 - 9

            String[] lineas = textoFirma.split("\n");
            for(String linea: lineas){
                g2d.drawString(linea, xTexto, yTexto);
                yTexto += sSignerProperty.getTamanoLetra() != 0 ? (sSignerProperty.getTamanoLetra() + 3) : 15;
            }
            if(sSignerProperty.getTipoFirma() == 5){
                ImageIO.write(image, "png", new File(sSignerProperty.getImageSignPath() + "/" +
                        sSignerProperty.getFilesToSign()+".png"));
                sSignerProperty.setFirmaImageName(sSignerProperty.getFilesToSign()+".png");
            }else{
                ImageIO.write(image, "png", new File(sSignerProperty.getImageSignPath() + "/" +
                        sSignerProperty.getFilesToSign()));
                sSignerProperty.setFirmaImageName(sSignerProperty.getFilesToSign());
            }

            g2d.dispose();* */
            System.out.println(sSignerProperty.getImageSignPath() + "/" + sSignerProperty.getFirmaImageName());
            BufferedImage imageImagen = ImageIO.read(new File(sSignerProperty.getImageSignPath() + "/" + sSignerProperty.getFirmaImageName()));

            java.awt.Font font = new java.awt.Font("Dialog", sSignerProperty.getNegritaLetra() == 1 ? Font.BOLD : Font.NORMAL,
                    sSignerProperty.getTamanoLetra() != 0 ? sSignerProperty.getTamanoLetra() : 12);

            BufferedImage tempImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
            Graphics2D tempGraphics = tempImage.createGraphics();
            tempGraphics.setFont(font);
            FontMetrics metrics = tempGraphics.getFontMetrics();

            String[] datosNombre = nombres.split(" ");
            String textoNombres = "Firmado digitalmente por:\n";
            for (int i = 0; i < datosNombre.length; i++) {
                textoNombres += datosNombre[i];
                if ((i + 1) % 2 == 0 && (i + 1) < datosNombre.length) {
                    textoNombres += "\n";
                } else if ((i + 1) < datosNombre.length) {
                    textoNombres += " ";
                }
            }
            textoNombres += "\n";
            String textoFirma = textoNombres + "Motivo:" + motivo + "\n" + "Fecha:" + fecha;

            String[] lineas = textoFirma.split("\n");
            int lineHeight = metrics.getHeight();
            int maxLineWidth = 0;
            for (String linea : lineas) {
                int lineWidth = metrics.stringWidth(linea);
                if (lineWidth > maxLineWidth) {
                    maxLineWidth = lineWidth;
                }
            }
            tempGraphics.dispose();

            int margenVertical = 10;
            int altoTexto = lineHeight * lineas.length + margenVertical * 2;
            int altoImagen = Math.max(imageImagen.getHeight(), altoTexto);
            int anchoImagen = imageImagen.getWidth();

            int anchoTotal = anchoImagen + maxLineWidth + 15;
            int altoTotal = altoImagen;

            BufferedImage image = new BufferedImage(anchoTotal, altoTotal, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = image.createGraphics();
            g2d.drawImage(imageImagen, 1, (altoTotal - imageImagen.getHeight()) / 2, null);
            g2d.setColor(Color.BLACK);
            g2d.setFont(font);

            int xTexto = anchoImagen + 5;
            int yTexto = (altoTotal - altoTexto) / 2 + lineHeight;
            for (String linea : lineas) {
                g2d.drawString(linea, xTexto, yTexto);
                yTexto += lineHeight;
            }

            String outputFileName = sSignerProperty.getTipoFirma() == 5
                    ? sSignerProperty.getFilesToSign() + ".png"
                    : sSignerProperty.getFilesToSign();
            ImageIO.write(image, "png", new File(sSignerProperty.getImageSignPath() + "/" + outputFileName));
            sSignerProperty.setFirmaImageName(outputFileName);
            g2d.dispose();
        }
    }


    public void saveFile(byte[] bytesFile, SSignerProperty sSignerProperty,int folder) throws IOException {
        String path = "";
        String nombreArchivo = "";
        switch (folder){
            case 1:
                path = String.valueOf(sSignerProperty.getOriginFolderPath());
                nombreArchivo = sSignerProperty.getFilesToSign();
                break;
            case 2:
                path = String.valueOf(sSignerProperty.getDestinationFolderPath());
                nombreArchivo = sSignerProperty.getFileSigned();
                break;
            case 3:
                path = String.valueOf(sSignerProperty.getModificadoGlosaFolderPath());
                nombreArchivo = sSignerProperty.getFilesToSign();
                break;
            case 4:
                path = String.valueOf(sSignerProperty.getModificadoTituloFolderPath());
                nombreArchivo = sSignerProperty.getFilesToSign();
                break;
        }
        path = path + '/' + nombreArchivo;

        FileOutputStream fos = new FileOutputStream(path);
        fos.write(bytesFile);
        fos.close();
    }

    public void deleteFile(SSignerProperty sSignerProperty,Integer tipo){
        File file = null;
        if(tipo == 1 || tipo == 2 || tipo == 5){
            file = new File(sSignerProperty.getImageSignPath() + "/" + sSignerProperty.getFirmaImageName());
        }else if(tipo == 3 || tipo == 4){
            file = new File(sSignerProperty.getImageSignPath() + "/" + sSignerProperty.getVisadoImageName());
        }
        if(file.exists()){
            file.delete();
        }
    }

    public void insertCorrelativo(SSignerProperty sSignerProperty) throws IOException, DocumentException {
        PdfReader lector =new PdfReader(sSignerProperty.getModificadoGlosaFolderPath()+"/"+sSignerProperty.getFilesToSign());
        ByteArrayOutputStream outputStream =new ByteArrayOutputStream();
        PdfStamper escritor =new PdfStamper(lector, outputStream);

        Font fuente = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, Font.UNDERLINE);
        Phrase frase;
        Integer tipo = sSignerProperty.getTipoFirma();
        if(tipo == 1 || tipo == 2){
            frase = new Phrase(sSignerProperty.getFirmaCorrelativo(), fuente);
        }else{
            frase = null;
        }

        PdfContentByte contenido = escritor.getOverContent(1);
        frase.setLeading(14);

        int x,y;
        if(sSignerProperty.getPosicionTitulo() == 2){
            x = 195;
            y = 700;
        }else{
            x = 85;
            y = 700;
        }

        ColumnText.showTextAligned(contenido, Element.ALIGN_LEFT, frase, x, y, 0);
        escritor.close();
        lector.close();

        String pathOut = sSignerProperty.getModificadoTituloFolderPath()+ "/" + sSignerProperty.getFilesToSign();
        FileOutputStream outputStr = new FileOutputStream(pathOut);
        outputStr.write(outputStream.toByteArray());
        outputStr.close();
    }
    public void insertGlosa(SSignerProperty sSignerProperty) throws IOException, DocumentException {
        PdfReader lector =new PdfReader(sSignerProperty.getOriginFolderPath()+"/"+sSignerProperty.getFilesToSign());
        ByteArrayOutputStream outputStream =new ByteArrayOutputStream();
        PdfStamper escritor =new PdfStamper(lector, outputStream);

        Font fuente = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, Font.UNDERLINE);
        Font fuente2 = FontFactory.getFont(FontFactory.HELVETICA, 6);
        String[] palabras;
        Integer tipo = sSignerProperty.getTipoFirma();
        if(tipo == 1 || tipo == 2){
            palabras = sSignerProperty.getFirmaAvisoVal().split(" ");
        }else if(tipo == 3 || tipo == 4){
            palabras = sSignerProperty.getVisadoAvisoVal().split(" ");
        }else{
            palabras = null;
        }

        float anchoMaximoLinea = 500;
        Phrase frase2 = new Phrase();
        float anchoAcumulado = 0;
        float posicionX = 47;
        float posicionY = 121;
        PdfContentByte contenido = escritor.getOverContent(1);
        for (String palabra : palabras) {
            boolean caracterEncontrado = palabra.contains("\n") ? true : false;
            float anchoPalabra = fuente.getBaseFont().getWidthPoint(palabra, fuente2.getSize());
            if (anchoAcumulado + anchoPalabra > anchoMaximoLinea || caracterEncontrado) {
                ColumnText.showTextAligned(contenido, Element.ALIGN_LEFT, frase2, posicionX, posicionY, 0);
                frase2 = new Phrase();
                posicionY -= 8;
                anchoAcumulado = 0;
            }
            if(caracterEncontrado){
                palabra = palabra.replace("\n","");
            }
            frase2.add(new Phrase(palabra + " ", fuente2));
            anchoAcumulado += anchoPalabra;
        }
        ColumnText.showTextAligned(contenido, Element.ALIGN_LEFT, frase2, posicionX, posicionY, 0);
        escritor.close();
        lector.close();

        String pathOut = sSignerProperty.getModificadoGlosaFolderPath() + "/" + sSignerProperty.getFilesToSign();
        FileOutputStream outputStr = new FileOutputStream(pathOut);
        outputStr.write(outputStream.toByteArray());
        outputStr.close();
    }

    public byte[] loadFile(SSignerProperty sSignerProperty, Integer finalOutputPath) throws IOException {
        String path = "";
        switch (finalOutputPath){
            case 1:
                path = sSignerProperty.getDestinationFolderPath() + '/' +  sSignerProperty.getFileSigned();
                break;
            case 2:
                path = sSignerProperty.getTempFolderPath() + '/' +  sSignerProperty.getFileSigned();
                break;
        }
        java.io.File file = new java.io.File(path);
        if (file.exists()){
            return Files.readAllBytes(file.toPath());
        }else{
            return null;
        }
    }

    public void unzipFile(MultipartFile archivoZIP) throws IOException {
        ZipInputStream zipInputStream = new ZipInputStream(archivoZIP.getInputStream());
        ZipEntry entrada;
        while ((entrada = zipInputStream.getNextEntry()) != null) {
            String nombreArchivo = entrada.getName();
            File archivoSalida = new File(rutaArchivosDescomprimidos, nombreArchivo);
            if (entrada.isDirectory()) {
                archivoSalida.mkdirs();
            } else {
                try (FileOutputStream fos = new FileOutputStream(archivoSalida)) {
                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = zipInputStream.read(buffer)) != -1) {
                        fos.write(buffer, 0, bytesRead);
                    }
                }
            }
            zipInputStream.closeEntry();
        }

    }

    public SSignerMasiveList readXMLFile(MultipartFile archivoXML) throws JAXBException, IOException {
        JAXBContext jaxbContext = JAXBContext.newInstance(SSignerMasiveList.class);
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        String xmlString = new String(archivoXML.getBytes(), "UTF-8");
        SSignerMasiveList sSignerMasiveList = (SSignerMasiveList) unmarshaller.unmarshal(new StringReader(xmlString));

        return  sSignerMasiveList;
    }

    public void moveFile(SSignerProperty sSignerProperty) throws IOException {
        File archivoMassive = new File(rutaArchivosDescomprimidos + sSignerProperty.getFilesToSign());
        Path archivoDestino = Paths.get(sSignerProperty.getOriginFolderPath(), sSignerProperty.getFilesToSign());
        Files.copy(archivoMassive.toPath(), archivoDestino, StandardCopyOption.REPLACE_EXISTING);
    }


    public ByteArrayOutputStream  saveXMLFile(List<ResponseMassive> responseList, ArrayList<SSignerProperty> sSignerPropertyArrayList) throws Exception {
        ResponseMassiveList responseMassiveList = new ResponseMassiveList(responseList);
        JAXBContext jaxbContext = JAXBContext.newInstance(ResponseMassiveList.class);
        Marshaller marshaller = jaxbContext.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

        StringWriter stringWriter = new StringWriter();
        marshaller.marshal(responseMassiveList, stringWriter);

        String xmlContent = stringWriter.toString();
        File tempFile = new File("archivo.xml");
        try(FileWriter fileWriter = new FileWriter(tempFile)){
            fileWriter.write(xmlContent);
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ZipOutputStream zipOut = new ZipOutputStream(baos);

        addToZipFile(tempFile, zipOut);

        for(SSignerProperty sSignerProperty : sSignerPropertyArrayList){
            java.io.File file = new java.io.File(sSignerProperty.getDestinationFolderPath() + '/' +  sSignerProperty.getFileSigned());
            addToZipFile(file, zipOut);
        }

        zipOut.close();
        baos.close();

        return baos;
    }

    public void addToZipFile(File file, ZipOutputStream zipOut) throws Exception{
        FileInputStream fis = new FileInputStream(file);
        ZipEntry zipEntry = new ZipEntry(file.getName());
        zipOut.putNextEntry(zipEntry);

        byte[] bytes = new byte[1024];
        int length;
        while ((length = fis.read(bytes)) >= 0) {
            zipOut.write(bytes, 0, length);
        }

        fis.close();
    }

}
