package com.example.firmadorservice.Model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "responses")
@XmlAccessorType(XmlAccessType.FIELD)
public class ResponseMassiveList {

    @XmlElement(name = "response")
    private List<ResponseMassive> responses;

    public ResponseMassiveList(List<ResponseMassive> responses) {
        this.responses = responses;
    }

    public ResponseMassiveList() {
    }

    public List<ResponseMassive> getResponses() {
        return responses;
    }

    public void setResponses(List<ResponseMassive> responses) {
        this.responses = responses;
    }
}
