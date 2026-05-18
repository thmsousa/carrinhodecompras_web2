package com.example.atv3_associacoes.model.repository;

import com.example.atv3_associacoes.model.entity.Venda;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public class VendaRepository {
    @PersistenceContext
    private EntityManager em;

    public List<Venda> findAll() {
        return em.createQuery("from Venda", Venda.class).getResultList();
    }

    public Venda findById(Long id) {
        return em.find(Venda.class, id);
    }

    public List<Venda> findByData(LocalDateTime inicio, LocalDateTime fim) {
        return em.createQuery("from Venda v where v.data between :inicio and :fim", Venda.class)
                .setParameter("inicio", inicio)
                .setParameter("fim", fim)
                .getResultList();
    }

    public List<Venda> findByCliente(Long clienteId, LocalDateTime data) {
        String hql = "from Venda v where v.cliente.id = :cId";
        var query = em.createQuery(hql, Venda.class).setParameter("cId", clienteId);
        return query.getResultList();
    }

    @Transactional
    public void save(Venda venda) {
        if (venda.getId() == null) {
            em.persist(venda);
        } else {
            em.merge(venda);
        }
    }
}