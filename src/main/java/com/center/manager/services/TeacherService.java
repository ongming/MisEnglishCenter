package com.center.manager.services;

import com.center.manager.dto.*;
import com.center.manager.entities.*;
import com.center.manager.repositories.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service xử lý TOÀN BỘ logic cho chức năng Giảng viên.
 *
 * --- Nguyên lý Single Responsibility (SOLID) ---
 * Controller chỉ lo giao diện (nhấn nút, hiện bảng).
 * Service lo xử lý logic (lấy dữ liệu, chuyển đổi, kiểm tra).
 * Repository lo truy vấn DB.
 *
 * Luồng: Controller → TeacherService → Repository → Database
 *
 * --- @Transactional ---
 * Đánh dấu readOnly=true cho các hàm chỉ ĐỌC dữ liệu (không thêm/sửa/xóa).
 * Giúp Hibernate tối ưu hiệu năng (không cần theo dõi thay đổi).
 */
@Service
public class TeacherService {

    // Các repository cần dùng — inject qua Constructor
    private final ClassRepository classRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final ScheduleRepository scheduleRepository;
    private final AttendanceRepository attendanceRepository;

    // Formatter để chuyển LocalDate → String hiển thị (ví dụ: 10/02/2026)
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    /**
     * Constructor Injection — Spring tự truyền tất cả Repository vào.
     */
    public TeacherService(ClassRepository classRepository,
                          EnrollmentRepository enrollmentRepository,
                          ScheduleRepository scheduleRepository,
                          AttendanceRepository attendanceRepository) {
        this.classRepository = classRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.scheduleRepository = scheduleRepository;
        this.attendanceRepository = attendanceRepository;
    }

    // =====================================================
    // 1. LỚP ĐANG DẠY (My Classes)
    // =====================================================

    /**
     * Lấy danh sách lớp mà giảng viên đang phụ trách.
     *
     * Luồng: teacherId → ClassRepository → List<ClassEntity> → chuyển thành List<ClassDTO>
     *
     * --- Stream API ---
     * .stream()        : Chuyển List thành luồng dữ liệu để xử lý
     * .map(...)        : Chuyển đổi từng phần tử (ClassEntity → ClassDTO)
     * .collect(...)    : Gom kết quả lại thành List mới
     *
     * @param teacherId ID của giảng viên (lấy từ UserSession)
     * @return danh sách ClassDTO để hiển thị lên TableView
     */
    @Transactional(readOnly = true)
    public List<ClassDTO> getClassesByTeacher(Long teacherId) {
        // Bước 1: Lấy danh sách ClassEntity từ DB
        List<ClassEntity> classes = classRepository.findByTeacher_TeacherId(teacherId);

        // Bước 2: Chuyển đổi Entity → DTO bằng Stream API (Lambda)
        // Mỗi ClassEntity được chuyển thành ClassDTO thông qua hàm convertToClassDTO
        return classes.stream()
                .map(this::convertToClassDTO)  // this::convertToClassDTO = (c) -> convertToClassDTO(c)
                .collect(Collectors.toList());
    }

    /**
     * Lấy danh sách học viên trong 1 lớp cụ thể.
     *
     * Luồng: classId → EnrollmentRepository → List<Enrollment> → chuyển thành List<StudentInClassDTO>
     *
     * @param classId ID lớp học
     * @return danh sách StudentInClassDTO
     */
    @Transactional(readOnly = true)
    public List<StudentInClassDTO> getStudentsInClass(Long classId) {
        // Bước 1: Lấy tất cả enrollment (ghi danh) của lớp này
        List<Enrollment> enrollments = enrollmentRepository.findByClassEntity_ClassId(classId);

        // Bước 2: Chuyển Enrollment → StudentInClassDTO
        // Mỗi Enrollment chứa 1 Student → lấy thông tin student ra
        return enrollments.stream()
                .map(this::convertToStudentDTO)
                .collect(Collectors.toList());
    }

    // =====================================================
    // 2. LỊCH DẠY (Schedule)
    // =====================================================

    /**
     * Lấy lịch dạy của giảng viên (tất cả buổi học của tất cả lớp GV đang dạy).
     *
     * Luồng:
     *   1. teacherId → lấy danh sách classId
     *   2. danh sách classId → lấy tất cả schedule
     *   3. schedule → chuyển thành ScheduleDTO
     *
     * @param teacherId ID giảng viên
     * @return danh sách ScheduleDTO
     */
    @Transactional(readOnly = true)
    public List<ScheduleDTO> getScheduleByTeacher(Long teacherId) {
        // Bước 1: Lấy danh sách lớp GV đang dạy
        List<ClassEntity> classes = classRepository.findByTeacher_TeacherId(teacherId);

        // Bước 2: Lấy danh sách classId từ các lớp đó
        // Stream: mỗi ClassEntity → lấy classId → gom thành List<Long>
        List<Long> classIds = classes.stream()
                .map(ClassEntity::getClassId)  // ClassEntity::getClassId = (c) -> c.getClassId()
                .collect(Collectors.toList());

        // Bước 3: Nếu GV không dạy lớp nào → trả về list rỗng
        if (classIds.isEmpty()) {
            return List.of();  // List.of() = tạo list rỗng bất biến
        }

        // Bước 4: Lấy tất cả schedule của các lớp đó (đã sắp xếp theo ngày)
        List<Schedule> schedules = scheduleRepository
                .findByClassEntity_ClassIdInOrderByStudyDateAscStartTimeAsc(classIds);

        // Bước 5: Chuyển Schedule → ScheduleDTO
        return schedules.stream()
                .map(this::convertToScheduleDTO)
                .collect(Collectors.toList());
    }

    // =====================================================
    // 3. ĐIỂM DANH (Attendance)
    // =====================================================

