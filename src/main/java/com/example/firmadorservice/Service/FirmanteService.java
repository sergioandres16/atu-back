package com.example.firmadorservice.Service;

import com.example.firmadorservice.Entity.Firmante;
import com.example.firmadorservice.Repository.FirmanteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class FirmanteService {
    @Autowired
    FirmanteRepository firmanteRepository;

    public boolean existFirmante(String dniStr){
        Optional<Firmante> optionalFirmante = firmanteRepository.findFirmante(dniStr);
        return optionalFirmante.isPresent();
    }
}
