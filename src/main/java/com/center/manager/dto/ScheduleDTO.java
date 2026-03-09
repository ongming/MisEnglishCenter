package com.center.manager.dto;

import lombok.*;

/**
 * DTO cho bảng "Lịch dạy" của Giảng viên.
 *
 * Ví dụ:
 *   | Lớp           | Ngày       | Bắt đầu | Kết thúc |
 *   | COM-A1-0201   | 10/02/2026 | 18:30    | 20:30    |
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScheduleDTO {

    private Long scheduleId;    // ID (ẩn)
    private String className;   // Tên lớp
    private String studyDate;   // Ngày học
    private String startTime;   // Giờ bắt đầu
    private String endTime;     // Giờ kết thúc
}

