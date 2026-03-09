package com.center.manager.controllers.teacher;

import com.center.manager.config.UserSession;
import com.center.manager.dto.AttendanceDTO;
import com.center.manager.dto.ClassDTO;
import com.center.manager.dto.StudentInClassDTO;
import com.center.manager.services.TeacherService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.TextFieldTableCell;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Controller cho tab "Điểm danh" của Giảng viên.
 * Liên kết với file: teacher/attendance-tab.fxml
 *
 * Chức năng:
 *   1. GV chọn lớp (ComboBox) + chọn ngày (DatePicker)
 *   2. Nhấn "Tải danh sách" → hiện danh sách HV với trạng thái điểm danh
 *   3. GV chỉnh trạng thái (Present/Absent/Late) + ghi chú cho từng HV
 *   4. Nhấn "Lưu điểm danh" → lưu vào DB
 *
 * --- Bảng có thể chỉnh sửa (Editable TableView) ---
 * tableAttendance.setEditable(true) → cho phép sửa trực tiếp trên bảng.
 * ComboBoxTableCell: Hiện dropdown chọn Present/Absent/Late.
 * TextFieldTableCell: Cho phép gõ ghi chú trực tiếp.
 */
@Component("teacherAttendanceTabController")
public class AttendanceTabController {

    // ========== FXML Controls ==========
    @FXML private ComboBox<ClassDTO> cboClass;          // Dropdown chọn lớp
    @FXML private DatePicker dpAttendDate;               // Chọn ngày điểm danh
    @FXML private Label lblAttendanceTitle;              // Tiêu đề bảng

    @FXML private TableView<AttendanceDTO> tableAttendance;
    @FXML private TableColumn<AttendanceDTO, String> colAttStudent;
    @FXML private TableColumn<AttendanceDTO, String> colAttStatus;
    @FXML private TableColumn<AttendanceDTO, String> colAttNote;

    // ========== Service ==========
    private final TeacherService teacherService;

    // Formatter ngày
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public AttendanceTabController(TeacherService teacherService) {
        this.teacherService = teacherService;
    }

