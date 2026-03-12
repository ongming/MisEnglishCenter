package com.center.manager.repo.jpa;

import com.center.manager.model.Payment;
import com.center.manager.repo.PaymentRepository;
import jakarta.persistence.EntityManager;

import java.util.List;

public class JpaPaymentRepository implements PaymentRepository {

    @Override
    public List<Object[]> findByStudent(EntityManager em, Long studentId) {
        String jpql = """
                SELECT p.paymentId, s.fullName, p.amount, p.paymentDate,
                       p.paymentMethod, p.status, COALESCE(p.referenceCode, '')
                FROM Payment p
                JOIN Student s ON p.studentId = s.studentId
                WHERE p.studentId = :studentId
                ORDER BY p.paymentDate DESC
                """;
        return em.createQuery(jpql, Object[].class)
                .setParameter("studentId", studentId)
                .getResultList();
    }

    @Override
    public List<Object[]> findAll(EntityManager em) {
        String jpql = """
                SELECT p.paymentId, s.fullName, p.amount, p.paymentDate,
                       p.paymentMethod, p.status, COALESCE(p.referenceCode, '')
                FROM Payment p
                JOIN Student s ON p.studentId = s.studentId
                ORDER BY p.paymentDate DESC
                """;
        return em.createQuery(jpql, Object[].class).getResultList();
    }

    @Override
    public void save(EntityManager em, Payment payment) {
        if (payment.getPaymentId() != null && payment.getPaymentId() > 0) {
            em.merge(payment);
        } else {
            em.persist(payment);
        }
    }

    @Override
    public Payment findById(EntityManager em, Long paymentId) {
        return em.find(Payment.class, paymentId);
    }
}
