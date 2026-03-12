package com.center.manager.service;

import com.center.manager.db.TransactionManager;
import com.center.manager.model.Teacher;
import com.center.manager.model.UserAccount;
import com.center.manager.repo.AdminRepository;
import org.mindrot.jbcrypt.BCrypt;

import java.time.LocalDate;
import java.util.List;

/**
 * Service xử lý nghiệp vụ Admin: quản lý giáo viên và tạo tài khoản.
 */
public class AdminService {

    private final AdminRepository adminRepo;
    private final TransactionManager tx;

    public AdminService(AdminRepository adminRepo, TransactionManager tx) {
        this.adminRepo = adminRepo;
        this.tx = tx;
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

    private String safeTrim(String input) {
        return input == null ? "" : input.trim();
    }
}

