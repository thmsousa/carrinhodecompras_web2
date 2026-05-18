package com.example.atv3_associacoes.controller;

import com.example.atv3_associacoes.model.entity.Pessoa;
import com.example.atv3_associacoes.model.entity.PessoaFisica;
import com.example.atv3_associacoes.model.entity.PessoaJuridica;
import com.example.atv3_associacoes.model.repository.PessoaRepository;
import com.example.atv3_associacoes.model.repository.VendaRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
        List<Pessoa> clientes;
        if (nome != null && !nome.trim().isEmpty()) {
            clientes = repository.findByNome(nome);
        } else {
            clientes = repository.findAll(); // Se não tem filtro, TEM que trazer tudo
        }
        model.addAttribute("clientes", clientes);
        return "clientes/list";
    }

    // Consulta de Vendas por Cliente
    @GetMapping("/{id}/vendas")
    public String vendasPorCliente(@PathVariable Long id, Model model) {
        model.addAttribute("vendas", vendaRepository.findByCliente(id, null));
        return "vendas/list"; // Reutiliza a página de listagem de vendas
    }

    @GetMapping("/form-pf")
    public String formPf(PessoaFisica pessoaFisica) {
        return "clientes/form-pf";
    }

    @PostMapping("/save-pf")
    public String savePf(@Valid PessoaFisica pessoa, BindingResult result, RedirectAttributes attr) {
        if (result.hasErrors()) {
            return "clientes/form-pf";
        }
        repository.save(pessoa);
        attr.addFlashAttribute("mensagemSucesso", "Cliente PF salvo!");
        return "redirect:/clientes/lista";
    }

    @GetMapping("/form-pj")
    public String formPj(PessoaJuridica pessoaJuridica) {
        return "clientes/form-pj";
    }

    @PostMapping("/save-pj")
    public String savePj(@Valid PessoaJuridica pessoa, BindingResult result, RedirectAttributes attr) {
        if (result.hasErrors()) {
            return "clientes/form-pj";
        }
        repository.save(pessoa);
        attr.addFlashAttribute("mensagemSucesso", "Cliente Jurídico salvo com sucesso!");
        return "redirect:/clientes/lista";
    }


}