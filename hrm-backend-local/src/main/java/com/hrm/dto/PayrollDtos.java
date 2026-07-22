package com.hrm.dto;

import java.math.BigDecimal;

public class PayrollDtos {

    public record CalculateRequest(Integer thang, Integer nam) {}

    public record PayrollResponse(
            Long id,
            Long nhanVienId,
            String hoTen,
            Integer thang,
            Integer nam,
            BigDecimal luongCoBan,
            BigDecimal phuCap,
            Double soNgayCong,
            BigDecimal luongGross,
            BigDecimal baoHiem,
            BigDecimal thueTNCN,
            BigDecimal luongThucNhan,
            String trangThai
    ) {}
}
