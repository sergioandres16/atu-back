package com.example.firmadorservice.Repository;

import com.example.firmadorservice.Entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<Usuario, Integer> {
    @Query(nativeQuery = true, value = "SELECT * FROM usuario WHERE us_firmante = ?1 AND us_estado = 1")
    Optional<Usuario> findByUsuario(String dniFirmante);

    @Query(nativeQuery = true, value = "SELECT * FROM usuario WHERE us_uuid = ?1 AND us_estado = 1")
    Optional<Usuario> findByUsuarioByUUID(String uuidFirmante);
}
