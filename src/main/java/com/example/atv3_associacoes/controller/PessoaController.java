package com.example.atv3_associacoes.controller;

import com.example.atv3_associacoes.model.entity.Pessoa;
import com.example.atv3_associacoes.model.repository.PessoaRepository;
import com.example.atv3_associacoes.model.repository.VendaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
}