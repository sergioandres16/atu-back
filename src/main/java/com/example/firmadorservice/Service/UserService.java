package com.example.firmadorservice.Service;

import com.example.firmadorservice.Entity.Firmante;
import com.example.firmadorservice.Entity.Usuario;
import com.example.firmadorservice.Repository.FirmanteRepository;
import com.example.firmadorservice.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    public boolean existeUsuarioLog(String username){
        Optional<Usuario> optionalUsuario = userRepository.findByUsuario(username);
        return optionalUsuario.isPresent();
    }

    public Usuario obtenerUsuarioLog(String username){
        Usuario usuario;
        Optional<Usuario> optionalUsuario = userRepository.findByUsuario(username);
        if(optionalUsuario.isPresent()){
            usuario = optionalUsuario.get();
        }else{
            usuario = null;
        }
        return usuario;
    }

    public Usuario obtenerUsuarioByUUID(String uuid){
        Usuario usuario;
        Optional<Usuario> optionalUsuario = userRepository.findByUsuarioByUUID(uuid);
        if(optionalUsuario.isPresent()){
            usuario = optionalUsuario.get();
        }else{
            usuario = null;
        }
        return usuario;
    }

    public void guardarUsuario(Usuario usuario){
        userRepository.save(usuario);
    }
}
