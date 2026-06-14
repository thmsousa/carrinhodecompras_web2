package com.example.atv3_associacoes.controller;

import com.example.atv3_associacoes.model.entity.*;
import com.example.atv3_associacoes.model.repository.PessoaRepository;
import com.example.atv3_associacoes.model.repository.RoleRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/cadastro")
public class CadastroController {

    @Autowired
    private PessoaRepository pessoaRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // ===== Pessoa Física =====

    @GetMapping("/pf")
    public String formPf(PessoaFisica pessoaFisica) {
        // Inicializa a lista com uma instância vazia para evitar erro de índice na tela do Thymeleaf
        if (pessoaFisica.getEnderecos() == null) {
            pessoaFisica.setEnderecos(new ArrayList<>());
        }
        if (pessoaFisica.getEnderecos().isEmpty()) {
            pessoaFisica.getEnderecos().add(new Endereco());
        }
        return "cadastro/form-pf";
    }

    @Transactional
    @PostMapping("/save-pf")
    public String savePf(@Valid PessoaFisica pessoa, BindingResult result,
                         @RequestParam("loginUsuario") String login,
                         @RequestParam("senhaUsuario") String senha,
                         RedirectAttributes attr) {
        if (result.hasErrors()) return "cadastro/form-pf";

        Usuario usuario = criarUsuario(login, senha);
        pessoa.setUsuario(usuario);

        // Vincula o endereço recebido de volta à pessoa (preenche o pessoa_id no banco)
        if (pessoa.getEnderecos() != null && !pessoa.getEnderecos().isEmpty()) {
            pessoa.getEnderecos().get(0).setCliente(pessoa);
        }

        pessoaRepository.save(pessoa);

        attr.addFlashAttribute("mensagemSucesso", "Cadastro realizado com sucesso! Faça login.");
        return "redirect:/login";
    }

    // ===== Pessoa Jurídica =====

    @GetMapping("/pj")
    public String formPj(PessoaJuridica pessoaJuridica) {
        // Inicializa a lista com uma instância vazia para evitar erro de índice na tela do Thymeleaf
        if (pessoaJuridica.getEnderecos() == null) {
            pessoaJuridica.setEnderecos(new ArrayList<>());
        }
        if (pessoaJuridica.getEnderecos().isEmpty()) {
            pessoaJuridica.getEnderecos().add(new Endereco());
        }
        return "cadastro/form-pj";
    }

    @Transactional
    @PostMapping("/save-pj")
    public String savePj(@Valid PessoaJuridica pessoa, BindingResult result,
                         @RequestParam("loginUsuario") String login,
                         @RequestParam("senhaUsuario") String senha,
                         RedirectAttributes attr) {
        if (result.hasErrors()) return "cadastro/form-pj";

        Usuario usuario = criarUsuario(login, senha);
        pessoa.setUsuario(usuario);

        // Vincula o endereço recebido de volta à pessoa (preenche o pessoa_id no banco)
        if (pessoa.getEnderecos() != null && !pessoa.getEnderecos().isEmpty()) {
            pessoa.getEnderecos().get(0).setCliente(pessoa);
        }

        pessoaRepository.save(pessoa);

        attr.addFlashAttribute("mensagemSucesso", "Cadastro realizado com sucesso! Faça login.");
        return "redirect:/login";
    }

    // metodo para criar e configurar o usuário
    private Usuario criarUsuario(String login, String senha) {
        Usuario usuario = new Usuario();
        usuario.setUsuario(login);
        usuario.setSenha(passwordEncoder.encode(senha));

        Role roleUser = roleRepository.findByNome("ROLE_USER");
        List<Role> roles = new ArrayList<>();
        roles.add(roleUser);
        usuario.setRoles(roles);

        return usuario;
    }
}