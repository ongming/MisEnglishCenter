package com.center.manager.service;

import com.center.manager.db.TransactionManager;
import com.center.manager.repo.jpa.*;
import com.center.manager.service.impl.*;

/**
 * Factory tao cac Service - UI goi ServiceFactory.xxx() de lay service.
 * Giu UI don gian, khong can inject qua constructor.
 */
public final class ServiceFactory {

    private static final TransactionManager TX = new TransactionManager();

    private static final AuthService AUTH_SERVICE =
            new AuthServiceImpl(new JpaAuthRepository(), TX);

    private static final PersonService PERSON_SERVICE =
            new PersonServiceImpl(new JpaTeacherRepository(), new JpaStudentRepository(), TX);

    private static final ClassService CLASS_SERVICE =
            new ClassServiceImpl(new JpaClassRepository(), new JpaScheduleRepository(), TX);

    private static final AttendanceService ATTENDANCE_SERVICE =
            new AttendanceServiceImpl(new JpaAttendanceRepository(), TX);

    private static final AdminService ADMIN_SERVICE =
            new AdminServiceImpl(new JpaAdminRepository(), TX);

    private static final PaymentService PAYMENT_SERVICE =
            new PaymentServiceImpl(new JpaPaymentRepository(), TX);

    private ServiceFactory() {
    }

    public static AuthService authService() {
        return AUTH_SERVICE;
    }

    public static PersonService personService() {
        return PERSON_SERVICE;
    }

    public static ClassService classService() {
        return CLASS_SERVICE;
    }

    public static AttendanceService attendanceService() {
        return ATTENDANCE_SERVICE;
    }

    public static AdminService adminService() {
        return ADMIN_SERVICE;
    }

    public static PaymentService paymentService() {
        return PAYMENT_SERVICE;
    }
}
