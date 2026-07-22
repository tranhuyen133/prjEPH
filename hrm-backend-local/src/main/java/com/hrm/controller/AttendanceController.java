package com.hrm.controller;

import com.hrm.dto.AttendanceDtos.AttendanceResponse;
import com.hrm.service.AttendanceService;
import com.hrm.service.CurrentUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/attendance")
@RequiredArgsConstructor
public class AttendanceController {

    private final AttendanceService attendanceService;
    private final CurrentUserService currentUserService;

    @PostMapping("/check-in")
    public AttendanceResponse checkIn() {
        return attendanceService.checkIn(currentUserService.currentNhanVienId());
    }

    @PostMapping("/check-out")
    public AttendanceResponse checkOut() {
        return attendanceService.checkOut(currentUserService.currentNhanVienId());
    }

    @GetMapping("/me")
    public List<AttendanceResponse> me(
            @RequestParam(required = false) Integer thang,
            @RequestParam(required = false) Integer nam) {
        LocalDate now = LocalDate.now();
        int t = thang != null ? thang : now.getMonthValue();
        int n = nam != null ? nam : now.getYear();
        return attendanceService.monthly(currentUserService.currentNhanVienId(), t, n);
    }

    @GetMapping("/{nhanVienId}")
    public List<AttendanceResponse> byEmployee(
            @PathVariable Long nhanVienId,
            @RequestParam(required = false) Integer thang,
            @RequestParam(required = false) Integer nam) {
        LocalDate now = LocalDate.now();
        int t = thang != null ? thang : now.getMonthValue();
        int n = nam != null ? nam : now.getYear();
        return attendanceService.monthly(nhanVienId, t, n);
    }
}
