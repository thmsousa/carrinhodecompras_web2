package com.example.atv3_associacoes.model.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.br.CNPJ;

@Entity
@DiscriminatorValue("PJ")
public class PessoaJuridica extends Pessoa {
    @NotBlank(message = "A Razão Social é obrigatória.")
    private String razaoSocial;

    @NotBlank(message = "O CNPJ é obrigatório.")
    @CNPJ(message = "CNPJ em formato inválido.") // Valida se o CNPJ é real
    private String cnpj;

    public String getRazaoSocial() {
        return razaoSocial;
    }

    public void setRazaoSocial(String razaoSocial) {
        this.razaoSocial = razaoSocial;
    }

    public String getCnpj() {
        return cnpj;
    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }

    @Override
    public String getNomeExibicao() {
        return razaoSocial;
    }
}