package com.inmobiliaria.inmobiliariabackend.service;
import com.inmobiliaria.inmobiliariabackend.model.Usuario;
import com.inmobiliaria.inmobiliariabackend.model.UsuarioRol;
import com.inmobiliaria.inmobiliariabackend.repository.UsuarioRepository;
import com.inmobiliaria.inmobiliariabackend.repository.UsuarioRolRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private UsuarioRolRepository usuarioRolRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario user = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Credenciales no válidas"));

        if (!user.getActivo() || user.getFechaEliminacion() != null) {
            throw new UsernameNotFoundException("Credenciales no válidas");
        }

        Set<GrantedAuthority> authorities = user.getRoles().stream()
                .map(UsuarioRol::getNombre)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toSet());

        return new User(user.getUsername(), user.getPassword(), authorities);
    }
}