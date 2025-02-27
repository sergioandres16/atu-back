package com.example.firmadorservice.Repository;

import com.example.firmadorservice.Entity.Firmante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FirmanteRepository extends JpaRepository<Firmante,String> {

    @Query(nativeQuery = true, value = "SELECT * FROM firmantes WHERE dni = ?1 AND estado = 1")
    Optional<Firmante> findFirmante(String dni);

}
