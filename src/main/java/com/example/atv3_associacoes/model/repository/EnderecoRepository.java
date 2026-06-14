package com.example.atv3_associacoes.model.repository;

import com.example.atv3_associacoes.model.entity.Endereco;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Repository
public class EnderecoRepository {

    @PersistenceContext
    private EntityManager em;

    @Transactional
    public void save(Endereco endereco) {
        if (endereco.getId() == null) {
            em.persist(endereco);
        } else {
            em.merge(endereco);
        }
    }

    public List<Endereco> findByCliente(Long clienteId) {
        return em.createQuery("SELECT e FROM Endereco e WHERE e.cliente.id = :cId", Endereco.class)
                .setParameter("cId", clienteId)
                .getResultList();
    }

    public Endereco findById(Long id) {
        return em.find(Endereco.class, id);
    }
}