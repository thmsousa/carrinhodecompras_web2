package com.example.atv3_associacoes.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.List;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tipo_pessoa")
public abstract class Pessoa implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "O e-mail é obrigatório.")
    @Email(message = "Informe um e-mail válido.")
    private String email;

    @NotBlank(message = "O telefone é obrigatório.")
    private String telefone;

    @OneToMany(mappedBy = "cliente")
    private List<Venda> vendas;

    // Adicione esse atributo dentro da classe abstrata Pessoa
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    // Retorna se é Física ou Jurídica baseando-se na instância real
    public String getTipoDocumentoLabel() {
        if (this instanceof PessoaFisica || this.getClass().getSimpleName().contains("PessoaFisica")) {
            return "PF";
        }
        return "PJ";
    }

    // Retorna o CPF ou o CNPJ dinamicamente
    public String getNumeroDocumento() {
        if (this instanceof PessoaFisica) {
            return "CPF: " + ((PessoaFisica) this).getCpf();
        } else if (this instanceof PessoaJuridica) {
            return "CNPJ: " + ((PessoaJuridica) this).getCnpj();
        }
        return "";
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public List<Venda> getVendas() {
        return vendas;
    }

    public void setVendas(List<Venda> vendas) {
        this.vendas = vendas;
    }
    public abstract String getNomeExibicao();
}