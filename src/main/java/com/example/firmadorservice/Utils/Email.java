package com.example.firmadorservice.Utils;

import sendinblue.ApiClient;
import sendinblue.Configuration;
import sendinblue.auth.ApiKeyAuth;
import sibApi.TransactionalEmailsApi;
import sibModel.CreateSmtpEmail;
import sibModel.SendSmtpEmail;
import sibModel.SendSmtpEmailSender;
import sibModel.SendSmtpEmailTo;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Email {
    private static String URLB = "https://api.brevo.com/v3/smtp/emails";
    private static String apikey = "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";

    private static String remitente = "notificaciones@plussigner.com";

    private static String usuario = "SAETA";

    private static String url = "https://atu.plussigner.com/ATU/register";

    public static void sendEmail(String correo, String uuid) throws IOException {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setConnectTimeout(60000);
        defaultClient.setWriteTimeout(60000);
        defaultClient.setReadTimeout(60000);
        ApiKeyAuth apiKey = (ApiKeyAuth) defaultClient.getAuthentication("api-key");
        apiKey.setApiKey(apikey);

        try {
            TransactionalEmailsApi api = new TransactionalEmailsApi();

            SendSmtpEmailSender sender = new SendSmtpEmailSender();
            sender.setEmail(remitente);
            sender.setName(usuario);
            List<SendSmtpEmailTo> destlist = new ArrayList<SendSmtpEmailTo>();

            SendSmtpEmailTo to = new SendSmtpEmailTo();
            to.setEmail(correo);
            destlist.add(to);

            SendSmtpEmail sendSmtpEmail = new SendSmtpEmail();
            sendSmtpEmail.setSender(sender);
            sendSmtpEmail.setTo(destlist);

            sendSmtpEmail.setSubject("REGISTRO DE PIN - FIRMA MÓVIL");

            String link = url + "/"+uuid;
            //String encabezadoCorreo = "<html><body>Estimado usuario:<br>El siguiente link será usado para registrar su PIN: "+link+"<br>";
            String encabezadoCorreo = "<html><body style='background-color: #ffffff; font-family: Arial, sans-serif;'>"
                    + "<img src='https://boleta.plussigner.com/TITULOS/logoSaeta.png' alt='Saeta' style='width:100%; max-width:300px; display:block; margin:auto;'>"
                    + "<p style='font-size: 16px; color: #555; margin-bottom: 20px;'>Estimado usuario: </p>"
                    + "<p style='font-size: 16px; color: #555; margin-bottom: 10px;'>Le damos la bienvenida al Portal de Actualización de PIN de su certificado digital. Para continuar, por favor, pulse el botón 'Registrar PIN'.</p>"
                    + "<table border='0' cellpadding='0' cellspacing='0' style='margin: 0 auto;'>"
                    + "<tbody>"
                    + "<tr>"
                    + "<td align='center'>"
                    + "<table border='0' cellpadding='0' cellspacing='0'>"
                    + "<tbody>"
                    + "<tr>"
                    + "<td><a href='" + link + "' target='_blank'><button style='background-color: #4285f4; color: white; padding: 12px 30px; border: none; border-radius: 5px; cursor: pointer; font-size: 16px;'>Registrar PIN</button></a></td>"
                    + "</tr>"
                    + "</tbody>"
                    + "</table>"
                    + "</td>"
                    + "</tr>"
                    + "</tbody>"
                    + "</table>"
                    + "<p style='font-size: 16px; color: #555; margin-top: 20px;'>Para mayor información respecto al uso de esta plataforma, sírvase comunicarse a nuestro correo electrónico: <span style='font-weight: bold;'>soporte@saeta.pe</span></p>"
                    + "</body></html>";
            String finalCorreo = "<p style='font-size: 16px; color: #555; margin-top: 20px;'>Atentamente,<br>Saeta</p>";
            //String finalCorreo = "<br>Atentamente,<br>Saeta</body></html>";
            sendSmtpEmail.setHtmlContent(encabezadoCorreo+finalCorreo);

            CreateSmtpEmail response2 = api.sendTransacEmail(sendSmtpEmail);
        } catch (Exception e) {
            String mensajeError = "Error al enviar correo a la direccion: "+correo;
            Logs.writeLogsFile(mensajeError);
        }
    }
}
