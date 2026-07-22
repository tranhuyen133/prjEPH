package com.hrm.controller;

import com.hrm.dto.ContractDtos.*;
import com.hrm.entity.Contract;
import com.hrm.entity.Employee;
import com.hrm.exception.BusinessException;
import com.hrm.repository.ContractRepository;
import com.hrm.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/contracts")
@RequiredArgsConstructor
public class ContractController {

    private final ContractRepository contractRepository;
    private final EmployeeRepository employeeRepository;

    @PostMapping("/employee/{nhanVienId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ContractResponse create(@PathVariable Long nhanVienId, @RequestBody ContractRequest req) {
        Employee nv = employeeRepository.findById(nhanVienId)
                .orElseThrow(() -> new BusinessException("Khong tim thay nhan vien"));
        Contract c = Contract.builder()
                .nhanVien(nv)
                .soHopDong(req.soHopDong())
                .loaiHopDong(req.loaiHopDong())
                .tuNgay(req.tuNgay())
                .denNgay(req.denNgay())
                .luong(req.luong())
                .trangThai(req.trangThai() == null ? "HIEU_LUC" : req.trangThai())
                .build();
        return toResponse(contractRepository.save(c));
    }

    @GetMapping("/employee/{nhanVienId}")
    public List<ContractResponse> byEmployee(@PathVariable Long nhanVienId) {
        return contractRepository.findByNhanVienIdOrderByTuNgayDesc(nhanVienId)
                .stream().map(this::toResponse).toList();
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public List<ContractResponse> all() {
        return contractRepository.findAll().stream().map(this::toResponse).toList();
    }

    @GetMapping("/expiring")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public List<ContractResponse> expiring() {
        LocalDate today = LocalDate.now();
        return contractRepository.findByDenNgayBetween(today, today.plusDays(30))
                .stream().map(this::toResponse).toList();
    }

    private ContractResponse toResponse(Contract c) {
        return new ContractResponse(
                c.getId(), c.getNhanVien().getId(), c.getNhanVien().getHoTen(),
                c.getSoHopDong(), c.getLoaiHopDong(), c.getTuNgay(), c.getDenNgay(),
                c.getLuong(), c.getTrangThai());
    }
}
