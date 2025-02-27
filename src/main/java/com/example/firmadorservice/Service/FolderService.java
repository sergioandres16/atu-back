package com.example.firmadorservice.Service;

import com.example.firmadorservice.Model.SSignerProperty;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;

@Service
public class FolderService {
    public void createFolders(SSignerProperty sSignerProperty){
        new File(sSignerProperty.getOriginFolderPath()).mkdirs();
        new File(sSignerProperty.getDestinationFolderPath()).mkdirs();
        new File(sSignerProperty.getTempFolderPath()).mkdirs();
        new File(sSignerProperty.getModificadoGlosaFolderPath()).mkdirs();
        new File(sSignerProperty.getModificadoTituloFolderPath()).mkdirs();
    }

    public void cleanFolders(SSignerProperty sSignerProperty) throws IOException {
        FileUtils.cleanDirectory(new File(sSignerProperty.getOriginFolderPath()));
        FileUtils.cleanDirectory(new File(sSignerProperty.getDestinationFolderPath()));
    }


}
