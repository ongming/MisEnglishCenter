package com.center.manager.service;

import com.center.manager.db.TransactionManager;
import com.center.manager.repo.jpa.JpaAdminRepository;
import com.center.manager.repo.jpa.JpaAttendanceRepository;
import com.center.manager.repo.jpa.JpaAuthRepository;
import com.center.manager.repo.jpa.JpaClassRepository;
import com.center.manager.repo.jpa.JpaCourseRepository;
import com.center.manager.repo.jpa.JpaPaymentRepository;
import com.center.manager.repo.jpa.JpaRoomRepository;
import com.center.manager.repo.jpa.JpaScheduleRepository;
import com.center.manager.repo.jpa.JpaStudentRepository;
import com.center.manager.repo.jpa.JpaTeacherRepository;
import com.center.manager.service.impl.AdminServiceImpl;
import com.center.manager.service.impl.AttendanceServiceImpl;
import com.center.manager.service.impl.AuthServiceImpl;
import com.center.manager.service.impl.ClassServiceImpl;
import com.center.manager.service.impl.CourseServiceImpl;
import com.center.manager.service.impl.PaymentServiceImpl;
import com.center.manager.service.impl.PersonServiceImpl;
import com.center.manager.service.impl.RoomServiceImpl;

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

    private static final CourseService COURSE_SERVICE =
            new CourseServiceImpl(new JpaCourseRepository(), TX);

    private static final RoomService ROOM_SERVICE =
            new RoomServiceImpl(new JpaRoomRepository(), TX);

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

    public static CourseService courseService() {
        return COURSE_SERVICE;
    }

    public static RoomService roomService() {
        return ROOM_SERVICE;
    }
}
