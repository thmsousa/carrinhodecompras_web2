package com.example.atv3_associacoes.controller;

import com.example.atv3_associacoes.model.entity.Pessoa;
import com.example.atv3_associacoes.model.entity.Venda;
import com.example.atv3_associacoes.model.repository.PessoaRepository;
import com.example.atv3_associacoes.model.repository.VendaRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Controller
@RequestMapping("/vendas")
public class VendaController {

    @Autowired
    private VendaRepository repository;

    @Autowired
    private PessoaRepository pessoaRepository;

    /**
     * Lista todas as vendas ou filtra por uma data específica.
     */
    @GetMapping("/lista")
    public String listar(@RequestParam(required = false) String dataFiltro, Model model) {
        List<Venda> vendas;

        if (dataFiltro != null && !dataFiltro.isEmpty()) {
            try {
                LocalDate dataSel = LocalDate.parse(dataFiltro);
                LocalDateTime inicio = dataSel.atStartOfDay();
                LocalDateTime fim = dataSel.atTime(LocalTime.MAX);
                vendas = repository.findByData(inicio, fim);
            } catch (Exception e) {
                vendas = repository.findAll();
            }
        } else {
            vendas = repository.findAll();
        }

        model.addAttribute("vendas", vendas);
        return "vendas/list";
    }

    /**
     * Exibe a página do carrinho de compras.
     */
    @GetMapping("/carrinho")
    public String verCarrinho(Model model, HttpSession session) {
        Venda venda = (Venda) session.getAttribute("venda_sessao");
        model.addAttribute("venda", venda);
        model.addAttribute("clientes", pessoaRepository.findAll());
        return "vendas/cart";
    }

    /**
     * Remove um item específico do carrinho baseado no índice da lista.
     */
    @PostMapping("/remover-item")
    public String removerItem(@RequestParam int index, HttpSession session) {
        Venda venda = (Venda) session.getAttribute("venda_sessao");
        if (venda != null && index < venda.getItens().size()) {
            venda.getItens().remove(index);
        }
        return "redirect:/vendas/carrinho";
    }

    /**
     * Finaliza a venda persistindo no banco de dados com validações robustas.
     */
    @PostMapping("/finalizar")
    @Transactional
    public String finalizar(@RequestParam(value = "cliente.id", required = false) Long clienteId,
                            HttpSession session,
                            RedirectAttributes redirectAttributes) {

        Venda vendaSessao = (Venda) session.getAttribute("venda_sessao");

        // VALIDAÇÃO 1: Carrinho vazio ou nulo
        if (vendaSessao == null || vendaSessao.getItens().isEmpty()) {
            redirectAttributes.addFlashAttribute("mensagemErro", "Não é possível finalizar uma venda sem itens no carrinho!");
            return "redirect:/vendas/carrinho";
        }

        // VALIDAÇÃO 2: Cliente não selecionado (clienteId virá null se nada for selecionado)
        if (clienteId == null) {
            redirectAttributes.addFlashAttribute("mensagemErro", "Você deve selecionar um cliente para finalizar a compra.");
            return "redirect:/vendas/carrinho";
        }

        try {
            Pessoa cliente = pessoaRepository.findById(clienteId);

            if (cliente == null) {
                redirectAttributes.addFlashAttribute("mensagemErro", "O cliente selecionado não foi encontrado ou é inválido.");
                return "redirect:/vendas/carrinho";
            }

            // Configura os dados finais da venda
            vendaSessao.setCliente(cliente);
            vendaSessao.setData(LocalDateTime.now());

            // Persiste a venda e seus itens no banco
            repository.save(vendaSessao);

            // Limpa o carrinho da sessão após o sucesso
            session.removeAttribute("venda_sessao");

            // Feedback de sucesso para a página de listagem
            redirectAttributes.addFlashAttribute("mensagemSucesso", "Venda finalizada com sucesso!");
            return "redirect:/vendas/lista";

        } catch (Exception e) {
            // Captura falhas de persistência ou integridade
            redirectAttributes.addFlashAttribute("mensagemErro", "Erro ao processar a venda no servidor: " + e.getMessage());
            return "redirect:/vendas/carrinho";
        }
    }

    /**
     * Exibe os detalhes de uma venda específica que ja foi feita.
     */
    @GetMapping("/detalhes/{id}")
    public String detalhes(@PathVariable Long id, Model model) {
        Venda venda = repository.findById(id);
        if (venda == null) {
            return "redirect:/vendas/lista";
        }
        model.addAttribute("venda", venda);
        return "vendas/detail";
    }

    /**
     * Atualiza a quantidade de um item no carrinho com validação de valor positivo*/
    @PostMapping("/atualizar-quantidade")
    public String atualizarQuantidade(@RequestParam int index,
                                      @RequestParam Double novaQuantidade,
                                      HttpSession session,
                                      RedirectAttributes redirectAttributes) {
        Venda venda = (Venda) session.getAttribute("venda_sessao");

        if (venda != null && index < venda.getItens().size()) {
            if (novaQuantidade != null && novaQuantidade > 0) {
                venda.getItens().get(index).setQuantidade(novaQuantidade);
            } else {
                // Se o usuário colocar 0 ou negativo o item é removido
                venda.getItens().remove(index);
                redirectAttributes.addFlashAttribute("mensagemSucesso", "Item removido do carrinho.");
            }
        }
        return "redirect:/vendas/carrinho";
    }
}