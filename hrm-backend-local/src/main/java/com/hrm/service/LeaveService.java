package com.hrm.service;

import com.hrm.dto.LeaveDtos.*;
import com.hrm.entity.Employee;
import com.hrm.entity.LeaveRequest;
import com.hrm.entity.LeaveStatus;
import com.hrm.exception.BusinessException;
import com.hrm.repository.EmployeeRepository;
import com.hrm.repository.LeaveRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LeaveService {

    @Value("${app.leave.annual-days:12}")
    private int annualDays;

    private final LeaveRequestRepository leaveRepository;
    private final EmployeeRepository employeeRepository;

    public LeaveResponse request(Long nhanVienId, LeaveRequestDto dto) {
        Employee nv = employeeRepository.findById(nhanVienId)
                .orElseThrow(() -> new BusinessException("Khong tim thay nhan vien"));

        if (dto.tuNgay() == null || dto.denNgay() == null || dto.denNgay().isBefore(dto.tuNgay())) {
            throw new BusinessException("Khoang ngay nghi khong hop le");
        }

        // chan trung lich voi don da duyet
        List<LeaveRequest> overlap = leaveRepository
                .findByNhanVienIdAndTrangThaiAndTuNgayLessThanEqualAndDenNgayGreaterThanEqual(
                        nhanVienId, LeaveStatus.DA_DUYET, dto.denNgay(), dto.tuNgay());
        if (!overlap.isEmpty()) {
            throw new BusinessException("Ban da co don nghi da duyet trung khoang ngay nay");
        }

        // kiem tra quy phep nam (chi tinh phep nam)
        if ("PHEP_NAM".equalsIgnoreCase(dto.loaiPhep())) {
            long daDung = leaveRepository
                    .findByNhanVienIdAndTrangThai(nhanVienId, LeaveStatus.DA_DUYET).stream()
                    .filter(l -> "PHEP_NAM".equalsIgnoreCase(l.getLoaiPhep()))
                    .mapToLong(l -> soNgay(l.getTuNgay(), l.getDenNgay()))
                    .sum();
            long xinThem = soNgay(dto.tuNgay(), dto.denNgay());
            if (daDung + xinThem > annualDays) {
                throw new BusinessException("Vuot qua quy phep nam (" + annualDays
                        + " ngay). Da dung: " + daDung + " ngay.");
            }
        }

        LeaveRequest lr = LeaveRequest.builder()
                .nhanVien(nv)
                .loaiPhep(dto.loaiPhep())
                .tuNgay(dto.tuNgay())
                .denNgay(dto.denNgay())
                .lyDo(dto.lyDo())
                .trangThai(LeaveStatus.CHO_DUYET)
                .build();
        return toResponse(leaveRepository.save(lr));
    }

    public List<LeaveResponse> mine(Long nhanVienId) {
        return leaveRepository.findByNhanVienIdOrderByIdDesc(nhanVienId)
                .stream().map(this::toResponse).toList();
    }

    public List<LeaveResponse> pending() {
        return leaveRepository.findByTrangThaiOrderByIdDesc(LeaveStatus.CHO_DUYET)
                .stream().map(this::toResponse).toList();
    }

    public LeaveResponse approve(Long id, Long nguoiDuyetNhanVienId, ApproveDto dto) {
        LeaveRequest lr = leaveRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Khong tim thay don nghi"));
        if (lr.getTrangThai() != LeaveStatus.CHO_DUYET) {
            throw new BusinessException("Don nay da duoc xu ly");
        }
        lr.setTrangThai(dto.approve() ? LeaveStatus.DA_DUYET : LeaveStatus.TU_CHOI);
        lr.setPhanHoi(dto.phanHoi());
        if (nguoiDuyetNhanVienId != null) {
            employeeRepository.findById(nguoiDuyetNhanVienId).ifPresent(lr::setNguoiDuyet);
        }
        return toResponse(leaveRepository.save(lr));
    }

    private long soNgay(java.time.LocalDate tu, java.time.LocalDate den) {
        return ChronoUnit.DAYS.between(tu, den) + 1;
    }

    private LeaveResponse toResponse(LeaveRequest l) {
        return new LeaveResponse(
                l.getId(), l.getNhanVien().getId(), l.getNhanVien().getHoTen(),
                l.getLoaiPhep(), l.getTuNgay(), l.getDenNgay(),
                soNgay(l.getTuNgay(), l.getDenNgay()), l.getLyDo(),
                l.getTrangThai().name(), l.getPhanHoi());
    }
}
