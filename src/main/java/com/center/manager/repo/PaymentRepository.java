package com.center.manager.repo;

import com.center.manager.model.Payment;
import jakarta.persistence.EntityManager;

import java.util.List;

public interface PaymentRepository {
    List<Object[]> findByStudent(EntityManager em, Long studentId);
    List<Object[]> findAll(EntityManager em);
    void save(EntityManager em, Payment payment);
    Payment findById(EntityManager em, Long paymentId);
}
