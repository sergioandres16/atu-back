package com.example.firmadorservice.Repository;

import com.example.firmadorservice.Entity.FirmaDigital;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface FirmaDigitalRepository extends JpaRepository<FirmaDigital, Integer> {
}
