package com.center.manager.controllers.teacher;

import com.center.manager.config.UserSession;
import com.center.manager.dto.ClassDTO;
import com.center.manager.dto.StudentInClassDTO;
import com.center.manager.services.TeacherService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Controller cho tab "Lớp đang dạy" của Giảng viên.
 * Liên kết với file: teacher/my-classes-tab.fxml
 *
 * Chức năng:
 *   1. Load danh sách lớp GV đang dạy → hiển thị lên bảng trên.
 *   2. Khi GV click chọn 1 lớp → load danh sách HV trong lớp đó → hiện bảng dưới.
 *
 * --- Luồng dữ liệu ---
 * initialize() → TeacherService.getClassesByTeacher(teacherId) → hiện bảng lớp
 * click lớp   → TeacherService.getStudentsInClass(classId)     → hiện bảng HV
 *
 * --- JavaFX TableView ---
 * TableView cần 2 thứ:
 *   1. TableColumn — cột (đã khai báo trong FXML)
 *   2. ObservableList — dữ liệu (tạo trong code Java)
 *
 * setCellValueFactory: Bảo JavaFX lấy giá trị nào để hiển thị trong cột đó.
 * Ví dụ: colClassName lấy giá trị từ classDTO.getClassName()
 */
@Component
public class MyClassesTabController {

    // ========== FXML Controls (liên kết với fx:id trong FXML) ==========

    // Bảng danh sách lớp
    @FXML private TableView<ClassDTO> tableClasses;
    @FXML private TableColumn<ClassDTO, String> colClassName;
    @FXML private TableColumn<ClassDTO, String> colCourseName;
    @FXML private TableColumn<ClassDTO, String> colStartDate;
    @FXML private TableColumn<ClassDTO, String> colEndDate;
    @FXML private TableColumn<ClassDTO, String> colMaxStudent;
    @FXML private TableColumn<ClassDTO, String> colStatus;

    // Bảng danh sách học viên
    @FXML private TableView<StudentInClassDTO> tableStudents;
    @FXML private TableColumn<StudentInClassDTO, String> colStudentName;
    @FXML private TableColumn<StudentInClassDTO, String> colStudentPhone;
    @FXML private TableColumn<StudentInClassDTO, String> colStudentEmail;
    @FXML private TableColumn<StudentInClassDTO, String> colEnrollDate;
    @FXML private TableColumn<StudentInClassDTO, String> colEnrollStatus;

    // Label tiêu đề bảng học viên (thay đổi khi chọn lớp)
    @FXML private Label lblStudentListTitle;

    // ========== Service ==========
    private final TeacherService teacherService;

    /**
     * Constructor Injection — Spring tự truyền TeacherService vào.
     */
    public MyClassesTabController(TeacherService teacherService) {
        this.teacherService = teacherService;
    }

    /**
     * Hàm khởi tạo — JavaFX gọi tự động sau khi load FXML.
     *
     * Bước 1: Cấu hình các cột cho 2 bảng.
     * Bước 2: Load dữ liệu lớp từ DB.
     * Bước 3: Lắng nghe sự kiện click vào bảng lớp.
     */
    @FXML
    private void initialize() {
        // --- Bước 1: Cấu hình cột cho bảng LỚP ---
        // setCellValueFactory: Bảo mỗi cột lấy giá trị gì từ ClassDTO
        // SimpleStringProperty: Bọc String thành Property để JavaFX hiển thị được
        colClassName.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().getClassName()));
        colCourseName.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().getCourseName()));
        colStartDate.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().getStartDate()));
        colEndDate.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().getEndDate()));
        colMaxStudent.setCellValueFactory(cell ->
                new SimpleStringProperty(
                        cell.getValue().getMaxStudent() != null
                                ? cell.getValue().getMaxStudent().toString() : "0"));
        colStatus.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().getStatus()));

        // --- Bước 1b: Cấu hình cột cho bảng HỌC VIÊN ---
        colStudentName.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().getFullName()));
        colStudentPhone.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().getPhone()));
        colStudentEmail.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().getEmail()));
        colEnrollDate.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().getEnrollmentDate()));
        colEnrollStatus.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().getEnrollmentStatus()));

        // --- Bước 2: Load danh sách lớp từ DB ---
        loadMyClasses();

        // --- Bước 3: Lắng nghe khi GV click chọn 1 lớp ---
        // getSelectionModel() = quản lý hàng được chọn trong bảng
        // selectedItemProperty() = Property thay đổi mỗi khi chọn hàng mới
        // addListener = đăng ký hàm xử lý khi giá trị thay đổi
        // (obs, oldVal, newVal) = (observable, giá trị cũ, giá trị mới)
        tableClasses.getSelectionModel().selectedItemProperty()
                .addListener((obs, oldVal, newVal) -> {
                    if (newVal != null) {
                        // Khi chọn lớp mới → load danh sách HV của lớp đó
                        loadStudentsInClass(newVal);
                    }
                });
    }

    /**
     * Load danh sách lớp GV đang dạy → hiển thị lên tableClasses.
     */
    private void loadMyClasses() {
        // Lấy teacherId từ session (đã lưu lúc đăng nhập)
        Long teacherId = UserSession.getInstance().getCurrentUser().getTeacherId();

        if (teacherId == null) {
            System.out.println("[WARN] teacherId = null, GV chưa liên kết teacher.");
            return;
        }

        // Gọi Service để lấy dữ liệu
        List<ClassDTO> classes = teacherService.getClassesByTeacher(teacherId);

        // Chuyển List thành ObservableList (JavaFX yêu cầu dùng ObservableList cho TableView)
        ObservableList<ClassDTO> data = FXCollections.observableArrayList(classes);

        // Gắn dữ liệu vào bảng
        tableClasses.setItems(data);
    }

    /**
     * Load danh sách HV trong lớp được chọn → hiển thị lên tableStudents.
     *
     * @param selectedClass lớp GV vừa click chọn
     */
    private void loadStudentsInClass(ClassDTO selectedClass) {
        // Đổi tiêu đề: "Danh sách học viên — Lớp COM-A1-0201"
        lblStudentListTitle.setText("Danh sách học viên — Lớp " + selectedClass.getClassName());

        // Gọi Service
        List<StudentInClassDTO> students = teacherService.getStudentsInClass(selectedClass.getClassId());

        // Hiển thị lên bảng
        ObservableList<StudentInClassDTO> data = FXCollections.observableArrayList(students);
        tableStudents.setItems(data);
    }
}