    /**
     * Khởi tạo: load danh sách lớp vào ComboBox + cấu hình bảng.
     */
    @FXML
    private void initialize() {
        // --- 1. Load danh sách lớp GV đang dạy vào ComboBox ---
        loadClassComboBox();

        // --- 2. Đặt ngày mặc định = hôm nay ---
        dpAttendDate.setValue(LocalDate.now());

        // --- 3. Cấu hình cột bảng ---
        // Cột "Học viên" — chỉ hiển thị, không cho sửa
        colAttStudent.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().getStudentName()));

        // Cột "Trạng thái" — ComboBox cho chọn Present/Absent/Late
        // Bước 1: Cho bảng chỉnh sửa được
        tableAttendance.setEditable(true);

        // Bước 2: Cột status dùng ComboBoxTableCell (dropdown trong ô)
        colAttStatus.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().getStatus()));
        colAttStatus.setCellFactory(ComboBoxTableCell.forTableColumn(
                FXCollections.observableArrayList("Present", "Absent", "Late")
        ));
        // Khi GV chọn giá trị mới trong dropdown → cập nhật vào DTO
        colAttStatus.setOnEditCommit(event -> {
            // event.getRowValue() = dòng đang sửa (AttendanceDTO)
            // event.getNewValue() = giá trị mới GV vừa chọn
            event.getRowValue().setStatus(event.getNewValue());
        });

        // Cột "Ghi chú" — TextField cho gõ trực tiếp
        colAttNote.setCellValueFactory(cell ->
                new SimpleStringProperty(
                        cell.getValue().getNote() != null ? cell.getValue().getNote() : ""));
        colAttNote.setCellFactory(TextFieldTableCell.forTableColumn());
        colAttNote.setOnEditCommit(event -> {
            event.getRowValue().setNote(event.getNewValue());
        });
    }

    /**
     * Load danh sách lớp GV đang dạy vào ComboBox.
     */
    private void loadClassComboBox() {
        Long teacherId = UserSession.getInstance().getCurrentUser().getTeacherId();
        if (teacherId == null) return;

        List<ClassDTO> classes = teacherService.getClassesByTeacher(teacherId);
        cboClass.setItems(FXCollections.observableArrayList(classes));

        // Hiển thị tên lớp trong ComboBox (thay vì hiện ClassDTO@xxxxx)
        // StringConverter: chuyển đổi ClassDTO ↔ String để ComboBox biết hiển thị gì
        cboClass.setConverter(new javafx.util.StringConverter<>() {
            @Override
            public String toString(ClassDTO dto) {
                return dto != null ? dto.getClassName() : "";
            }

            @Override
            public ClassDTO fromString(String s) {
                return null; // Không cần chuyển ngược
            }
        });
    }

    /**
     * Xử lý khi nhấn "Tải danh sách" — load điểm danh theo lớp + ngày.
     *
     * Nếu đã có điểm danh (trong DB) → hiện dữ liệu cũ.
     * Nếu chưa có → tạo danh sách mới từ enrollment (tất cả HV mặc định Present).
     */
    @FXML
    private void handleLoadAttendance() {
        // Kiểm tra đã chọn lớp và ngày chưa
        ClassDTO selectedClass = cboClass.getValue();
        LocalDate selectedDate = dpAttendDate.getValue();

        if (selectedClass == null || selectedDate == null) {
            showAlert("Vui lòng chọn lớp và ngày điểm danh.");
            return;
        }

        // Đổi tiêu đề
        lblAttendanceTitle.setText("Điểm danh — " + selectedClass.getClassName()
                + " — " + selectedDate.format(DATE_FORMAT));

        // Thử lấy dữ liệu đã có trong DB
        List<AttendanceDTO> existing = teacherService.getAttendanceByClassAndDate(
                selectedClass.getClassId(), selectedDate);

        if (!existing.isEmpty()) {
            // Đã có điểm danh ngày này → hiện lên để GV chỉnh sửa
            tableAttendance.setItems(FXCollections.observableArrayList(existing));
        } else {
            // Chưa có → tạo danh sách mới từ enrollment
            // Lấy tất cả HV trong lớp → tạo AttendanceDTO mặc định (Present)
            List<StudentInClassDTO> students = teacherService.getStudentsInClass(selectedClass.getClassId());

            // Stream: chuyển mỗi StudentInClassDTO → AttendanceDTO mới
            List<AttendanceDTO> newAttendances = students.stream()
                    .map(s -> AttendanceDTO.builder()
                            .attendanceId(null)              // null = chưa lưu (sẽ INSERT khi save)
                            .studentId(s.getStudentId())
                            .classId(selectedClass.getClassId())
                            .className(selectedClass.getClassName())
                            .studentName(s.getFullName())
                            .attendDate(selectedDate.format(DATE_FORMAT))
                            .status("Present")               // Mặc định = có mặt
                            .note("")
                            .build())
                    .collect(java.util.stream.Collectors.toList());

            tableAttendance.setItems(FXCollections.observableArrayList(newAttendances));
        }
    }

    /**
     * Xử lý khi nhấn "Lưu điểm danh" — lưu tất cả dòng trong bảng vào DB.
     */
    @FXML
    private void handleSaveAttendance() {
        ObservableList<AttendanceDTO> items = tableAttendance.getItems();

        if (items == null || items.isEmpty()) {
            showAlert("Chưa có dữ liệu điểm danh để lưu.");
            return;
        }

        try {
            // Duyệt từng dòng trong bảng → gọi Service lưu
            // forEach + Lambda: thay vì dùng vòng for truyền thống
            items.forEach(dto -> teacherService.saveAttendance(dto));

            showAlert("✅ Đã lưu điểm danh thành công!");

            // Reload lại để hiện dữ liệu đã lưu (có attendanceId)
            handleLoadAttendance();

        } catch (Exception e) {
            System.err.println("Lỗi khi lưu điểm danh: " + e.getMessage());
            e.printStackTrace();
            showAlert("❌ Lỗi khi lưu: " + e.getMessage());
        }
    }

    /**
     * Hiện hộp thoại thông báo.
     */
    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Thông báo");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}