    /**
     * Lấy danh sách điểm danh của 1 lớp trong 1 ngày.
     *
     * @param classId    ID lớp
     * @param attendDate ngày điểm danh
     * @return danh sách AttendanceDTO
     */
    @Transactional(readOnly = true)
    public List<AttendanceDTO> getAttendanceByClassAndDate(Long classId, LocalDate attendDate) {
        List<Attendance> attendances = attendanceRepository
                .findByClassEntity_ClassIdAndAttendDate(classId, attendDate);

        return attendances.stream()
                .map(this::convertToAttendanceDTO)
                .collect(Collectors.toList());
    }

    /**
     * Lấy toàn bộ lịch sử điểm danh của 1 lớp.
     *
     * @param classId ID lớp
     * @return danh sách AttendanceDTO
     */
    @Transactional(readOnly = true)
    public List<AttendanceDTO> getAttendanceHistoryByClass(Long classId) {
        List<Attendance> attendances = attendanceRepository
                .findByClassEntity_ClassIdOrderByAttendDateDesc(classId);

        return attendances.stream()
                .map(this::convertToAttendanceDTO)
                .collect(Collectors.toList());
    }

    /**
     * Lưu điểm danh cho 1 học viên.
     *
     * Nếu đã có bản ghi điểm danh (cùng student + class + ngày) → cập nhật.
     * Nếu chưa có → tạo mới.
     *
     * @param dto dữ liệu điểm danh từ giao diện
     */
    @Transactional
    public void saveAttendance(AttendanceDTO dto) {
        Attendance attendance;

        if (dto.getAttendanceId() != null) {
            // Đã có bản ghi → tìm và cập nhật
            attendance = attendanceRepository.findById(dto.getAttendanceId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy bản ghi điểm danh"));
        } else {
            // Chưa có → tạo mới
            attendance = new Attendance();

            // Tạo reference tới Student và ClassEntity (chỉ cần set ID)
            Student student = new Student();
            student.setStudentId(dto.getStudentId());
            attendance.setStudent(student);

            ClassEntity classEntity = new ClassEntity();
            classEntity.setClassId(dto.getClassId());
            attendance.setClassEntity(classEntity);

            attendance.setAttendDate(LocalDate.parse(dto.getAttendDate(), DATE_FORMAT));
        }

        // Cập nhật trạng thái và ghi chú
        attendance.setStatus(dto.getStatus());
        attendance.setNote(dto.getNote());

        // Lưu vào DB (save = INSERT nếu mới, UPDATE nếu đã có ID)
        attendanceRepository.save(attendance);
    }

    // =====================================================
    // PRIVATE: Hàm chuyển đổi Entity → DTO
    // =====================================================

    /**
     * Chuyển ClassEntity → ClassDTO.
     * Lấy tên khóa học từ Course (quan hệ ManyToOne).
     */
    private ClassDTO convertToClassDTO(ClassEntity entity) {
        return ClassDTO.builder()
                .classId(entity.getClassId())
                .className(entity.getClassName())
                // entity.getCourse() = lấy Course từ quan hệ ManyToOne
                .courseName(entity.getCourse() != null ? entity.getCourse().getCourseName() : "N/A")
                .startDate(entity.getStartDate() != null ? entity.getStartDate().format(DATE_FORMAT) : "")
                .endDate(entity.getEndDate() != null ? entity.getEndDate().format(DATE_FORMAT) : "")
                .maxStudent(entity.getMaxStudent())
                .status(entity.getStatus())
                .build();
    }

    /**
     * Chuyển Enrollment → StudentInClassDTO.
     * Lấy thông tin Student từ quan hệ ManyToOne trong Enrollment.
     */
    private StudentInClassDTO convertToStudentDTO(Enrollment enrollment) {
        Student student = enrollment.getStudent();

        return StudentInClassDTO.builder()
                .studentId(student.getStudentId())
                .fullName(student.getFullName())
                .phone(student.getPhone() != null ? student.getPhone() : "")
                .email(student.getEmail() != null ? student.getEmail() : "")
                .enrollmentDate(enrollment.getEnrollmentDate() != null
                        ? enrollment.getEnrollmentDate().format(DATE_FORMAT) : "")
                .enrollmentStatus(enrollment.getStatus())
                .build();
    }

    /**
     * Chuyển Schedule → ScheduleDTO.
     */
    private ScheduleDTO convertToScheduleDTO(Schedule schedule) {
        return ScheduleDTO.builder()
                .scheduleId(schedule.getScheduleId())
                // schedule.getClassEntity() = lấy ClassEntity từ quan hệ ManyToOne
                .className(schedule.getClassEntity() != null
                        ? schedule.getClassEntity().getClassName() : "N/A")
                .studyDate(schedule.getStudyDate() != null
                        ? schedule.getStudyDate().format(DATE_FORMAT) : "")
                .startTime(schedule.getStartTime() != null
                        ? schedule.getStartTime().toString() : "")
                .endTime(schedule.getEndTime() != null
                        ? schedule.getEndTime().toString() : "")
                .build();
    }

    /**
     * Chuyển Attendance → AttendanceDTO.
     */
    private AttendanceDTO convertToAttendanceDTO(Attendance attendance) {
        return AttendanceDTO.builder()
                .attendanceId(attendance.getAttendanceId())
                .studentId(attendance.getStudent().getStudentId())
                .classId(attendance.getClassEntity().getClassId())
                .className(attendance.getClassEntity().getClassName())
                .studentName(attendance.getStudent().getFullName())
                .attendDate(attendance.getAttendDate() != null
                        ? attendance.getAttendDate().format(DATE_FORMAT) : "")
                .status(attendance.getStatus())
                .note(attendance.getNote())
                .build();
    }
}

