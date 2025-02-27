package com.example.firmadorservice.Service;

import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.stereotype.Service;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import javax.naming.ldap.LdapName;
import javax.naming.ldap.Rdn;
import java.security.*;
import java.security.cert.*;

@Service
public class CertificateService {

    public static String getCertUserName(X509Certificate certificate) {
        String dn = certificate.getSubjectDN().toString();
        String certUserName = "";
        try {
            LdapName ln = new LdapName(dn);
            for (Rdn rdn : ln.getRdns())
                if (rdn.getType().equalsIgnoreCase("CN")) {
                    certUserName = rdn.getValue().toString();
                    return certUserName;
                }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getCertBusiness(X509Certificate certificate){
        String dn = certificate.getSubjectDN().toString();
        String certBusiness = "";
        try {
            LdapName ln = new LdapName(dn);
            for (Rdn rdn : ln.getRdns())
                if (rdn.getType().equalsIgnoreCase("O")) {
                    certBusiness = rdn.getValue().toString();
                    return certBusiness;
                }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getCertPosition(X509Certificate certificate){
        String dn = certificate.getSubjectDN().toString();
        String certPosition = "";
        try {
            LdapName ln = new LdapName(dn);
            for (Rdn rdn : ln.getRdns())
                if (rdn.getType().equalsIgnoreCase("T")) {
                    certPosition = rdn.getValue().toString();
                    return certPosition;
                }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getCertLocacion(X509Certificate certificate){
        String dn = certificate.getSubjectDN().toString();
        String certLocacion = "";
        try {
            LdapName ln = new LdapName(dn);
            for (Rdn rdn : ln.getRdns())
                if (rdn.getType().equalsIgnoreCase("ST")) {
                    certLocacion = rdn.getValue().toString();
                    return certLocacion;
                }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getSerialNumber(X509Certificate certificate) {
        String dn = certificate.getSubjectDN().toString();
        String[] tagserie = new String[]{"SERIALNUMBER"};
        try {
            LdapName ln = new LdapName(dn);
            for (Rdn rdn : ln.getRdns())
                if (rdn.getType().equalsIgnoreCase(tagserie[0])) {
                    return rdn.getValue().toString();
                }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String decryptCertificatePassword(String passwordEncrypted) throws NoSuchPaddingException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
        byte[] keyValue = new byte[] {
                50, 45, 113, 43, 56, 98, 106, 68, 35, 69,
                103, 57, 46, 82, 45, 121 };
        Key key = new SecretKeySpec(keyValue, "AES");
        Cipher c1 = Cipher.getInstance("AES");
        c1.init(Cipher.DECRYPT_MODE, key);
        byte[] decodedValue = Base64.decodeBase64(passwordEncrypted);
        byte[] decValue = c1.doFinal(decodedValue);
        String decryptedValue = new String(decValue);

        return decryptedValue;
    }

}
