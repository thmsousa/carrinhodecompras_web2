package com.example.atv3_associacoes.controller;

import com.example.atv3_associacoes.model.entity.Endereco;
import com.example.atv3_associacoes.model.entity.Pessoa;
import com.example.atv3_associacoes.model.entity.PessoaFisica;
import com.example.atv3_associacoes.model.entity.PessoaJuridica;
import com.example.atv3_associacoes.model.repository.PessoaRepository;
import com.example.atv3_associacoes.model.repository.VendaRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/clientes")
public class PessoaController {

    @Autowired
    private PessoaRepository repository;
    @Autowired
    private VendaRepository vendaRepository;

    @GetMapping("/lista")
    public String listar(@RequestParam(required = false) String nome, Model model) {
        model.addAttribute("clientes", (nome != null && !nome.trim().isEmpty())
                ? repository.findByNome(nome) : repository.findAll());
        return "clientes/list";
    }

    @GetMapping("/{id}/vendas")
    public String vendasPorCliente(@PathVariable Long id, Model model) {
        model.addAttribute("vendas", vendaRepository.findByCliente(id, null));
        return "vendas/list";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/form-pf")
    public String formPf(PessoaFisica pessoaFisica) {
        // Inicializa a lista e adiciona um objeto vazio para o Thymeleaf mapear o índice 0 sem estourar erro
        if (pessoaFisica.getEnderecos() == null) {
            pessoaFisica.setEnderecos(new ArrayList<>());
        }
        if (pessoaFisica.getEnderecos().isEmpty()) {
            pessoaFisica.getEnderecos().add(new Endereco());
        }
        return "clientes/form-pf";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/save-pf")
    public String savePf(@Valid PessoaFisica pessoa, BindingResult result, RedirectAttributes attr) {
        if (result.hasErrors()) return "clientes/form-pf";

        // Garante a amarração bidirecional do endereço com a Pessoa (preenche a coluna pessoa_id no banco)
        if (pessoa.getEnderecos() != null && !pessoa.getEnderecos().isEmpty()) {
            pessoa.getEnderecos().get(0).setCliente(pessoa);
        }

        repository.save(pessoa);
        attr.addFlashAttribute("mensagemSucesso", "Cliente PF salvo!");
        return "redirect:/clientes/lista";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/form-pj")
    public String formPj(PessoaJuridica pessoaJuridica) {
        // Inicializa a lista e adiciona um objeto vazio para o Thymeleaf mapear o índice 0 sem estourar erro
        if (pessoaJuridica.getEnderecos() == null) {
            pessoaJuridica.setEnderecos(new ArrayList<>());
        }
        if (pessoaJuridica.getEnderecos().isEmpty()) {
            pessoaJuridica.getEnderecos().add(new Endereco());
        }
        return "clientes/form-pj";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/save-pj")
    public String savePj(@Valid PessoaJuridica pessoa, BindingResult result, RedirectAttributes attr) {
        if (result.hasErrors()) return "clientes/form-pj";

        // Garante a amarração bidirecional do endereço com a Pessoa (preenche a coluna pessoa_id no banco)
        if (pessoa.getEnderecos() != null && !pessoa.getEnderecos().isEmpty()) {
            pessoa.getEnderecos().get(0).setCliente(pessoa);
        }

        repository.save(pessoa);
        attr.addFlashAttribute("mensagemSucesso", "Cliente PJ salvo!");
        return "redirect:/clientes/lista";
    }
}