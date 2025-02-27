package com.example.firmadorservice.Controller;

import com.example.firmadorservice.Model.*;
import com.example.firmadorservice.Security.TokenUtils;
import com.example.firmadorservice.Service.*;
import com.example.firmadorservice.Utils.Logs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@RestController
@CrossOrigin("*")
public class SignController {

    final
    FolderService folderService;

    final
    SignService signService;

    final
    FileService fileService;

    final
    CertificateService certificateService;

    final
    FirmanteService firmanteService;

    final
    UserService userService;

    public SignController(FolderService folderService, SignService signService, FileService fileService, CertificateService certificateService, FirmanteService firmanteService, UserService userService) {
        this.folderService = folderService;
        this.signService = signService;
        this.fileService = fileService;
        this.certificateService = certificateService;
        this.firmanteService = firmanteService;
        this.userService = userService;
    }

    @PostMapping("/sign")
    public ResponseEntity<Response> signDocumento(@RequestHeader("Authorization") String token,
                                                  @RequestParam("file") MultipartFile archivo,
                                                  @RequestParam(value = "dni") String idCliente,
                                                  @RequestParam(value = "tipoFirma") String tipoFirma,
                                                  @RequestParam(value = "posicionTitulo", required = false) String posicionTitulo,
                                                  @RequestParam(value = "posicionSuperior", required = false) String posicionSuperior,
                                                  @RequestParam(value = "coordenadas") String coordenadas,
                                                  @RequestParam(value = "avisoVal", required = false) String avisoVal,
                                                  @RequestParam(value = "nomUsuario", required = false) String nombreUsuario,
                                                  @RequestParam(value = "correlativo", required = false) String correlativo,
                                                  @RequestParam(value = "numExpediente", required = false) String numExpediente,
                                                  @RequestParam(value = "motivo", required = false) String motivo,
                                                  @RequestParam(value = "nroVistos", required = false) String numeroVistos,
                                                  @RequestParam(value = "tamanoLetra", required = false) String tamanoLetra,
                                                  @RequestParam(value = "negritaLetra", required = false) String negrita,
                                                  @RequestParam(value = "tamanoImagen", required = false) String tamanoImagen) throws IOException {
        SSignerProperty sSignerProperty = null;
        try {
            // Intentar construir el objeto SSignerProperty
            sSignerProperty = new SSignerProperty(
                    idCliente,
                    tipoFirma,
                    correlativo,
                    avisoVal,
                    numExpediente,
                    coordenadas,
                    archivo.getOriginalFilename(),
                    nombreUsuario,
                    numeroVistos,
                    posicionTitulo,
                    posicionSuperior,
                    motivo,
                    tamanoLetra,
                    negrita,
                    tamanoImagen
            );
        } catch(Exception e) {
            // Si ocurre algún error al construir SSignerProperty, creamos un objeto temporal mínimo para loguear.
            SSignerProperty tmp = new SSignerProperty();
            tmp.setDni(idCliente);
            tmp.setFilesToSign(archivo.getOriginalFilename());
            String errorMsg = "Error al construir SSignerProperty: " + e.getMessage();
            Logs.writeErrorParams(errorMsg, tmp);
            Logs.writeLogsFile(errorMsg);
            Response resp = new Response(archivo.getOriginalFilename(), null, false, errorMsg);
            return new ResponseEntity<>(resp, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        try {
            // Validar token
            if (!userService.existeUsuarioLog(TokenUtils.usernameUsuario(token))) {
                Logs.writeErrorParams("Token inválido", sSignerProperty);
                Response response = new Response(archivo.getOriginalFilename(), null, false, "Token inválido.");
                return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
            }

            // Verificar si firmante existe
            if (!firmanteService.existFirmante(idCliente)) {
                Logs.writeErrorParams("Firmante no registrado", sSignerProperty);
                Response response = new Response(archivo.getOriginalFilename(), null, false, "Firmante no registrado.");
                return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
            }

            // Verificar extensión del archivo (debe ser PDF)
            String[] partesNombre = archivo.getOriginalFilename().split("\\.");
            if (partesNombre.length < 2 || !partesNombre[1].equalsIgnoreCase("pdf")) {
                Logs.writeErrorParams("El documento debe ser un PDF", sSignerProperty);
                Response response = new Response(archivo.getOriginalFilename(), null, false, "El documento debe ser un pdf.");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }

            // Crear carpetas y guardar el archivo original
            folderService.createFolders(sSignerProperty);
            fileService.saveFile(archivo.getBytes(), sSignerProperty, 1);

            // Llamar al servicio de firma
            Response response = signService.signFile(sSignerProperty);
            if (!response.isEstado()) {
                Logs.writeErrorParams("Error en signService (estado=false)", sSignerProperty);
                return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
            }
            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (Exception e) {
            // Capturar cualquier excepción no contemplada en el flujo
            String msgError = "Excepción general en /sign: " + e.getMessage();
            Logs.writeErrorParams(msgError, sSignerProperty);
            Logs.writeLogsFile(msgError);
            Response resp = new Response(archivo.getOriginalFilename(), null, false, e.getMessage());
            return new ResponseEntity<>(resp, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/signMasivo")
    public ResponseEntity<Resource> firmaMasiva(@RequestHeader("Authorization") String token,
                                                @RequestParam("fileZip") MultipartFile archivoComprimido,
                                                @RequestParam("fileXML") MultipartFile archivoXML,
                                                @RequestParam(value = "DNI") String idCliente) throws Exception {
        // Crear un objeto temporal mínimo para loguear en caso de error inicial
        SSignerProperty tmpSigner = new SSignerProperty();
        tmpSigner.setDni(idCliente);
        tmpSigner.setFilesToSign(archivoComprimido.getOriginalFilename());

        try {
            // Validar token
            if (!userService.existeUsuarioLog(TokenUtils.usernameUsuario(token))) {
                Logs.writeErrorParams("Token inválido en signMasivo", tmpSigner);
                return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
            }

            // Verificar si firmante existe
            if (!firmanteService.existFirmante(idCliente)) {
                Logs.writeErrorParams("Firmante no registrado (signMasivo)", tmpSigner);
                return new ResponseEntity<>(null, HttpStatus.FORBIDDEN);
            }

            // Verificar extensiones
            String extZip = archivoComprimido.getOriginalFilename().split("\\.")[1];
            String extXml = archivoXML.getOriginalFilename().split("\\.")[1];
            if (!extZip.equalsIgnoreCase("zip") || !extXml.equalsIgnoreCase("xml")) {
                Logs.writeErrorParams("Archivo no es ZIP o XML (signMasivo)", tmpSigner);
                return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
            }

            // Procesar archivos masivos
            fileService.unzipFile(archivoComprimido);
            SSignerMasiveList sSignerMasiveList = fileService.readXMLFile(archivoXML);
            ArrayList<SSignerProperty> sSignerPropertyArrayList = new ArrayList<>();
            for (SSignerMasive sSignerMasive : sSignerMasiveList.getSSignerMassive()) {
                SSignerProperty sSignerProperty = new SSignerProperty(
                        idCliente,
                        String.valueOf(sSignerMasive.gettipoFirma()),
                        sSignerMasive.getcorrelativo(),
                        sSignerMasive.getavisoVal(),
                        sSignerMasive.getnumExpediente(),
                        sSignerMasive.getcoordenadas(),
                        sSignerMasive.getnomArchivo(),
                        sSignerMasive.getnomUsuario(),
                        String.valueOf(sSignerMasive.getnroVistos()),
                        sSignerMasive.getposicionTitulo(),
                        sSignerMasive.getposicionSuperior(),
                        sSignerMasive.getmotivo(),
                        sSignerMasive.getTamanoLetra(),
                        sSignerMasive.getNegrita(),
                        sSignerMasive.getTamanoImagen()
                );
                sSignerPropertyArrayList.add(sSignerProperty);
            }

            List<ResponseMassive> responseList = new ArrayList<>();
            for (SSignerProperty sSignerProperty : sSignerPropertyArrayList) {
                folderService.createFolders(sSignerProperty);
                fileService.moveFile(sSignerProperty);
                Response response = signService.signFile(sSignerProperty);
                if (!response.isEstado()) {
                    Logs.writeErrorParams("Error en signMasivo (estado=false)", sSignerProperty);
                }
                responseList.add(new ResponseMassive(
                        response.getNombreArchivo(),
                        response.isEstado(),
                        response.isEstado() ? "OK" : "NOK"
                ));
            }

            ByteArrayOutputStream archivo = fileService.saveXMLFile(responseList, sSignerPropertyArrayList);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", "archivo.zip");
            InputStreamResource resource = new InputStreamResource(new ByteArrayInputStream(archivo.toByteArray()));
            return ResponseEntity.ok().headers(headers).body(resource);

        } catch (Exception e) {
            String msgError = "Excepción general en /signMasivo: " + e.getMessage();
            Logs.writeErrorParams(msgError, tmpSigner);
            Logs.writeLogsFile(msgError);
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @ExceptionHandler({MissingRequestHeaderException.class})
    public ResponseEntity<Response> gestionarErrorTokenHeader(){
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new Response("", null, false, "Falta Authentication token"));
    }

    @ExceptionHandler({MissingServletRequestPartException.class, MissingServletRequestParameterException.class})
    public ResponseEntity<Response> gestionarFaltaParametros(){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new Response("", null, false, "Falta parámetros obligatorios"));
    }

    @ExceptionHandler({MaxUploadSizeExceededException.class})
    public ResponseEntity<Response> gestionarExcesoSizeArchivo(){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new Response("", null, false, "El tamaño máximo de los archivos es de 10MB."));
    }
}