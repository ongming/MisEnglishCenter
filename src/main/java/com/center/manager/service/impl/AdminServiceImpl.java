package com.center.manager.service.impl;

import com.center.manager.db.TransactionManager;
import com.center.manager.model.ClassEntity;
import com.center.manager.model.Enrollment;
import com.center.manager.model.Student;
import com.center.manager.model.Teacher;
import com.center.manager.model.UserAccount;
import com.center.manager.repo.AdminRepository;
import com.center.manager.service.AdminService;
import org.mindrot.jbcrypt.BCrypt;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Implement AdminService: quan ly giao vien va account.
 */
public class AdminServiceImpl implements AdminService {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern PHONE_PATTERN =
            Pattern.compile("^(0\\d{9}|\\+84\\d{9})$");

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

    @Override
    public List<Object[]> getAllStudents() throws Exception {
        return tx.runInTransaction(adminRepo::findAllStudents);
    }

    @Override
    public Long createStudent(String fullName, String dateOfBirth, String gender,
                              String phone, String email, String address,
                              String registrationDate, String status) throws Exception {
        String name = safeTrim(fullName);
        if (name.isEmpty()) {
            throw new IllegalArgumentException("Ho ten hoc vien khong duoc de trong.");
        }

        return tx.runInTransaction(em -> {
            String normalizedEmail = normalizeEmail(email);
            String normalizedPhone = normalizePhone(phone);
            validateStudentContactOrThrow(normalizedEmail, normalizedPhone);
            validateStudentDuplicateOrThrow(em, normalizedEmail, normalizedPhone, null);

            Student student = new Student();
            student.setFullName(name);
            student.setDateOfBirth(parseDateOrNull(dateOfBirth, "Ngay sinh"));
            student.setGender(safeTrim(gender));
            student.setPhone(normalizedPhone);
            student.setEmail(normalizedEmail);
            student.setAddress(safeTrim(address));
            student.setRegistrationDate(parseDateOrDefaultNow(registrationDate));
            student.setStatus(safeTrim(status).isEmpty() ? "Active" : safeTrim(status));

            adminRepo.saveStudent(em, student);
            return student.getStudentId();
        });
    }

    @Override
    public void updateStudent(Long studentId, String fullName, String dateOfBirth, String gender,
                              String phone, String email, String address,
                              String registrationDate, String status) throws Exception {
        if (studentId == null) {
            throw new IllegalArgumentException("Vui long chon hoc vien.");
        }

        String name = safeTrim(fullName);
        if (name.isEmpty()) {
            throw new IllegalArgumentException("Ho ten hoc vien khong duoc de trong.");
        }

        tx.runInTransaction(em -> {
            Student student = adminRepo.findStudentById(em, studentId);
            if (student == null) {
                throw new IllegalArgumentException("Khong tim thay hoc vien de cap nhat.");
            }

            String normalizedEmail = normalizeEmail(email);
            String normalizedPhone = normalizePhone(phone);
            validateStudentContactOrThrow(normalizedEmail, normalizedPhone);
            validateStudentDuplicateOrThrow(em, normalizedEmail, normalizedPhone, studentId);

            student.setFullName(name);
            student.setDateOfBirth(parseDateOrNull(dateOfBirth, "Ngay sinh"));
            student.setGender(safeTrim(gender));
            student.setPhone(normalizedPhone);
            student.setEmail(normalizedEmail);
            student.setAddress(safeTrim(address));

            String regDate = safeTrim(registrationDate);
            if (regDate.isEmpty()) {
                if (student.getRegistrationDate() == null) {
                    student.setRegistrationDate(LocalDate.now());
                }
            } else {
                student.setRegistrationDate(parseDateOrNull(regDate, "Ngay dang ky"));
            }

            String studentStatus = safeTrim(status);
            student.setStatus(studentStatus.isEmpty() ? "Active" : studentStatus);
            return null;
        });
    }

