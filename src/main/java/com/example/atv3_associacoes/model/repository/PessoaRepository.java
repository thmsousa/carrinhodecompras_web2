package com.example.atv3_associacoes.model.repository;

import com.example.atv3_associacoes.model.entity.Pessoa;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional; // Adicionado
import java.util.List;

@Repository
public class PessoaRepository {

    @PersistenceContext
    private EntityManager em;

    @Transactional // Necessário para operações de escrita
    public void save(Pessoa pessoa) {
        if (pessoa.getId() == null) {
            em.persist(pessoa);
        } else {
            em.merge(pessoa);
        }
    }

    public List<Pessoa> findAll() {
        return em.createQuery("SELECT p FROM Pessoa p", Pessoa.class).getResultList();
    }

    public Pessoa findById(Long id) {
        return em.find(Pessoa.class, id);
    }

    public List<Pessoa> findByNome(String termo) {
        String hql = "from Pessoa p where " +
                "(treat(p as PessoaFisica).nome like :t) or " +
                "(treat(p as PessoaJuridica).razaoSocial like :t)";

        return em.createQuery(hql, Pessoa.class)
                .setParameter("t", "%" + termo + "%")
                .getResultList();
    }
}