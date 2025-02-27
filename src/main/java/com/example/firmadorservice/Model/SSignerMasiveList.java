package com.example.firmadorservice.Model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "documentos")
public class SSignerMasiveList {
    private List<SSignerMasive> documentos;

    @XmlElement(name = "documento")
    public List<SSignerMasive> getSSignerMassive() {
        return documentos;
    }

    public void setSSignerMassive(List<SSignerMasive> sSignerMasiveList){
        this.documentos = sSignerMasiveList;
    }
}
