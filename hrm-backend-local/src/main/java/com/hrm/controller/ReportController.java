package com.hrm.controller;

import com.hrm.entity.Payroll;
import com.hrm.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.*;

@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
public class ReportController {

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;
    private final LeaveRequestRepository leaveRepository;
    private final PayrollRepository payrollRepository;

    @GetMapping("/dashboard")
    public Map<String, Object> dashboard() {
        Map<String, Object> m = new HashMap<>();
        m.put("tongNhanVien", employeeRepository.count());
        m.put("dangLamViec", employeeRepository.countByTrangThai("DANG_LAM"));
        m.put("soPhongBan", departmentRepository.count());
        m.put("donChoDuyet", leaveRepository
                .findByTrangThaiOrderByIdDesc(com.hrm.entity.LeaveStatus.CHO_DUYET).size());
        return m;
    }

    @GetMapping("/employees-by-department")
    public List<Map<String, Object>> employeesByDepartment() {
        return departmentRepository.findAll().stream()
                .map(d -> {
                    Map<String, Object> row = new HashMap<>();
                    row.put("phongBan", d.getTenPhongBan());
                    row.put("soNhanVien", employeeRepository.findByPhongBanId(d.getId()).size());
                    return row;
                })
                .toList();
    }

    @GetMapping("/payroll-summary")
    public Map<String, Object> payrollSummary(@RequestParam int thang, @RequestParam int nam) {
        List<Payroll> list = payrollRepository.findByThangAndNam(thang, nam);
        BigDecimal tongQuyLuong = list.stream()
                .map(Payroll::getLuongThucNhan)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        Map<String, Object> m = new HashMap<>();
        m.put("thang", thang);
        m.put("nam", nam);
        m.put("soPhieuLuong", list.size());
        m.put("tongQuyLuong", tongQuyLuong);
        return m;
    }
}
