package com.example.firmadorservice.Repository;

import com.example.firmadorservice.Entity.RegistroFirma;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RegistroFirmaRepository extends JpaRepository<RegistroFirma, Integer> {

}
