package com.example.firmadorservice.Service;

import com.example.firmadorservice.Entity.Certificado;
import com.example.firmadorservice.Entity.FirmaDigital;
import com.example.firmadorservice.Entity.RegistroFirma;
import com.example.firmadorservice.Model.Response;
import com.example.firmadorservice.Model.SSignerProperty;
import com.example.firmadorservice.Repository.CertificadoRepository;
import com.example.firmadorservice.Repository.FirmaDigitalRepository;
import com.example.firmadorservice.Repository.RegistroFirmaRepository;
import com.example.firmadorservice.Utils.Logs;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.*;
import com.itextpdf.signatures.*;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.CertificateExpiredException;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.*;

@Service
public class SignService {

    private final String keyStore = "PKCS12"; // Windows: Windows-MY     Linux: PKCS12
    SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm dd-MM-yyyy");

    @Autowired
    FirmaDigitalRepository firmaDigitalRepository;

    @Autowired
    CertificadoRepository certificadoRepository;

    @Autowired
    FileService fileService;

    @Autowired
    RegistroFirmaRepository registroFirmaRepository;

    public Response signFile(SSignerProperty sSignerProperty) throws IOException {
        Response response;
        String textoFirma = "";

        // 1) Verificar si existe Certificado en la tabla
        Optional<Certificado> optionalCertificado =
                certificadoRepository.findCertificadoByIDFirmante(sSignerProperty.getDni());
        if (optionalCertificado.isPresent()) {
            try {
                // 2) Preparar keystore
                String nombreCertificado = optionalCertificado.get().getNombreCertificado();
                String passCertificado = CertificateService.decryptCertificatePassword(
                        optionalCertificado.get().getPassCertificado()
                );

                BouncyCastleProvider provider = new BouncyCastleProvider();
                Security.addProvider(provider);

                File certificado = new File(sSignerProperty.getCertPath() + "/" + nombreCertificado);
                if (!certificado.exists()) {
                    // ERROR: Certificado no se encuentra en el servidor
                    String mensajeError = "El certificado del firmante " + sSignerProperty.getDni()
                            + " no se encuentra registrado en el servidor.";
                    Logs.writeErrorParams(mensajeError, sSignerProperty);
                    Logs.writeLogsFile(mensajeError);

                    return new Response(sSignerProperty.getFilesToSign(), null, false, mensajeError);
                }
                InputStream is = new FileInputStream(certificado);
                KeyStore ks = KeyStore.getInstance(keyStore);
                ks.load(is, passCertificado.toCharArray());

                String alias = null;
                Enumeration<String> aliases = ks.aliases();
                if (aliases.hasMoreElements()) {
                    alias = aliases.nextElement();
                }

                PrivateKey key = (PrivateKey) ks.getKey(alias, passCertificado.toCharArray());
                java.security.cert.Certificate[] chain = ks.getCertificateChain(alias);

                // 3) Verificar tipo de firma
                Integer tipo = sSignerProperty.getTipoFirma();
                String rutaFile;
                if (tipo == 1 || tipo == 2) {
                    if (tipo == 2) {
                        fileService.insertGlosa(sSignerProperty);
                        rutaFile = sSignerProperty.getModificadoGlosaFolderPath() + "/" + sSignerProperty.getFilesToSign();
                    } else {
                        // tipo == 1
                        if(sSignerProperty.getNroVistos() == 0){
                            fileService.insertGlosa(sSignerProperty);
                            fileService.insertCorrelativo(sSignerProperty);
                            rutaFile = sSignerProperty.getModificadoTituloFolderPath()+ "/" + sSignerProperty.getFilesToSign();
                        }else{
                            rutaFile = sSignerProperty.getOriginFolderPath()+"/"+sSignerProperty.getFilesToSign();
                        }
                    }

                    // 4) Firmar PDF
                    OutputStream fout = new FileOutputStream(
                            sSignerProperty.getDestinationFolderPath() + "/" + sSignerProperty.getFileSigned()
                    );
                    PdfReader reader = new PdfReader(rutaFile);
                    PdfSigner pdfSigner = new PdfSigner(
                            reader,
                            fout,
                            new StampingProperties().useAppendMode()
                    );
                    pdfSigner.setCertificationLevel(PdfSigner.NOT_CERTIFIED);

                    X509Certificate xcert = (X509Certificate) ks.getCertificate(alias);
                    String expirationDate = dateFormat.format(xcert.getNotAfter().getTime());

                    // Verificar vigencia del certificado
                    try {
                        xcert.checkValidity();
                    } catch (CertificateExpiredException e) {
                        String mensajeError = "El certificado del firmante " + sSignerProperty.getDni() + " / "
                                + nombreCertificado + " ya expiró el " + expirationDate + ".";
                        Logs.writeErrorParams(mensajeError, sSignerProperty);
                        Logs.writeLogsFile(mensajeError);
                        return new Response(sSignerProperty.getFileSigned(), null, false, mensajeError);
                    }

                    String nombresFirmante = CertificateService.getCertUserName(xcert);
                    String motivo = sSignerProperty.getFirmaMotivo();
                    String sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date());

                    float WidthFirm, HeightFirm, XFirm, YFirm;
                    if (tipo == 1 && sSignerProperty.getNroVistos() > 0) {
                        // Firma con título
                        fileService.createFirmaConTituloImageFile(sSignerProperty, nombresFirmante, motivo, sdf);
                        WidthFirm = 525;
                        HeightFirm = 80 + sSignerProperty.getPosicionSuperior() / 3;
                        XFirm = sSignerProperty.getPosX();
                        YFirm = sSignerProperty.getPosY();
                    } else {
                        // Firma normal
                        fileService.createFirmaImageFile(sSignerProperty, nombresFirmante, motivo, sdf);
                        Rectangle rect = new Rectangle(
                                sSignerProperty.getPosX(),
                                sSignerProperty.getPosY(),
                                sSignerProperty.getFirmaAncho(),
                                sSignerProperty.getFirmaAlto()
                        );
                        WidthFirm = sSignerProperty.getImageFirmaWidth() + sSignerProperty.getFirmaAncho();
                        HeightFirm = sSignerProperty.getFirmaAlto();
                        XFirm = sSignerProperty.getPosX();
                        YFirm = sSignerProperty.getPosY();
                    }

                    PdfSignatureAppearance sap = pdfSigner.getSignatureAppearance();
                    sap.setRenderingMode(PdfSignatureAppearance.RenderingMode.GRAPHIC);
                    sap.setSignatureGraphic(ImageDataFactory.create(
                            sSignerProperty.getImageSignPath() + "/" + sSignerProperty.getFirmaImageName()
                    ));
                    sap.setReason(sSignerProperty.getFirmaMotivo());
                    sap.setLocation(optionalCertificado.get().getLocation());

                    Rectangle rectFirm = new Rectangle(XFirm, YFirm, WidthFirm, HeightFirm);
                    sap.setPageRect(rectFirm).setPageNumber(1);

                    PrivateKeySignature pks = new PrivateKeySignature(key, "sha256", provider.getName());
                    pdfSigner.signDetached(new BouncyCastleDigest(), pks, chain,
                            null, null, null, 40000, PdfSigner.CryptoStandard.CMS);

                    reader.close();
                    fout.flush();
                    fout.close();

                    // LTV
                    if (optionalCertificado.get().getLtv() == 1) {
                        OutputStream padesOut = new FileOutputStream(
                                sSignerProperty.getTempFolderPath() + "/" + sSignerProperty.getFileSigned()
                        );
                        PdfReader reader2 = new PdfReader(
                                sSignerProperty.getDestinationFolderPath() + "/" + sSignerProperty.getFileSigned()
                        );
                        PdfWriter writer2 = new PdfWriter(padesOut);
                        PdfDocument doc2 = new PdfDocument(reader2, writer2, new StampingProperties().useAppendMode());

                        LtvVerification v = new LtvVerification(doc2);
                        SignatureUtil signatureUtil = new SignatureUtil(doc2);
                        List<String> names = signatureUtil.getSignatureNames();
                        for (String name : names) {
                            v.addVerification(
                                    name,
                                    new OcspClientBouncyCastle(null),
                                    new CrlClientOnline(),
                                    LtvVerification.CertificateOption.WHOLE_CHAIN,
                                    LtvVerification.Level.OCSP_CRL,
                                    LtvVerification.CertificateInclusion.YES
                            );
                        }
                        v.merge();
                        doc2.close();
                        writer2.close();
                        reader2.close();
                    }

                    Integer finalOutputPath = (optionalCertificado.get().getLtv() == 1) ? 2 : 1;

                    // Respuesta final
                    response = new Response(
                            sSignerProperty.getFileSigned(),
                            fileService.loadFile(sSignerProperty, finalOutputPath),
                            true,
                            "Documento Firmado exitosamente"
                    );
                    fileService.deleteFile(sSignerProperty, tipo);

                    // >>> Guardar en firmadigital (NO se modifica) <<<
                    FirmaDigital savedFirmaDigital = firmaDigitalRepository.save(
                            new FirmaDigital(
                                    sSignerProperty.getDni(),
                                    sSignerProperty.getDestinationFolderPath() + "/" + sSignerProperty.getFileSigned(),
                                    sSignerProperty.getFilesToSign(),
                                    sSignerProperty.getFirmaCorrelativo(),
                                    sSignerProperty.getFirmaAvisoVal(),
                                    sSignerProperty.getFirmaNumExpediente(),
                                    Instant.now()
                            )
                    );

                    // >>> Registrar también en registrofirma <<<
                    RegistroFirma registro = new RegistroFirma();
                    registro.setFirmadigitalId(savedFirmaDigital.getId());
                    registro.setDni(sSignerProperty.getDni());
                    registro.setTipoFirma(String.valueOf(tipo));
                    registro.setPosicionTitulo(String.valueOf(sSignerProperty.getPosicionTitulo()));
                    registro.setPosicionSuperior(String.valueOf(sSignerProperty.getPosicionSuperior()));
                    registro.setCoordenadas(sSignerProperty.getCoordenadas());
                    registro.setAvisoVal(sSignerProperty.getFirmaAvisoVal());
                    registro.setNomUsuario(sSignerProperty.getNomUsuario());
                    registro.setCorrelativo(sSignerProperty.getFirmaCorrelativo());
                    registro.setNumExpediente(sSignerProperty.getFirmaNumExpediente());
                    registro.setMotivo(sSignerProperty.getFirmaMotivo());
                    registro.setNroVistos(String.valueOf(sSignerProperty.getNroVistos()));
                    registro.setTamanoLetra(
                            (sSignerProperty.getTamanoLetra() == null)
                                    ? ""
                                    : String.valueOf(sSignerProperty.getTamanoLetra())
                    );
                    // Aquí cambiamos a getNegritaLetra()
                    registro.setNegritaLetra(
                            sSignerProperty.getNegritaLetra() == null
                                    ? ""
                                    : String.valueOf(sSignerProperty.getNegritaLetra())
                    );
                    registro.setTamanoImagen(
                            (sSignerProperty.getTamanoImagen() == null)
                                    ? ""
                                    : String.valueOf(sSignerProperty.getTamanoImagen())
                    );
                    registro.setFechaHora(Instant.now());
                    Logs.writeLogsFile("Voy a guardar en registrofirma con ID de firmadigital=" + savedFirmaDigital.getId());

                    registroFirmaRepository.save(registro);

                } else if (tipo == 3 || tipo == 4) {
                    // VISADO
                    fileService.createVisadoImageFile(sSignerProperty);

                    if (tipo == 4) {
                        fileService.insertGlosa(sSignerProperty);
                        rutaFile = sSignerProperty.getModificadoGlosaFolderPath() + "/" + sSignerProperty.getFilesToSign();
                    } else {
                        rutaFile = sSignerProperty.getOriginFolderPath() + "/" + sSignerProperty.getFilesToSign();
                    }

                    OutputStream fout = new FileOutputStream(
                            sSignerProperty.getDestinationFolderPath() + "/" + sSignerProperty.getFileSigned()
                    );
                    PdfReader reader = new PdfReader(rutaFile);
                    PdfSigner pdfSigner = new PdfSigner(
                            reader,
                            fout,
                            new StampingProperties().useAppendMode()
                    );
                    pdfSigner.setCertificationLevel(PdfSigner.NOT_CERTIFIED);

                    X509Certificate xcert = (X509Certificate) ks.getCertificate(alias);
                    String expirationDate = dateFormat.format(xcert.getNotAfter().getTime());
                    try {
                        xcert.checkValidity();
                    } catch (CertificateExpiredException e) {
                        String mensajeError = "El certificado del firmante " + sSignerProperty.getDni() + " / "
                                + nombreCertificado + " ya expiró el " + expirationDate + ".";
                        Logs.writeErrorParams(mensajeError, sSignerProperty);
                        Logs.writeLogsFile(mensajeError);
                        return new Response(sSignerProperty.getFileSigned(), null, false, mensajeError);
                    }

                    Rectangle rect = new Rectangle(
                            sSignerProperty.getPosX(),
                            sSignerProperty.getPosY(),
                            sSignerProperty.getFirmaAncho(),
                            sSignerProperty.getFirmaAlto()
                    );
                    float WidthFirm = sSignerProperty.getImageVisadoWidth();
                    float HeightFirm = sSignerProperty.getImageVisadoHeight();
                    float XFirm = sSignerProperty.getPosX();
                    float YFirm = sSignerProperty.getPosY();

                    PdfSignatureAppearance sap = pdfSigner.getSignatureAppearance();
                    sap.setRenderingMode(PdfSignatureAppearance.RenderingMode.GRAPHIC);
                    sap.setSignatureGraphic(ImageDataFactory.create(
                            sSignerProperty.getImageSignPath() + "/" + sSignerProperty.getVisadoImageName()
                    ));
                    sap.setReason(sSignerProperty.getFirmaMotivo());
                    sap.setLocation(optionalCertificado.get().getLocation());
                    Rectangle rectFirm = new Rectangle(XFirm, YFirm, WidthFirm, HeightFirm);
                    sap.setPageRect(rectFirm).setPageNumber(1);

                    PrivateKeySignature pks = new PrivateKeySignature(key, "sha256", provider.getName());
                    pdfSigner.signDetached(new BouncyCastleDigest(), pks, chain,
                            null, null, null, 40000, PdfSigner.CryptoStandard.CMS);

                    reader.close();
                    fout.flush();
                    fout.close();

                    // LTV
                    if (optionalCertificado.get().getLtv() == 1) {
                        OutputStream padesOut = new FileOutputStream(
                                sSignerProperty.getTempFolderPath() + "/" + sSignerProperty.getFileSigned()
                        );
                        PdfReader reader2 = new PdfReader(
                                sSignerProperty.getDestinationFolderPath() + "/" + sSignerProperty.getFileSigned()
                        );
                        PdfWriter writer2 = new PdfWriter(padesOut);
                        PdfDocument doc2 = new PdfDocument(reader2, writer2, new StampingProperties().useAppendMode());

                        LtvVerification v = new LtvVerification(doc2);
                        SignatureUtil signatureUtil = new SignatureUtil(doc2);
                        List<String> names = signatureUtil.getSignatureNames();
                        for (String name : names) {
                            v.addVerification(
                                    name,
                                    new OcspClientBouncyCastle(null),
                                    new CrlClientOnline(),
                                    LtvVerification.CertificateOption.WHOLE_CHAIN,
                                    LtvVerification.Level.OCSP_CRL,
                                    LtvVerification.CertificateInclusion.NO
                            );
                        }
                        v.merge();
                        doc2.close();
                        writer2.close();
                        reader2.close();
                    }
                    Integer finalOutputPath = (optionalCertificado.get().getLtv() == 1) ? 2 : 1;

                    Response responseVisado = new Response(
                            sSignerProperty.getFileSigned(),
                            fileService.loadFile(sSignerProperty, finalOutputPath),
                            true,
                            "Documento Visado exitosamente"
                    );
                    fileService.deleteFile(sSignerProperty, tipo);

                    // >>> Guardar en firmadigital (NO se toca) <<<
                    FirmaDigital savedFirmaDigital = firmaDigitalRepository.save(
                            new FirmaDigital(
                                    sSignerProperty.getDni(),
                                    sSignerProperty.getDestinationFolderPath() + "/" + sSignerProperty.getFileSigned(),
                                    sSignerProperty.getFilesToSign(),
                                    "",
                                    sSignerProperty.getVisadoAvisoVal(),
                                    "",
                                    Instant.now()
                            )
                    );

                    // >>> Insertar en registrofirma <<<
                    RegistroFirma registro = new RegistroFirma();
                    registro.setFirmadigitalId(savedFirmaDigital.getId());
                    registro.setDni(sSignerProperty.getDni());
                    registro.setTipoFirma(String.valueOf(tipo));
                    registro.setPosicionTitulo(String.valueOf(sSignerProperty.getPosicionTitulo()));
                    registro.setPosicionSuperior(String.valueOf(sSignerProperty.getPosicionSuperior()));
                    registro.setCoordenadas(sSignerProperty.getCoordenadas());

                    // Para visado, es getVisadoAvisoVal()
                    registro.setAvisoVal(sSignerProperty.getVisadoAvisoVal());
                    registro.setNomUsuario(sSignerProperty.getNomUsuario());
                    registro.setCorrelativo("");
                    registro.setNumExpediente("");
                    registro.setMotivo(sSignerProperty.getFirmaMotivo());
                    registro.setNroVistos(String.valueOf(sSignerProperty.getNroVistos()));
                    registro.setTamanoLetra(
                            (sSignerProperty.getTamanoLetra() == null)
                                    ? ""
                                    : String.valueOf(sSignerProperty.getTamanoLetra())
                    );
                    // Aquí cambiamos a getNegritaLetra()
                    registro.setNegritaLetra(
                            sSignerProperty.getNegritaLetra() == null
                                    ? ""
                                    : String.valueOf(sSignerProperty.getNegritaLetra())
                    );
                    registro.setTamanoImagen(
                            (sSignerProperty.getTamanoImagen() == null)
                                    ? ""
                                    : String.valueOf(sSignerProperty.getTamanoImagen())
                    );
                    registro.setFechaHora(Instant.now());
                    Logs.writeLogsFile("Voy a guardar en registrofirma con ID de firmadigital=" + savedFirmaDigital.getId());

                    registroFirmaRepository.save(registro);

                    return responseVisado;

                } else if (tipo == 5) {
                    // Firma con glosa custom
                    OutputStream fout = new FileOutputStream(
                            sSignerProperty.getDestinationFolderPath() + "/" + sSignerProperty.getFileSigned()
                    );
                    PdfReader reader = new PdfReader(
                            sSignerProperty.getOriginFolderPath() + "/" + sSignerProperty.getFilesToSign()
                    );
                    PdfSigner pdfSigner = new PdfSigner(reader, fout, new StampingProperties().useAppendMode());
                    pdfSigner.setCertificationLevel(PdfSigner.NOT_CERTIFIED);

                    X509Certificate xcert = (X509Certificate) ks.getCertificate(alias);
                    String expirationDate = dateFormat.format(xcert.getNotAfter().getTime());
                    try {
                        xcert.checkValidity();
                    } catch (CertificateExpiredException e) {
                        String mensajeError = "El certificado del firmante " + sSignerProperty.getDni() + " / "
                                + nombreCertificado + " ya expiró el " + expirationDate + ".";
                        Logs.writeErrorParams(mensajeError, sSignerProperty);
                        Logs.writeLogsFile(mensajeError);
                        return new Response(sSignerProperty.getFileSigned(), null, false, mensajeError);
                    }

                    String nombresFirmante = CertificateService.getCertUserName(xcert);
                    String motivo = sSignerProperty.getFirmaMotivo();
                    String sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date());

                    fileService.createFirmaImageFile(sSignerProperty, nombresFirmante, motivo, sdf);
                    Rectangle rect = new Rectangle(
                            sSignerProperty.getPosX(),
                            sSignerProperty.getPosY(),
                            sSignerProperty.getFirmaAncho(),
                            sSignerProperty.getFirmaAlto()
                    );

                    PdfSignatureAppearance sap = pdfSigner.getSignatureAppearance();
                    sap.setRenderingMode(PdfSignatureAppearance.RenderingMode.GRAPHIC);
                    sap.setSignatureGraphic(ImageDataFactory.create(
                            sSignerProperty.getImageSignPath() + "/" + sSignerProperty.getFirmaImageName()
                    ));
                    sap.setReason(sSignerProperty.getFirmaMotivo());
                    sap.setLocation(optionalCertificado.get().getLocation());

                    sap.setPageRect(rect).setPageNumber(1);

                    PrivateKeySignature pks = new PrivateKeySignature(key, "sha256", provider.getName());
                    pdfSigner.signDetached(new BouncyCastleDigest(), pks, chain,
                            null, null, null, 40000, PdfSigner.CryptoStandard.CMS);

                    reader.close();
                    fout.flush();
                    fout.close();

                    // LTV
                    if (optionalCertificado.get().getLtv() == 1) {
                        OutputStream padesOut = new FileOutputStream(
                                sSignerProperty.getTempFolderPath() + "/" + sSignerProperty.getFileSigned()
                        );
                        PdfReader reader2 = new PdfReader(
                                sSignerProperty.getDestinationFolderPath() + "/" + sSignerProperty.getFileSigned()
                        );
                        PdfWriter writer2 = new PdfWriter(padesOut);
                        PdfDocument doc2 = new PdfDocument(reader2, writer2, new StampingProperties().useAppendMode());

                        LtvVerification v = new LtvVerification(doc2);
                        SignatureUtil signatureUtil = new SignatureUtil(doc2);
                        List<String> names = signatureUtil.getSignatureNames();
                        for (String name : names) {
                            v.addVerification(
                                    name,
                                    new OcspClientBouncyCastle(null),
                                    new CrlClientOnline(),
                                    LtvVerification.CertificateOption.WHOLE_CHAIN,
                                    LtvVerification.Level.OCSP_CRL,
                                    LtvVerification.CertificateInclusion.YES
                            );
                        }
                        v.merge();
                        doc2.close();
                        writer2.close();
                        reader2.close();
                    }
                    Integer finalOutputPath = (optionalCertificado.get().getLtv() == 1) ? 2 : 1;

                    Response responseGlosaCustom = new Response(
                            sSignerProperty.getFileSigned(),
                            fileService.loadFile(sSignerProperty, finalOutputPath),
                            true,
                            "Documento Firmado exitosamente"
                    );
                    fileService.deleteFile(sSignerProperty, tipo);

                    // >>> Guardar en firmadigital (NO modificar) <<<
                    FirmaDigital savedFirmaDigital = firmaDigitalRepository.save(
                            new FirmaDigital(
                                    sSignerProperty.getDni(),
                                    sSignerProperty.getDestinationFolderPath() + "/" + sSignerProperty.getFileSigned(),
                                    sSignerProperty.getFilesToSign(),
                                    sSignerProperty.getFirmaCorrelativo(),
                                    sSignerProperty.getFirmaAvisoVal(),
                                    sSignerProperty.getFirmaNumExpediente(),
                                    Instant.now()
                            )
                    );

                    // >>> Insertar en registrofirma <<<
                    RegistroFirma registro = new RegistroFirma();
                    registro.setFirmadigitalId(savedFirmaDigital.getId());
                    registro.setDni(sSignerProperty.getDni());
                    registro.setTipoFirma(String.valueOf(tipo));
                    registro.setPosicionTitulo(String.valueOf(sSignerProperty.getPosicionTitulo()));
                    registro.setPosicionSuperior(String.valueOf(sSignerProperty.getPosicionSuperior()));
                    registro.setCoordenadas(sSignerProperty.getCoordenadas());
                    registro.setAvisoVal(sSignerProperty.getFirmaAvisoVal());
                    registro.setNomUsuario(sSignerProperty.getNomUsuario());
                    registro.setCorrelativo(sSignerProperty.getFirmaCorrelativo());
                    registro.setNumExpediente(sSignerProperty.getFirmaNumExpediente());
                    registro.setMotivo(sSignerProperty.getFirmaMotivo());
                    registro.setNroVistos(String.valueOf(sSignerProperty.getNroVistos()));
                    registro.setTamanoLetra(
                            (sSignerProperty.getTamanoLetra() == null)
                                    ? ""
                                    : String.valueOf(sSignerProperty.getTamanoLetra())
                    );
                    // Aquí cambiamos a getNegritaLetra()
                    registro.setNegritaLetra(
                            sSignerProperty.getNegritaLetra() == null
                                    ? ""
                                    : String.valueOf(sSignerProperty.getNegritaLetra())
                    );
                    registro.setTamanoImagen(
                            (sSignerProperty.getTamanoImagen() == null)
                                    ? ""
                                    : String.valueOf(sSignerProperty.getTamanoImagen())
                    );
                    registro.setFechaHora(Instant.now());
                    Logs.writeLogsFile("Voy a guardar en registrofirma con ID de firmadigital=" + savedFirmaDigital.getId());
                    registroFirmaRepository.save(registro);

                    return responseGlosaCustom;

                } else {
                    // ERROR: Tipo de Firma no existe
                    String mensajeError = "Tipo de Firma no existe";
                    Logs.writeErrorParams(mensajeError, sSignerProperty);
                    Logs.writeLogsFile(mensajeError);
                    response = new Response(sSignerProperty.getFilesToSign(), null, false, mensajeError);
                }

            } catch (Exception e) {
                e.printStackTrace();
                // Log diario con parámetros
                String mensajeError = e.getMessage();
                Logs.writeErrorParams(
                        "Ocurrió un error al ejecutar el servicio para el firmante "
                                + sSignerProperty.getDni() + " : " + mensajeError,
                        sSignerProperty
                );
                // Log general
                Logs.writeLogsFile("Ocurrió un error al ejecutar el servicio para el firmante "
                        + sSignerProperty.getDni() + " : " + mensajeError);

                response = new Response(sSignerProperty.getFilesToSign(), null, false, mensajeError);
            }

        } else {
            // ERROR: Certificado no existe
            String mensajeError = "Certificado del firmante " + sSignerProperty.getDni() + " no existe";
            Logs.writeErrorParams(mensajeError, sSignerProperty);
            Logs.writeLogsFile(mensajeError);
            response = new Response(sSignerProperty.getFilesToSign(), null, false, mensajeError);
        }

        return response;
    }
}