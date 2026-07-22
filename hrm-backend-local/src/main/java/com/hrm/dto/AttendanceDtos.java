package com.hrm.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public class AttendanceDtos {

    public record AttendanceResponse(
            Long id,
            Long nhanVienId,
            String hoTen,
            LocalDate ngay,
            LocalTime gioVao,
            LocalTime gioRa,
            Double soGioLam,
            boolean diTre,
            boolean veSom
    ) {}
}
