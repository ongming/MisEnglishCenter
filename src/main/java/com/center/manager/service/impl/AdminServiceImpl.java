package com.center.manager.service.impl;

import com.center.manager.db.TransactionManager;
import com.center.manager.model.ClassEntity;
import com.center.manager.model.Teacher;
import com.center.manager.model.UserAccount;
import com.center.manager.repo.AdminRepository;
import com.center.manager.service.AdminService;
import org.mindrot.jbcrypt.BCrypt;

import java.time.LocalDate;
import java.util.List;

/**
 * Implement AdminService: quan ly giao vien va account.
 */
public class AdminServiceImpl implements AdminService {

    private final AdminRepository adminRepo;
    private final TransactionManager tx;

    public AdminServiceImpl(AdminRepository adminRepo, TransactionManager tx) {
        this.adminRepo = adminRepo;
        this.tx = tx;
    }

    @Override
    public List<Object[]> getAllTeachers() throws Exception {
        return tx.runInTransaction(adminRepo::findAllTeachers);
    }

    /**
     * Validate du lieu giao vien toi thieu truoc khi persist.
     */
    @Override
    public Long createTeacher(String fullName, String phone, String email, String specialty,
                              String hireDate, String status) throws Exception {
        String name = safeTrim(fullName);
        if (name.isEmpty()) {
            throw new IllegalArgumentException("Ho ten giao vien khong duoc de trong.");
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

    @Override
    public List<Object[]> getTeachersWithoutAccount() throws Exception {
        return tx.runInTransaction(adminRepo::findTeachersWithoutAccount);
    }

    @Override
    public List<Object[]> getStudentsWithoutAccount() throws Exception {
        return tx.runInTransaction(adminRepo::findStudentsWithoutAccount);
    }

    @Override
    public void createTeacherAccount(Long teacherId, String username, String rawPassword) throws Exception {
        createAccount("Teacher", teacherId, null, username, rawPassword);
    }

    @Override
    public void createStudentAccount(Long studentId, String username, String rawPassword) throws Exception {
        createAccount("Student", null, studentId, username, rawPassword);
    }

    /**
     * Tao lop hoc moi cho admin voi cac validate co ban.
     */
    @Override
    public Long createClass(String className, Long courseId, Long teacherId, String startDate,
                            String endDate, Integer maxStudent, Long roomId) throws Exception {
        if (safeTrim(className).isEmpty()) {
            throw new IllegalArgumentException("Ten lop hoc khong duoc de trong.");
        }
        if (courseId == null) {
            throw new IllegalArgumentException("Phai chon khoa hoc.");
        }
        if (safeTrim(startDate).isEmpty()) {
            throw new IllegalArgumentException("Phai nhap ngay bat dau.");
        }
        if (maxStudent == null || maxStudent <= 0) {
            throw new IllegalArgumentException("So luong hoc vien toi da phai lon hon 0.");
        }

        return tx.runInTransaction(em -> {
            ClassEntity clazz = new ClassEntity();
            clazz.setClassName(className.trim());
            clazz.setCourseId(courseId);
            clazz.setTeacherId(teacherId);
            clazz.setStartDate(LocalDate.parse(startDate));
            String endDateValue = safeTrim(endDate);
            if (!endDateValue.isEmpty()) {
                clazz.setEndDate(LocalDate.parse(endDateValue));
            }
            clazz.setMaxStudent(maxStudent);
            clazz.setRoomId(roomId);
            clazz.setStatus("Planned");
            em.persist(clazz);
            return clazz.getClassId();
        });
    }

    /**
     * Ham dung chung tao account va kiem tra trung username/quan he role.
     */
    private void createAccount(String role, Long teacherId, Long studentId, String username, String rawPassword) throws Exception {
        String user = safeTrim(username);
        String pass = rawPassword == null ? "" : rawPassword.trim();

        if (user.isEmpty()) {
            throw new IllegalArgumentException("Username khong duoc de trong.");
        }
        if (pass.isEmpty()) {
            throw new IllegalArgumentException("Password khong duoc de trong.");
        }

        tx.runInTransaction(em -> {
            if (adminRepo.existsUsername(em, user)) {
                throw new IllegalArgumentException("Username da ton tai.");
            }

            // Dung lambda de validate role theo map key-value, de them role moi de dang hon.
            List<Runnable> roleChecks = List.of(
                    () -> validateTeacherRole(em, role, teacherId),
                    () -> validateStudentRole(em, role, studentId)
            );
            roleChecks.forEach(Runnable::run);

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

    private void validateTeacherRole(jakarta.persistence.EntityManager em, String role, Long teacherId) {
        if (!"Teacher".equals(role)) {
            return;
        }
        if (teacherId == null) {
            throw new IllegalArgumentException("Vui long chon giao vien.");
        }
        if (adminRepo.existsTeacherAccount(em, teacherId)) {
            throw new IllegalArgumentException("Giao vien nay da co tai khoan.");
        }
    }

    private void validateStudentRole(jakarta.persistence.EntityManager em, String role, Long studentId) {
        if (!"Student".equals(role)) {
            return;
        }
        if (studentId == null) {
            throw new IllegalArgumentException("Vui long chon hoc vien.");
        }
        if (adminRepo.existsStudentAccount(em, studentId)) {
            throw new IllegalArgumentException("Hoc vien nay da co tai khoan.");
        }
    }

    private String safeTrim(String input) {
        return input == null ? "" : input.trim();
    }
}

