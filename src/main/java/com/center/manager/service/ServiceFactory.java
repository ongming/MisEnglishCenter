package com.center.manager.service;

import com.center.manager.db.TransactionManager;
import com.center.manager.repo.jpa.*;
import com.center.manager.db.JpaRoomRepository;

/**
 * Factory tạo các Service — UI gọi ServiceFactory.xxx() để lấy service.
 * Giữ UI đơn giản, không cần inject qua constructor.
 */
public final class ServiceFactory {

    private static final TransactionManager TX = new TransactionManager();

    private static final AuthService AUTH_SERVICE =
            new AuthService(new JpaAuthRepository(), TX);

    private static final PersonService PERSON_SERVICE =
            new PersonService(new JpaTeacherRepository(), new JpaStudentRepository(), TX);

    private static final ClassService CLASS_SERVICE =
            new ClassService(new JpaClassRepository(), new JpaScheduleRepository(), TX);

    private static final AttendanceService ATTENDANCE_SERVICE =
            new AttendanceService(new JpaAttendanceRepository(), TX);

    private static final AdminService ADMIN_SERVICE =
            new AdminService(new JpaAdminRepository(), TX, new JpaClassRepository());

    private static final PaymentService PAYMENT_SERVICE =
            new PaymentService(new JpaPaymentRepository(), TX);

    private static final CourseService COURSE_SERVICE =
            new CourseService(new com.center.manager.repo.jpa.JpaCourseRepository(), TX);

    private static final RoomService ROOM_SERVICE =
            new RoomService(new JpaRoomRepository(), TX);

    private ServiceFactory() {}

    public static AuthService authService() { return AUTH_SERVICE; }
    public static PersonService personService() { return PERSON_SERVICE; }
    public static ClassService classService() { return CLASS_SERVICE; }
    public static AttendanceService attendanceService() { return ATTENDANCE_SERVICE; }
    public static AdminService adminService() { return ADMIN_SERVICE; }
    public static PaymentService paymentService() { return PAYMENT_SERVICE; }
    public static CourseService courseService() { return COURSE_SERVICE; }
    public static RoomService roomService() { return ROOM_SERVICE; }
}
