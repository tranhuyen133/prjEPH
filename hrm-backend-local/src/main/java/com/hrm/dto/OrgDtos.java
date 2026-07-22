package com.hrm.dto;

import java.math.BigDecimal;

public class OrgDtos {

    public record DepartmentRequest(String tenPhongBan, String moTa) {}

    public record DepartmentResponse(Long id, String tenPhongBan, String moTa, long soNhanVien) {}

    public record PositionRequest(String tenChucVu, BigDecimal luongCoBan, BigDecimal phuCap) {}

    public record PositionResponse(Long id, String tenChucVu, BigDecimal luongCoBan, BigDecimal phuCap) {}
}
