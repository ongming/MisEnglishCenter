package com.center.manager.dto;

import lombok.*;

/**
 * DTO (Data Transfer Object) cho bảng "Lớp đang dạy" của Giảng viên.
 *
 * --- DTO là gì? ---
 * DTO là class trung gian, chỉ chứa DỮ LIỆU CẦN HIỂN THỊ lên giao diện.
 * Không dùng Entity trực tiếp vì:
 *   1. Entity có thể chứa quá nhiều field không cần hiển thị.
 *   2. TableView của JavaFX cần các getter đơn giản, DTO dễ điều khiển hơn.
 *   3. Tránh lỗi LazyInitializationException khi truy cập quan hệ ngoài session.
 *
 * Ví dụ hiển thị trên bảng:
 *   | Tên lớp       | Khóa học          | Bắt đầu    | Kết thúc   | Sĩ số | Trạng thái |
 *   | COM-A1-0201   | English Comm A1   | 10/02/2026 | 05/04/2026 | 25    | Ongoing    |
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClassDTO {

    private Long classId;           // ID lớp (ẩn trên bảng, dùng để query sau)
    private String className;       // Tên lớp
    private String courseName;      // Tên khóa học
    private String startDate;       // Ngày bắt đầu (String để hiển thị dễ)
    private String endDate;         // Ngày kết thúc
    private Integer maxStudent;     // Sĩ số tối đa
    private String status;          // Trạng thái
}

