package com.example.firmadorservice.Security;

import com.example.firmadorservice.Entity.Usuario;
import com.example.firmadorservice.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailServiceImpl implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException{
        Usuario usuario= userRepository.findByUsuario(username).orElseThrow(() -> new UsernameNotFoundException("El usuario con username "+username+" no existe."));
        return new UserDetailsImpl(usuario);
    }
}
