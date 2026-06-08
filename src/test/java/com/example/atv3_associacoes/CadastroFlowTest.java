package com.example.atv3_associacoes;

import com.example.atv3_associacoes.model.entity.*;
import com.example.atv3_associacoes.model.repository.PessoaRepository;
import com.example.atv3_associacoes.model.repository.RoleRepository;
import com.example.atv3_associacoes.model.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CadastroFlowTest {

    @Autowired
    private PessoaRepository pessoaRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    @Transactional
    void testCadastroELoginPF() {
        // 1. Simular o que o CadastroController faz
        System.out.println("=== INICIO DO TESTE DE CADASTRO ===");

        // Buscar a role
        Role roleUser = roleRepository.findByNome("ROLE_USER");
        System.out.println("Role encontrada: " + (roleUser != null ? roleUser.getNome() : "NULL!"));
        assertNotNull(roleUser, "ROLE_USER deveria existir no banco.");

        // Criar usuario
        Usuario usuario = new Usuario();
        usuario.setUsuario("novousuario");
        usuario.setSenha(passwordEncoder.encode("minhasenha"));
        List<Role> roles = new ArrayList<>();
        roles.add(roleUser);
        usuario.setRoles(roles);

        System.out.println("Usuario criado: " + usuario.getUsuario());
        System.out.println("Senha criptografada: " + usuario.getSenha());

        // Criar pessoa
        PessoaFisica pessoa = new PessoaFisica();
        pessoa.setNome("Teste Cadastro");
        pessoa.setCpf("529.982.247-25");
        pessoa.setEmail("teste@teste.com");
        pessoa.setTelefone("11999999999");
        pessoa.setUsuario(usuario);

        // Salvar
        pessoaRepository.save(pessoa);
        System.out.println("Pessoa salva com ID: " + pessoa.getId());
        System.out.println("Usuario salvo com ID: " + usuario.getId());

        // 2. Tentar buscar o usuario pelo login
        Usuario encontrado = usuarioRepository.findByUsuario("novousuario");
        System.out.println("Usuario encontrado no banco: " + (encontrado != null ? encontrado.getUsuario() : "NULL!"));
        assertNotNull(encontrado, "O usuario 'novousuario' deveria ter sido salvo no banco.");

        // 3. Verificar senha
        boolean senhaCorreta = passwordEncoder.matches("minhasenha", encontrado.getSenha());
        System.out.println("Senha corresponde? " + senhaCorreta);
        assertTrue(senhaCorreta, "A senha deveria corresponder.");

        // 4. Verificar roles
        System.out.println("Roles do usuario: " + encontrado.getRoles().size());
        assertFalse(encontrado.getAuthorities().isEmpty(), "O usuario deveria ter roles.");
        System.out.println("Authority: " + encontrado.getAuthorities().iterator().next().getAuthority());

        System.out.println("=== TESTE DE CADASTRO CONCLUIDO COM SUCESSO ===");
    }
}
