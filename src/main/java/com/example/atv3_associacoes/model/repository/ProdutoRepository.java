package com.example.atv3_associacoes.model.repository;

import com.example.atv3_associacoes.model.entity.Produto;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ProdutoRepository {
    @PersistenceContext
    private EntityManager em;

    public List<Produto> findAll() {
        return em.createQuery("from Produto", Produto.class).getResultList();
    }

    public List<Produto> findByDescricao(String termo) {
        return em.createQuery("from Produto p where p.descricao like :t", Produto.class)
                .setParameter("t", "%" + termo + "%")
                .getResultList();
    }

    public Produto findById(Long id) {
        return em.find(Produto.class, id);
    }
}