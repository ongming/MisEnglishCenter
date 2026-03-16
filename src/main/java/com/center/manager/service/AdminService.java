package com.center.manager.service;

import com.center.manager.db.TransactionManager;
import com.center.manager.model.ClassEntity;
import com.center.manager.model.Teacher;
import com.center.manager.model.UserAccount;
import com.center.manager.repo.AdminRepository;
import com.center.manager.repo.ClassRepository;
import org.mindrot.jbcrypt.BCrypt;

import java.time.LocalDate;
import java.util.List;

/**
 * Service xử lý nghiệp vụ Admin: quản lý giáo viên và tạo tài khoản.
 */
public class AdminService {

    private final AdminRepository adminRepo;
    private final TransactionManager tx;
    private final ClassRepository classRepo;

    public AdminService(AdminRepository adminRepo, TransactionManager tx, ClassRepository classRepo) {
        this.adminRepo = adminRepo;
        this.tx = tx;
        this.classRepo = classRepo;
    }

    public List<Object[]> getAllTeachers() throws Exception {
        return tx.runInTransaction(adminRepo::findAllTeachers);
    }

    public Long createTeacher(String fullName, String phone, String email, String specialty,
                               String hireDate, String status) throws Exception {
        String name = safeTrim(fullName);
        if (name.isEmpty()) {
            throw new IllegalArgumentException("Họ tên giáo viên không được để trống.");
        }

        return tx.runInTransaction(em -> {
            Teacher teacher = new Teacher();
            teacher.setFullName(name);
            teacher.setPhone(safeTrim(phone));
            teacher.setEmail(safeTrim(email));
            teacher.setSpecialty(safeTrim(specialty));
            String hireDateStr = safeTrim(hireDate);
            if (!hireDateStr.isEmpty()) {
                teacher.setHireDate(LocalDate.parse(hireDateStr));
            }
            teacher.setStatus(safeTrim(status).isEmpty() ? "Active" : safeTrim(status));

            adminRepo.saveTeacher(em, teacher);
            return teacher.getTeacherId();
        });
    }

    public List<Object[]> getTeachersWithoutAccount() throws Exception {
        return tx.runInTransaction(adminRepo::findTeachersWithoutAccount);
    }

    public List<Object[]> getStudentsWithoutAccount() throws Exception {
        return tx.runInTransaction(adminRepo::findStudentsWithoutAccount);
    }

    public void createTeacherAccount(Long teacherId, String username, String rawPassword) throws Exception {
        createAccount("Teacher", teacherId, null, username, rawPassword);
    }

    public void createStudentAccount(Long studentId, String username, String rawPassword) throws Exception {
        createAccount("Student", null, studentId, username, rawPassword);
    }

    private void createAccount(String role, Long teacherId, Long studentId, String username, String rawPassword) throws Exception {
        String user = safeTrim(username);
        String pass = rawPassword == null ? "" : rawPassword.trim();

        if (user.isEmpty()) {
            throw new IllegalArgumentException("Username không được để trống.");
        }
        if (pass.isEmpty()) {
            throw new IllegalArgumentException("Password không được để trống.");
        }

        tx.runInTransaction(em -> {
            if (adminRepo.existsUsername(em, user)) {
                throw new IllegalArgumentException("Username đã tồn tại.");
            }

            if ("Teacher".equals(role)) {
                if (teacherId == null) {
                    throw new IllegalArgumentException("Vui lòng chọn giáo viên.");
                }
                if (adminRepo.existsTeacherAccount(em, teacherId)) {
                    throw new IllegalArgumentException("Giáo viên này đã có tài khoản.");
                }
            }

            if ("Student".equals(role)) {
                if (studentId == null) {
                    throw new IllegalArgumentException("Vui lòng chọn học viên.");
                }
                if (adminRepo.existsStudentAccount(em, studentId)) {
                    throw new IllegalArgumentException("Học viên này đã có tài khoản.");
                }
            }

            UserAccount account = new UserAccount();
            account.setUsername(user);
            account.setPasswordHash(BCrypt.hashpw(pass, BCrypt.gensalt(10)));
            account.setRole(role);
            account.setTeacherId(teacherId);
            account.setStudentId(studentId);
            account.setStaffId(null);
            account.setIsActive(true);

            adminRepo.saveUserAccount(em, account);
            return null;
        });
    }

    /**
     * Tạo mới một lớp học (class) cho Admin.
     * @param className Tên lớp học
     * @param courseId ID khóa học
     * @param teacherId ID giáo viên (có thể null)
     * @param startDate Ngày bắt đầu (yyyy-MM-dd)
     * @param endDate Ngày kết thúc (yyyy-MM-dd, có thể null)
     * @param maxStudent Số lượng học viên tối đa
     * @param roomId ID phòng học (có thể null)
     * @return ID lớp học vừa tạo
     * @throws Exception nếu có lỗi
     */
    public Long createClass(String className, Long courseId, Long teacherId, String startDate, String endDate, Integer maxStudent, Long roomId) throws Exception {
        if (className == null || className.trim().isEmpty()) {
            throw new IllegalArgumentException("Tên lớp học không được để trống.");
        }
        if (courseId == null) {
            throw new IllegalArgumentException("Phải chọn khóa học.");
        }
        if (startDate == null || startDate.trim().isEmpty()) {
            throw new IllegalArgumentException("Phải nhập ngày bắt đầu.");
        }
        if (maxStudent == null || maxStudent <= 0) {
            throw new IllegalArgumentException("Số lượng học viên tối đa phải lớn hơn 0.");
        }
        return tx.runInTransaction(em -> {
            ClassEntity clazz = new ClassEntity();
            clazz.setClassName(className.trim());
            clazz.setCourseId(courseId);
            clazz.setTeacherId(teacherId);
            clazz.setStartDate(java.time.LocalDate.parse(startDate));
            if (endDate != null && !endDate.trim().isEmpty()) {
                clazz.setEndDate(java.time.LocalDate.parse(endDate));
            }
            clazz.setMaxStudent(maxStudent);
            clazz.setRoomId(roomId);
            clazz.setStatus("Planned"); // Trạng thái mặc định
            em.persist(clazz); // Lưu lớp học mới vào DB
            return clazz.getClassId();
        });
    }

    private String safeTrim(String input) {
        return input == null ? "" : input.trim();
    }
}

