package com.center.manager.service;

import java.math.BigDecimal;
import java.util.List;

/**
 * Service cho thanh toan.
 */
public interface PaymentService {

    List<Object[]> getByStudent(Long studentId) throws Exception;

    List<Object[]> getAll() throws Exception;

    /** Tao giao dich thanh toan moi cho hoc vien. */
    void createPayment(Long studentId, Long enrollmentId, BigDecimal amount,
                       String paymentMethod, String referenceCode) throws Exception;
}
