package com.center.manager.service;

import com.center.manager.db.TransactionManager;
import com.center.manager.model.Payment;
import com.center.manager.repo.PaymentRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Service cho thanh toán.
 */
public class PaymentService {
    private final PaymentRepository paymentRepo;
    private final TransactionManager tx;

    public PaymentService(PaymentRepository paymentRepo, TransactionManager tx) {
        this.paymentRepo = paymentRepo;
        this.tx = tx;
    }

    public List<Object[]> getByStudent(Long studentId) throws Exception {
        return tx.runInTransaction(em ->
                paymentRepo.findByStudent(em, studentId).stream()
                        .map(this::mapPaymentRow)
                        .toList());
    }

    public List<Object[]> getAll() throws Exception {
        return tx.runInTransaction(em ->
                paymentRepo.findAll(em).stream()
                        .map(this::mapPaymentRow)
                        .toList());
    }

    private Object[] mapPaymentRow(Object[] r) {
        return new Object[]{r[0], r[1], r[2], r[3] != null ? r[3].toString() : "", r[4], r[5], r[6]};
    }

    public void createPayment(Long studentId, Long enrollmentId, BigDecimal amount,
                              String paymentMethod, String referenceCode) throws Exception {
        if (studentId == null) {
            throw new IllegalArgumentException("Vui lòng chọn học viên.");
        }
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Số tiền phải lớn hơn 0.");
        }

        tx.runInTransaction(em -> {
            Payment payment = new Payment();
            payment.setStudentId(studentId);
            payment.setEnrollmentId(enrollmentId);
            payment.setAmount(amount);
            payment.setPaymentDate(LocalDateTime.now());
            payment.setPaymentMethod(paymentMethod != null ? paymentMethod : "Cash");
            payment.setStatus("Completed");
            payment.setReferenceCode(referenceCode);
            paymentRepo.save(em, payment);
            return null;
        });
    }
}
