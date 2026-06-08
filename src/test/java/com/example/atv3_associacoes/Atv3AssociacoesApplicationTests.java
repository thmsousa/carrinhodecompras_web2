package com.example.atv3_associacoes;

import com.example.atv3_associacoes.model.entity.Usuario;
import com.example.atv3_associacoes.model.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class Atv3AssociacoesApplicationTests {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void contextLoads() {
    }

    @Test
    void testAdminLogin() {
        Usuario admin = usuarioRepository.findByUsuario("admin");
        assertNotNull(admin, "O usuário 'admin' deveria existir no banco.");
        assertTrue(passwordEncoder.matches("admin", admin.getSenha()),
                "A senha 'admin' deveria corresponder ao hash do banco.");
        assertFalse(admin.getAuthorities().isEmpty(),
                "O admin deveria ter papéis atribuídos.");
        System.out.println(">>> Admin OK - Authorities: " + admin.getAuthorities());
    }

    @Test
    void testThiagoLogin() {
        Usuario thiago = usuarioRepository.findByUsuario("thiago");
        assertNotNull(thiago, "O usuário 'thiago' deveria existir no banco.");
        assertTrue(passwordEncoder.matches("123", thiago.getSenha()),
                "A senha '123' deveria corresponder ao hash do banco.");
        System.out.println(">>> Thiago OK - Authorities: " + thiago.getAuthorities());
    }
}
