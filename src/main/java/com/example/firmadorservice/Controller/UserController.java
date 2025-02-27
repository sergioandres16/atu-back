package com.example.firmadorservice.Controller;

import com.example.firmadorservice.Entity.Usuario;
import com.example.firmadorservice.Service.UserService;
import com.example.firmadorservice.Utils.Email;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.UUID;

@RestController
@CrossOrigin("*")
public class UserController {
    @Autowired
    UserService userService;

    @GetMapping("/generateUUID")
    public ResponseEntity<HashMap<String, Object>> generarUUID(@RequestParam(name = "dni") String dni) throws IOException {
        HashMap<String, Object> response = new HashMap<>();

        if(dni.length() != 8){
            response.put("estado",false);
            response.put("msg", "DNI ingresado es inválido");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }else{
            Usuario usuario = userService.obtenerUsuarioLog(dni);
            if(usuario == null){
                response.put("estado",false);
                response.put("msg", "No existe el usuario con el DNI registrado");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }else{
                if(usuario.getFechaGeneracionUuid() == null && usuario.getUuid() == null){
                    String uuid = UUID.randomUUID().toString();
                    usuario.setUuid(uuid);
                    usuario.setFechaGeneracionUuid(Instant.now());
                    userService.guardarUsuario(usuario);
                    Email.sendEmail(usuario.getCorreo(), uuid);
                }else{
                    Duration duracion = Duration.between(usuario.getFechaGeneracionUuid(), Instant.now());
                    if(duracion.toMinutes() <= 5){
                        response.put("estado",false);
                        response.put("msg", "Aún existe un link de verificación activo");
                        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
                    }else{
                        String uuid = UUID.randomUUID().toString();
                        usuario.setUuid(uuid);
                        usuario.setFechaGeneracionUuid(Instant.now());
                        userService.guardarUsuario(usuario);
                        Email.sendEmail(usuario.getCorreo(), uuid);
                    }
                }
                response.put("estado", true);
                response.put("msg", "Envio de correo exitoso");
                return new ResponseEntity<>(response, HttpStatus.OK);
            }
        }
    }

    @GetMapping("/verify")
    public ResponseEntity<HashMap<String, Object>> verificarUUID(@RequestParam(name = "uuid") String uuid){
        HashMap<String, Object> response = new HashMap<>();

        Usuario usuario = userService.obtenerUsuarioByUUID(uuid);
        if(usuario == null){
            response.put("estado",false);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }else{
            Duration duracion = Duration.between(usuario.getFechaGeneracionUuid(), Instant.now());
            if(duracion.toMinutes() <= 5){
                response.put("estado", true);
                return new ResponseEntity<>(response, HttpStatus.OK);
            }
            response.put("estado",false);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/register")
    public ResponseEntity<HashMap<String, Object>> registro(@RequestParam(name = "PIN") String pin,
                                                            @RequestParam(name = "uuid") String uuid){
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        HashMap<String, Object> response = new HashMap<>();

        if(pin.length() != 6){
            response.put("estado",false);
            response.put("msg", "PIN ingresado es inválido");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }else{
            Usuario usuario = userService.obtenerUsuarioByUUID(uuid);
            if(usuario == null){
                response.put("estado",false);
                response.put("msg", "No existe el token de verificación");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }else{
                Duration duracion = Duration.between(usuario.getFechaGeneracionUuid(), Instant.now());
                if(duracion.toMinutes() <= 5){
                    usuario.setPinFirmante(passwordEncoder.encode(pin));
                    usuario.setUuid(null);
                    usuario.setFechaGeneracionUuid(null);
                    userService.guardarUsuario(usuario);
                    response.put("estado", true);
                    response.put("msg", "PIN registrado exitosamente");
                    return new ResponseEntity<>(response, HttpStatus.OK);
                }
                response.put("estado", false);
                response.put("msg", "Token de verificación expirado");
                return new ResponseEntity<>(response, HttpStatus.OK);
            }
        }
    }
}
