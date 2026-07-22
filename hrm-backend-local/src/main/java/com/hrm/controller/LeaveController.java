package com.hrm.controller;

import com.hrm.dto.LeaveDtos.*;
import com.hrm.service.CurrentUserService;
import com.hrm.service.LeaveService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/leaves")
@RequiredArgsConstructor
public class LeaveController {

    private final LeaveService leaveService;
    private final CurrentUserService currentUserService;

    @PostMapping("/request")
    public LeaveResponse request(@RequestBody LeaveRequestDto dto) {
        return leaveService.request(currentUserService.currentNhanVienId(), dto);
    }

    @GetMapping("/me")
    public List<LeaveResponse> mine() {
        return leaveService.mine(currentUserService.currentNhanVienId());
    }

    @GetMapping("/pending")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public List<LeaveResponse> pending() {
        return leaveService.pending();
    }

    @PutMapping("/approve/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public LeaveResponse approve(@PathVariable Long id, @RequestBody ApproveDto dto) {
        return leaveService.approve(id, currentUserService.currentNhanVienId(), dto);
    }
}
