package com.center.manager.service.impl;

import com.center.manager.db.TransactionManager;
import com.center.manager.model.Payment;
import com.center.manager.repo.PaymentRepository;
import com.center.manager.service.PaymentService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Function;

/**
 * Implement PaymentService.
 */
public class PaymentServiceImpl implements PaymentService {
    private final PaymentRepository paymentRepo;
    private final TransactionManager tx;

    // Mapper ket qua query payment, chuan hoa cot thoi gian nullable de tranh null tren JTable.
    private final Function<Object[], Object[]> paymentRowMapper =
            r -> new Object[]{r[0], r[1], r[2], r[3] != null ? r[3].toString() : "", r[4], r[5], r[6]};

    public PaymentServiceImpl(PaymentRepository paymentRepo, TransactionManager tx) {
        this.paymentRepo = paymentRepo;
        this.tx = tx;
    }

    @Override
    public List<Object[]> getByStudent(Long studentId) throws Exception {
        return tx.runInTransaction(em -> paymentRepo.findByStudent(em, studentId).stream().map(paymentRowMapper).toList());
    }

    @Override
    public List<Object[]> getAll() throws Exception {
        return tx.runInTransaction(em -> paymentRepo.findAll(em).stream().map(paymentRowMapper).toList());
    }

    /**
     * Tao payment va gan thoi diem hien tai, trang thai mac dinh Completed.
     */
    @Override
    public void createPayment(Long studentId, Long enrollmentId, BigDecimal amount,
                              String paymentMethod, String referenceCode) throws Exception {
        if (studentId == null) {
            throw new IllegalArgumentException("Vui long chon hoc vien.");
        }
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("So tien phai lon hon 0.");
        }

        tx.runInTransaction(em -> {
            // Lambda transaction nay tao giao dich moi va persist trong 1 don vi xu ly.
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

