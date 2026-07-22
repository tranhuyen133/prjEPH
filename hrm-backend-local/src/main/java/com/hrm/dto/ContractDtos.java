package com.hrm.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ContractDtos {

    public record ContractRequest(
            String soHopDong,
            String loaiHopDong,
            LocalDate tuNgay,
            LocalDate denNgay,
            BigDecimal luong,
            String trangThai
    ) {}

    public record ContractResponse(
            Long id,
            Long nhanVienId,
            String hoTen,
            String soHopDong,
            String loaiHopDong,
            LocalDate tuNgay,
            LocalDate denNgay,
            BigDecimal luong,
            String trangThai
    ) {}
}
