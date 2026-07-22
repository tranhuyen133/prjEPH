package com.hrm.dto;

import java.time.LocalDate;

public class LeaveDtos {

    public record LeaveRequestDto(
            String loaiPhep,
            LocalDate tuNgay,
            LocalDate denNgay,
            String lyDo
    ) {}

    public record ApproveDto(boolean approve, String phanHoi) {}

    public record LeaveResponse(
            Long id,
            Long nhanVienId,
            String hoTen,
            String loaiPhep,
            LocalDate tuNgay,
            LocalDate denNgay,
            long soNgay,
            String lyDo,
            String trangThai,
            String phanHoi
    ) {}
}
