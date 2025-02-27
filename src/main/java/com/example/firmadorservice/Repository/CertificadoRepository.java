package com.example.firmadorservice.Repository;

import com.example.firmadorservice.Entity.Certificado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CertificadoRepository extends JpaRepository<Certificado,Integer> {
    @Query(nativeQuery = true, value = "SELECT * FROM certificados WHERE id_firmante = ?1")
    Optional<Certificado> findCertificadoByIDFirmante(String dniFirmante);
}
