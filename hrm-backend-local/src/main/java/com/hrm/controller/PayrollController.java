package com.hrm.controller;

import com.hrm.dto.PayrollDtos.*;
import com.hrm.service.CurrentUserService;
import com.hrm.service.PayrollService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/payroll")
@RequiredArgsConstructor
public class PayrollController {

    private final PayrollService payrollService;
    private final CurrentUserService currentUserService;

    @PostMapping("/calculate")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public List<PayrollResponse> calculate(@RequestBody CalculateRequest req) {
        return payrollService.calculateAll(req.thang(), req.nam());
    }

    @PutMapping("/{id}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public PayrollResponse approve(@PathVariable Long id) {
        return payrollService.approve(id);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public List<PayrollResponse> byMonth(@RequestParam int thang, @RequestParam int nam) {
        return payrollService.byMonth(thang, nam);
    }

    @GetMapping("/me")
    public List<PayrollResponse> mine() {
        return payrollService.mine(currentUserService.currentNhanVienId());
    }
}
