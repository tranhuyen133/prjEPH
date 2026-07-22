package com.hrm.controller;

import com.hrm.dto.OrgDtos.*;
import com.hrm.entity.Department;
import com.hrm.entity.Position;
import com.hrm.exception.BusinessException;
import com.hrm.repository.DepartmentRepository;
import com.hrm.repository.PositionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class OrgController {

    private final DepartmentRepository departmentRepository;
    private final PositionRepository positionRepository;

    // ===== Departments =====
    @GetMapping("/departments")
    public List<DepartmentResponse> departments() {
        return departmentRepository.findAll().stream()
                .map(d -> new DepartmentResponse(d.getId(), d.getTenPhongBan(), d.getMoTa(),
                        d.getNhanViens() == null ? 0 : d.getNhanViens().size()))
                .toList();
    }

    @PostMapping("/departments")
    @PreAuthorize("hasRole('ADMIN')")
    public DepartmentResponse createDept(@RequestBody DepartmentRequest req) {
        Department d = departmentRepository.save(Department.builder()
                .tenPhongBan(req.tenPhongBan()).moTa(req.moTa()).build());
        return new DepartmentResponse(d.getId(), d.getTenPhongBan(), d.getMoTa(), 0);
    }

    @PutMapping("/departments/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public DepartmentResponse updateDept(@PathVariable Long id, @RequestBody DepartmentRequest req) {
        Department d = departmentRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Khong tim thay phong ban"));
        d.setTenPhongBan(req.tenPhongBan());
        d.setMoTa(req.moTa());
        departmentRepository.save(d);
        return new DepartmentResponse(d.getId(), d.getTenPhongBan(), d.getMoTa(),
                d.getNhanViens() == null ? 0 : d.getNhanViens().size());
    }

    @DeleteMapping("/departments/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteDept(@PathVariable Long id) {
        departmentRepository.deleteById(id);
    }

    // ===== Positions =====
    @GetMapping("/positions")
    public List<PositionResponse> positions() {
        return positionRepository.findAll().stream()
                .map(p -> new PositionResponse(p.getId(), p.getTenChucVu(), p.getLuongCoBan(), p.getPhuCap()))
                .toList();
    }

    @PostMapping("/positions")
    @PreAuthorize("hasRole('ADMIN')")
    public PositionResponse createPos(@RequestBody PositionRequest req) {
        Position p = positionRepository.save(Position.builder()
                .tenChucVu(req.tenChucVu())
                .luongCoBan(req.luongCoBan())
                .phuCap(req.phuCap() == null ? BigDecimal.ZERO : req.phuCap())
                .build());
        return new PositionResponse(p.getId(), p.getTenChucVu(), p.getLuongCoBan(), p.getPhuCap());
    }

    @PutMapping("/positions/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public PositionResponse updatePos(@PathVariable Long id, @RequestBody PositionRequest req) {
        Position p = positionRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Khong tim thay chuc vu"));
        p.setTenChucVu(req.tenChucVu());
        p.setLuongCoBan(req.luongCoBan());
        p.setPhuCap(req.phuCap() == null ? BigDecimal.ZERO : req.phuCap());
        positionRepository.save(p);
        return new PositionResponse(p.getId(), p.getTenChucVu(), p.getLuongCoBan(), p.getPhuCap());
    }

    @DeleteMapping("/positions/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void deletePos(@PathVariable Long id) {
        positionRepository.deleteById(id);
    }
}
