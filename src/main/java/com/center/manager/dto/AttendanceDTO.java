package com.center.manager.dto;

import lombok.*;

/**
 * DTO cho bảng "Điểm danh" của Giảng viên.
 *
 * Dùng để hiển thị trên TableView khi GV xem lịch sử điểm danh,
 * và cũng dùng khi GV điểm danh mới (chọn trạng thái cho từng HV).
 *
 * Ví dụ:
 *   | Lớp         | Học viên      | Ngày       | Trạng thái | Ghi chú           |
 *   | COM-A1-0201 | Phạm Gia Bảo | 10/02/2026 | Present    |                   |
 *   | COM-A1-0201 | Nguyễn Mỹ Linh| 10/02/2026| Late       | Đến trễ 10 phút  |
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttendanceDTO {

    private Long attendanceId;  // ID (null nếu chưa lưu)
    private Long studentId;     // ID học viên
    private Long classId;       // ID lớp
    private String className;   // Tên lớp
    private String studentName; // Tên học viên
    private String attendDate;  // Ngày điểm danh
    private String status;      // Present / Absent / Late
    private String note;        // Ghi chú
}

