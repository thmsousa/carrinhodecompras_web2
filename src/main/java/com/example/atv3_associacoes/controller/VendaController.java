package com.example.atv3_associacoes.controller;

import com.example.atv3_associacoes.model.entity.Endereco;
import com.example.atv3_associacoes.model.entity.Pessoa;
import com.example.atv3_associacoes.model.entity.Usuario;
import com.example.atv3_associacoes.model.entity.Venda;
import com.example.atv3_associacoes.model.repository.PessoaRepository;
import com.example.atv3_associacoes.model.repository.VendaRepository;
import com.example.atv3_associacoes.model.repository.UsuarioRepository;
import com.example.atv3_associacoes.model.repository.EnderecoRepository;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
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

    @Autowired
    private EnderecoRepository enderecoRepository;

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

        boolean isAdmin = authentication != null && authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

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
                    vendas = List.of();
                }
            } else {
                vendas = List.of();
            }
        }

        model.addAttribute("vendas", vendas);
        return "vendas/list";
    }

    /**
     * Exibe a página do carrinho de compras e injeta os endereços rotulados da Pessoa logada.
     */
    @GetMapping("/carrinho")
    public String verCarrinho(Model model, HttpSession session, Authentication authentication, Principal principal) {
        Venda venda = (Venda) session.getAttribute("venda_sessao");
        model.addAttribute("venda", venda);

        boolean isAdmin = authentication != null && authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        model.addAttribute("isAdmin", isAdmin);

        if (principal != null) {
            Usuario usuarioLogado = usuarioRepository.findByUsuario(principal.getName());
            if (usuarioLogado != null) {
                try {
                    List<Pessoa> resultado = pessoaRepository.findAll();
                    Pessoa clienteExclusivo = resultado.stream()
                            .filter(p -> p.getUsuario() != null && p.getUsuario().getId().equals(usuarioLogado.getId()))
                            .findFirst().orElse(null);

                    model.addAttribute("clienteExclusivo", clienteExclusivo);

                    if (clienteExclusivo != null) {
                        model.addAttribute("enderecos", clienteExclusivo.getEnderecos());
                    }
                } catch (Exception e) {
                    model.addAttribute("clienteExclusivo", null);
                }
            }
        }
        return "vendas/cart";
    }

    /**
     * Exibe a tela de cadastro de novo endereço integrado ao fluxo da venda
     */
    @GetMapping("/novo-endereco")
    public String exibirFormEndereco(Endereco endereco) {
        return "vendas/form-endereco";
    }

    /**
     * Recebe os dados do formulário tradicional e redireciona de volta para o carrinho
     */
    @PostMapping("/salvar-endereco")
    @Transactional
    public String salvarEnderecoTradicional(@Valid Endereco endereco, BindingResult result, Principal principal, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "vendas/form-endereco";
        }

        if (principal == null) {
            return "redirect:/login";
        }

        Usuario usuarioLogado = usuarioRepository.findByUsuario(principal.getName());
        List<Pessoa> resultado = pessoaRepository.findAll();

        Pessoa cliente = resultado.stream()
                .filter(p -> p.getUsuario() != null && p.getUsuario().getId().equals(usuarioLogado.getId()))
                .findFirst().orElse(null);

        // TRATAMENTO: Se for o admin e ele não tiver um vínculo de Pessoa criado no import.sql ainda
        if (cliente == null) {
            redirectAttributes.addFlashAttribute("mensagemErro",
                    "Não foi possível cadastrar o endereço. Seu usuário atual (" + principal.getName() + ") não possui um perfil de Cliente associado no banco de dados.");
            return "redirect:/vendas/carrinho";
        }

        endereco.setCliente(cliente);
        enderecoRepository.save(endereco);

        redirectAttributes.addFlashAttribute("mensagemSucesso", "Novo endereço cadastrado com sucesso!");
        return "redirect:/vendas/carrinho";
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
     * Finaliza a venda forçando o vínculo com a sessão e validando o endereço e pagamento.
     */
    @PostMapping("/finalizar")
    @Transactional
    public String finalizar(@RequestParam("enderecoId") Long enderecoId,
                            @RequestParam("formaPagamento") String formaPagamento,
                            HttpSession session,
                            Principal principal,
                            RedirectAttributes redirectAttributes) {

        Venda vendaSessao = (Venda) session.getAttribute("venda_sessao");

        if (vendaSessao == null || vendaSessao.getItens().isEmpty()) {
            redirectAttributes.addFlashAttribute("mensagemErro", "Não é possível finalizar uma venda sem itens no carrinho!");
            return "redirect:/vendas/carrinho";
        }

        Pessoa cliente = null;

        if (principal != null) {
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

            final Long targetId = enderecoId;
            Endereco endereco = cliente.getEnderecos().stream()
                    .filter(e -> e.getId().equals(targetId))
                    .findFirst()
                    .orElse(null);

            if (endereco == null) {
                redirectAttributes.addFlashAttribute("mensagemErro", "O endereço de entrega selecionado é inválido.");
                return "redirect:/vendas/carrinho";
            }

            vendaSessao.setCliente(cliente);
            vendaSessao.setEnderecoEntrega(endereco);
            vendaSessao.setFormaPagamento(formaPagamento);
            vendaSessao.setData(LocalDateTime.now());

            if (vendaSessao.getItens() != null) {
                vendaSessao.getItens().forEach(item -> item.setVenda(vendaSessao));
            }

            repository.save(vendaSessao);
            session.removeAttribute("venda_sessao");
            redirectAttributes.addFlashAttribute("mensagemSucesso", "Venda finalizada com sucesso!");

            return "redirect:/vendas/lista";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensagemErro", "Erro ao processar a venda no servidor: " + e.getMessage());
            return "redirect:/vendas/carrinho";
        }
    }

    /**
     * Exibe os detalhes de uma venda específica que ja foi feita.
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

        if (!isAdmin && principal != null) {
            Usuario usuarioLogado = usuarioRepository.findByUsuario(principal.getName());
            List<Pessoa> todasPessoas = pessoaRepository.findAll();
            Pessoa clienteLogado = todasPessoas.stream()
                    .filter(p -> p.getUsuario() != null && p.getUsuario().getId().equals(usuarioLogado.getId()))
                    .findFirst().orElse(null);

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
                venda.getItens().remove(index);
                redirectAttributes.addFlashAttribute("mensagemSucesso", "Item removido do carrinho.");
            }
        }
        return "redirect:/vendas/carrinho";
    }
}