    @Override
    public void deleteStudent(Long studentId) throws Exception {
        if (studentId == null) {
            throw new IllegalArgumentException("Vui long chon hoc vien.");
        }

        tx.runInTransaction(em -> {
            adminRepo.deletePaymentsByStudent(em, studentId);
            adminRepo.deleteAttendancesByStudent(em, studentId);
            adminRepo.deleteEnrollmentsByStudent(em, studentId);
            adminRepo.deleteStudentAccounts(em, studentId);
            int affected = adminRepo.deleteStudentById(em, studentId);
            if (affected == 0) {
                throw new IllegalArgumentException("Khong tim thay hoc vien de xoa.");
            }
            return null;
        });
    }

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
    public void deleteTeacher(Long teacherId) throws Exception {
        if (teacherId == null) {
            throw new IllegalArgumentException("Vui long chon giao vien.");
        }

        tx.runInTransaction(em -> {
            adminRepo.clearTeacherFromClasses(em, teacherId);
            adminRepo.deleteTeacherAccounts(em, teacherId);
            int affected = adminRepo.deleteTeacherById(em, teacherId);
            if (affected == 0) {
                throw new IllegalArgumentException("Khong tim thay giao vien de xoa.");
            }
            return null;
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
    public List<Object[]> getStudentsNotInClass(Long classId) throws Exception {
        if (classId == null) {
            throw new IllegalArgumentException("Vui long chon lop hoc.");
        }
        return tx.runInTransaction(em -> adminRepo.findStudentsNotInClass(em, classId));
    }

    @Override
    public void addStudentToClass(Long classId, Long studentId) throws Exception {
        if (classId == null) {
            throw new IllegalArgumentException("Vui long chon lop hoc.");
        }
        if (studentId == null) {
            throw new IllegalArgumentException("Vui long chon hoc vien.");
        }

        tx.runInTransaction(em -> {
            if (adminRepo.existsEnrollment(em, studentId, classId)) {
                throw new IllegalArgumentException("Hoc vien da ton tai trong lop nay.");
            }

            Integer maxStudent = adminRepo.findClassMaxStudent(em, classId);
            if (maxStudent == null) {
                throw new IllegalArgumentException("Khong tim thay lop hoc.");
            }
            long current = adminRepo.countStudentsInClass(em, classId);
            if (current >= maxStudent) {
                throw new IllegalArgumentException("Lop hoc da day, khong the them hoc vien.");
            }

            Enrollment enrollment = new Enrollment();
            enrollment.setStudentId(studentId);
            enrollment.setClassId(classId);
            enrollment.setEnrollmentDate(LocalDate.now());
            enrollment.setStatus("Enrolled");
            enrollment.setResult("NA");
            adminRepo.saveEnrollment(em, enrollment);
            return null;
        });
    }

    @Override
    public void removeStudentFromClass(Long classId, Long studentId) throws Exception {
        if (classId == null || studentId == null) {
            throw new IllegalArgumentException("Thieu thong tin lop hoc/hoc vien.");
        }

        tx.runInTransaction(em -> {
            int affected = adminRepo.deleteEnrollment(em, studentId, classId);
            if (affected == 0) {
                throw new IllegalArgumentException("Hoc vien khong ton tai trong lop nay.");
            }
            return null;
        });
    }

    @Override
    public void createTeacherAccount(Long teacherId, String username, String rawPassword) throws Exception {
        createAccount("Teacher", teacherId, null, username, rawPassword);
    }

    @Override
    public void createStudentAccount(Long studentId, String username, String rawPassword) throws Exception {
        createAccount("Student", null, studentId, username, rawPassword);
    }

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

    private void validateStudentContactOrThrow(String email, String phone) {
        if (!email.isEmpty() && !EMAIL_PATTERN.matcher(email).matches()) {
            throw new IllegalArgumentException("Email khong dung dinh dang.");
        }
        if (!phone.isEmpty() && !PHONE_PATTERN.matcher(phone).matches()) {
            throw new IllegalArgumentException("So dien thoai khong hop le (vd: 0912345678 hoac +84912345678).");
        }
    }

    private void validateStudentDuplicateOrThrow(jakarta.persistence.EntityManager em, String email, String phone, Long excludeStudentId) {
        if (!email.isEmpty() && adminRepo.existsStudentEmail(em, email, excludeStudentId)) {
            throw new IllegalArgumentException("Email da ton tai.");
        }
        if (!phone.isEmpty() && adminRepo.existsStudentPhone(em, phone, excludeStudentId)) {
            throw new IllegalArgumentException("So dien thoai da ton tai.");
        }
    }

    private LocalDate parseDateOrDefaultNow(String input) {
        String value = safeTrim(input);
        return value.isEmpty() ? LocalDate.now() : parseDateOrNull(value, "Ngay");
    }

    private LocalDate parseDateOrNull(String input, String fieldName) {
        String value = safeTrim(input);
        if (value.isEmpty()) {
            return null;
        }
        try {
            return LocalDate.parse(value);
        } catch (DateTimeParseException ex) {
            throw new IllegalArgumentException(fieldName + " khong dung dinh dang yyyy-MM-dd.");
        }
    }

    private String normalizeEmail(String email) {
        return safeTrim(email).toLowerCase();
    }

    private String normalizePhone(String phone) {
        return safeTrim(phone);
    }

    private String safeTrim(String input) {
        return input == null ? "" : input.trim();
    }
}
