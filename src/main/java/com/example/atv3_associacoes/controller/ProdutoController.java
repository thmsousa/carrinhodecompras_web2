package com.example.atv3_associacoes.controller;

import com.example.atv3_associacoes.model.entity.Item;
import com.example.atv3_associacoes.model.entity.Produto;
import com.example.atv3_associacoes.model.entity.Venda;
import com.example.atv3_associacoes.model.repository.ProdutoRepository;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;

@Controller
@RequestMapping("/produtos")
public class ProdutoController {

    @Autowired
    private ProdutoRepository repository;

    @GetMapping("/lista")
    public String listar(@RequestParam(required = false) String descricao, Model model) {
        model.addAttribute("produtos", (descricao != null && !descricao.isEmpty())
                ? repository.findByDescricao(descricao) : repository.findAll());
        return "produtos/list";
    }

    @PostMapping("/adicionar")
    public String adicionarAoCarrinho(Item item, HttpSession session, RedirectAttributes redirectAttributes) {
        Venda venda = (Venda) session.getAttribute("venda_sessao");
        if (venda == null) {
            venda = new Venda();
            venda.setItens(new ArrayList<>());
        }

        Produto p = repository.findById(item.getProduto().getId());
        item.setProduto(p);

        item.setVenda(venda);
        venda.getItens().add(item);
        session.setAttribute("venda_sessao", venda);

        redirectAttributes.addFlashAttribute("mensagem", p.getDescricao() + " adicionado ao carrinho com sucesso!");

        return "redirect:/produtos/lista";
    }

    @GetMapping("/form")
    public String form(Produto produto) {
        return "produtos/form";
    }

    @PostMapping("/save")
    public String save(@Valid Produto produto, BindingResult result, RedirectAttributes attr) {
        if (result.hasErrors()) {
            return "produtos/form";
        }
        repository.save(produto);
        attr.addFlashAttribute("mensagemSucesso", "Produto salvo com sucesso!");
        // O erro estava aqui: mude de /list para /lista
        return "redirect:/produtos/lista";
    }
}