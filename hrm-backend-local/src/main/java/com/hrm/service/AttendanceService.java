package com.hrm.service;

import com.hrm.dto.AttendanceDtos.AttendanceResponse;
import com.hrm.entity.Attendance;
import com.hrm.entity.Employee;
import com.hrm.exception.BusinessException;
import com.hrm.repository.AttendanceRepository;
import com.hrm.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AttendanceService {

    private static final LocalTime CHECK_IN_LIMIT = LocalTime.of(8, 15);
    private static final LocalTime CHECK_OUT_LIMIT = LocalTime.of(17, 30);
    private static final long LUNCH_MINUTES = 90;

    private final AttendanceRepository attendanceRepository;
    private final EmployeeRepository employeeRepository;

    public AttendanceResponse checkIn(Long nhanVienId) {
        Employee nv = employee(nhanVienId);
        LocalDate today = LocalDate.now();
        Attendance att = attendanceRepository.findByNhanVienIdAndNgay(nhanVienId, today)
                .orElseGet(() -> Attendance.builder().nhanVien(nv).ngay(today).build());

        if (att.getGioVao() != null) {
            throw new BusinessException("Ban da check-in hom nay roi");
        }
        LocalTime now = LocalTime.now();
        att.setGioVao(now);
        att.setDiTre(now.isAfter(CHECK_IN_LIMIT));
        return toResponse(attendanceRepository.save(att));
    }

    public AttendanceResponse checkOut(Long nhanVienId) {
        LocalDate today = LocalDate.now();
        Attendance att = attendanceRepository.findByNhanVienIdAndNgay(nhanVienId, today)
                .orElseThrow(() -> new BusinessException("Ban chua check-in hom nay"));

        if (att.getGioVao() == null) throw new BusinessException("Ban chua check-in hom nay");
        if (att.getGioRa() != null) throw new BusinessException("Ban da check-out hom nay roi");

        LocalTime now = LocalTime.now();
        att.setGioRa(now);
        att.setVeSom(now.isBefore(CHECK_OUT_LIMIT));

        long minutes = Duration.between(att.getGioVao(), now).toMinutes() - LUNCH_MINUTES;
        double hours = Math.max(0, minutes) / 60.0;
        att.setSoGioLam(Math.round(hours * 100.0) / 100.0);

        return toResponse(attendanceRepository.save(att));
    }

    public List<AttendanceResponse> monthly(Long nhanVienId, int thang, int nam) {
        LocalDate tu = LocalDate.of(nam, thang, 1);
        LocalDate den = tu.withDayOfMonth(tu.lengthOfMonth());
        return attendanceRepository
                .findByNhanVienIdAndNgayBetweenOrderByNgayAsc(nhanVienId, tu, den)
                .stream().map(this::toResponse).toList();
    }

    private Employee employee(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Khong tim thay nhan vien"));
    }

    private AttendanceResponse toResponse(Attendance a) {
        return new AttendanceResponse(
                a.getId(), a.getNhanVien().getId(), a.getNhanVien().getHoTen(),
                a.getNgay(), a.getGioVao(), a.getGioRa(), a.getSoGioLam(),
                a.isDiTre(), a.isVeSom());
    }
}
