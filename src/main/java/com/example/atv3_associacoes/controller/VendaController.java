package com.example.atv3_associacoes.controller;

import com.example.atv3_associacoes.model.entity.Pessoa;
import com.example.atv3_associacoes.model.entity.Usuario;
import com.example.atv3_associacoes.model.entity.Venda;
import com.example.atv3_associacoes.model.repository.PessoaRepository;
import com.example.atv3_associacoes.model.repository.VendaRepository;
import com.example.atv3_associacoes.model.repository.UsuarioRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/vendas")
public class VendaController {

    @Autowired
    private VendaRepository repository;

    @Autowired
    private PessoaRepository pessoaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    /**
     * Lista todas as vendas ou filtra por uma data específica.
     * Protegido: ADMIN visualiza tudo; CLIENTE visualiza apenas suas próprias compras
     */
    @GetMapping("/lista")
    public String listar(@RequestParam(required = false) String dataFiltro,
                         Model model,
                         Authentication authentication,
                         Principal principal) {
        List<Venda> vendas;

        // 1. role do usuário logado
        boolean isAdmin = authentication != null && authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        // 2. roda a busca baseada nos filtros de data padrões do sistema
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

        // 3. SE NÃO FOR ADMIN: Intercepta e filtra a lista na memória para exibir apenas o histórico do cliente logado
        if (!isAdmin && principal != null) {
            Usuario usuarioLogado = usuarioRepository.findByUsuario(principal.getName());
            if (usuarioLogado != null) {
                List<Pessoa> todasPessoas = pessoaRepository.findAll();
                Pessoa clienteLogado = todasPessoas.stream()
                        .filter(p -> p.getUsuario() != null && p.getUsuario().getId().equals(usuarioLogado.getId()))
                        .findFirst().orElse(null);

                if (clienteLogado != null) {
                    final Long clienteId = clienteLogado.getId();
                    vendas = vendas.stream()
                            .filter(v -> v.getCliente() != null && v.getCliente().getId().equals(clienteId))
                            .collect(Collectors.toList());
                } else {
                    vendas = List.of(); // Se a conta não tiver pessoa vinculada, esvazia por segurança
                }
            } else {
                vendas = List.of();
            }
        }

        model.addAttribute("vendas", vendas);
        return "vendas/list";
    }

    /**
     * Exibe a página do carrinho de compras.
     */
    @GetMapping("/carrinho")
    public String verCarrinho(Model model, HttpSession session, Authentication authentication, Principal principal) {
        Venda venda = (Venda) session.getAttribute("venda_sessao");
        model.addAttribute("venda", venda);

        // Verifica se o usuário logado é ADMIN
        boolean isAdmin = authentication != null && authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        model.addAttribute("isAdmin", isAdmin);

        if (isAdmin) {
            model.addAttribute("clientes", pessoaRepository.findAll());
        } else if (principal != null) {
            // Busca o cliente logado para exibir no resumo do carrinho
            Usuario usuarioLogado = usuarioRepository.findByUsuario(principal.getName());
            if (usuarioLogado != null) {
                try {
                    // Executa a busca da pessoa vinculada diretamente por HQL via EntityManager
                    List<Pessoa> resultado = pessoaRepository.findAll();
                    Pessoa clienteExclusivo = resultado.stream()
                            .filter(p -> p.getUsuario() != null && p.getUsuario().getId().equals(usuarioLogado.getId()))
                            .findFirst().orElse(null);
                    model.addAttribute("clienteExclusivo", clienteExclusivo);
                } catch (Exception e) {
                    model.addAttribute("clienteExclusivo", null);
                }
            }
        }
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
                            Authentication authentication,
                            Principal principal,
                            RedirectAttributes redirectAttributes) {

        Venda vendaSessao = (Venda) session.getAttribute("venda_sessao");

        // VALIDAÇÃO 1: Carrinho vazio ou nulo
        if (vendaSessao == null || vendaSessao.getItens().isEmpty()) {
            redirectAttributes.addFlashAttribute("mensagemErro", "Não é possível finalizar uma venda sem itens no carrinho!");
            return "redirect:/vendas/carrinho";
        }

        Pessoa cliente = null;
        boolean isAdmin = authentication != null && authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (isAdmin) {
            // VALIDAÇÃO 2: Cliente não selecionado para o admin
            if (clienteId == null) {
                redirectAttributes.addFlashAttribute("mensagemErro", "Você deve selecionar um cliente para finalizar a compra.");
                return "redirect:/vendas/carrinho";
            }
            cliente = pessoaRepository.findById(clienteId);
        } else if (principal != null) {
            // CLIENTE COMUM: Ignora o ID da requisição e força o vínculo com a sua sessão segura
            Usuario usuarioLogado = usuarioRepository.findByUsuario(principal.getName());
            if (usuarioLogado != null) {
                List<Pessoa> resultado = pessoaRepository.findAll();
                cliente = resultado.stream()
                        .filter(p -> p.getUsuario() != null && p.getUsuario().getId().equals(usuarioLogado.getId()))
                        .findFirst().orElse(null);
            }
        }

        try {
            if (cliente == null) {
                redirectAttributes.addFlashAttribute("mensagemErro", "O cliente associado não foi encontrado ou é inválido.");
                return "redirect:/vendas/carrinho";
            }
            // Configura os dados finais da venda
            vendaSessao.setCliente(cliente);
            vendaSessao.setData(LocalDateTime.now());

            // Sincroniza o vínculo de cada item com a venda antes de salvar
            if (vendaSessao.getItens() != null) {
                vendaSessao.getItens().forEach(item -> item.setVenda(vendaSessao));
            }

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
     * Protegido: Bloqueia caso um cliente comum tente digitar na URL o ID de um pedido alheio.
     */
    @GetMapping("/detalhes/{id}")
    public String detalhes(@PathVariable Long id,
                           Model model,
                           Authentication authentication,
                           Principal principal,
                           RedirectAttributes redirectAttributes) {
        Venda venda = repository.findById(id);
        if (venda == null) {
            return "redirect:/vendas/lista";
        }

        boolean isAdmin = authentication != null && authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        // LÓGICA DE PROTEÇÃO DE ENDPOINT:
        if (!isAdmin && principal != null) {
            Usuario usuarioLogado = usuarioRepository.findByUsuario(principal.getName());
            List<Pessoa> todasPessoas = pessoaRepository.findAll();
            Pessoa clienteLogado = todasPessoas.stream()
                    .filter(p -> p.getUsuario() != null && p.getUsuario().getId().equals(usuarioLogado.getId()))
                    .findFirst().orElse(null);

            // Se o pedido não pertence ao cliente autenticado, corta o fluxo imediatamente
            if (venda.getCliente() == null || clienteLogado == null || !venda.getCliente().getId().equals(clienteLogado.getId())) {
                redirectAttributes.addFlashAttribute("mensagemErro", "Acesso negado! Você não tem permissão para visualizar este pedido.");
                return "redirect:/vendas/lista";
            }
        }

        model.addAttribute("venda", venda);
        return "vendas/detail";
    }

    /**
     * Atualiza a quantidade de um item no carrinho com validação de valor positivo
     */
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