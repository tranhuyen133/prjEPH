package com.hrm.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class EmployeeDtos {

    public record EmployeeRequest(
            String hoTen,
            LocalDate ngaySinh,
            String gioiTinh,
            String email,
            String soDienThoai,
            String diaChi,
            String anhDaiDien,
            LocalDate ngayVaoLam,
            String trangThai,
            Integer soNguoiPhuThuoc,
            Long phongBanId,
            Long chucVuId
    ) {}

    public record EmployeeResponse(
            Long id,
            String hoTen,
            LocalDate ngaySinh,
            String gioiTinh,
            String email,
            String soDienThoai,
            String diaChi,
            String anhDaiDien,
            LocalDate ngayVaoLam,
            String trangThai,
            Integer soNguoiPhuThuoc,
            Long phongBanId,
            String phongBan,
            Long chucVuId,
            String chucVu,
            BigDecimal luongCoBan
    ) {}
}
