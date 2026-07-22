package com.hrm.service;

import com.hrm.dto.PayrollDtos.PayrollResponse;
import com.hrm.entity.Employee;
import com.hrm.entity.Payroll;
import com.hrm.exception.BusinessException;
import com.hrm.repository.AttendanceRepository;
import com.hrm.repository.EmployeeRepository;
import com.hrm.repository.PayrollRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PayrollService {

    private static final double STANDARD_WORKING_DAYS = 22.0;
    private static final BigDecimal INSURANCE_RATE = new BigDecimal("0.105"); // BHXH 8 + BHYT 1.5 + BHTN 1
    private static final BigDecimal SELF_DEDUCTION = new BigDecimal("11000000");
    private static final BigDecimal DEPENDENT_DEDUCTION = new BigDecimal("4400000");

    private final PayrollRepository payrollRepository;
    private final EmployeeRepository employeeRepository;
    private final AttendanceRepository attendanceRepository;

    public List<PayrollResponse> calculateAll(int thang, int nam) {
        List<Employee> employees = employeeRepository.findAll().stream()
                .filter(e -> !"NGHI_VIEC".equals(e.getTrangThai()))
                .toList();

        return employees.stream()
                .map(e -> calculateFor(e, thang, nam))
                .map(this::toResponse)
                .toList();
    }

    private Payroll calculateFor(Employee e, int thang, int nam) {
        if (e.getChucVu() == null) {
            throw new BusinessException("Nhan vien " + e.getHoTen() + " chua co chuc vu de tinh luong");
        }

        BigDecimal luongCoBan = e.getChucVu().getLuongCoBan();
        BigDecimal phuCap = e.getChucVu().getPhuCap() == null ? BigDecimal.ZERO : e.getChucVu().getPhuCap();

        LocalDate tu = LocalDate.of(nam, thang, 1);
        LocalDate den = tu.withDayOfMonth(tu.lengthOfMonth());
        long soNgayCong = attendanceRepository
                .findByNhanVienIdAndNgayBetweenOrderByNgayAsc(e.getId(), tu, den).stream()
                .filter(a -> a.getGioVao() != null && a.getGioRa() != null)
                .count();
        double ngayCong = soNgayCong == 0 ? STANDARD_WORKING_DAYS : soNgayCong;

        // luong theo cong
        BigDecimal luongTheoCong = luongCoBan
                .multiply(BigDecimal.valueOf(Math.min(ngayCong, STANDARD_WORKING_DAYS)))
                .divide(BigDecimal.valueOf(STANDARD_WORKING_DAYS), 2, RoundingMode.HALF_UP);

        BigDecimal gross = luongTheoCong.add(phuCap);

        // bao hiem 10.5% tren luong co ban
        BigDecimal baoHiem = luongCoBan.multiply(INSURANCE_RATE).setScale(2, RoundingMode.HALF_UP);

        // thu nhap chiu thue
        BigDecimal giamTru = SELF_DEDUCTION.add(
                DEPENDENT_DEDUCTION.multiply(BigDecimal.valueOf(e.getSoNguoiPhuThuoc())));
        BigDecimal thuNhapTinhThue = gross.subtract(baoHiem).subtract(giamTru);
        if (thuNhapTinhThue.signum() < 0) thuNhapTinhThue = BigDecimal.ZERO;

        BigDecimal thue = thueLuyTien(thuNhapTinhThue);

        BigDecimal thucNhan = gross.subtract(baoHiem).subtract(thue).setScale(2, RoundingMode.HALF_UP);

        Payroll p = payrollRepository.findByNhanVienIdAndThangAndNam(e.getId(), thang, nam)
                .orElseGet(() -> Payroll.builder().nhanVien(e).thang(thang).nam(nam).build());
        if ("DA_CHOT".equals(p.getTrangThai())) {
            return p; // da chot thi khong tinh lai
        }
        p.setLuongCoBan(luongCoBan);
        p.setPhuCap(phuCap);
        p.setSoNgayCong(ngayCong);
        p.setLuongGross(gross);
        p.setBaoHiem(baoHiem);
        p.setThueTNCN(thue);
        p.setLuongThucNhan(thucNhan);
        p.setTrangThai("TAM_TINH");
        return payrollRepository.save(p);
    }

    // Thue TNCN luy tien 7 bac (thu nhap tinh thue / thang)
    private BigDecimal thueLuyTien(BigDecimal tntt) {
        double x = tntt.doubleValue();
        double thue;
        if (x <= 5_000_000) thue = x * 0.05;
        else if (x <= 10_000_000) thue = x * 0.10 - 250_000;
        else if (x <= 18_000_000) thue = x * 0.15 - 750_000;
        else if (x <= 32_000_000) thue = x * 0.20 - 1_650_000;
        else if (x <= 52_000_000) thue = x * 0.25 - 3_250_000;
        else if (x <= 80_000_000) thue = x * 0.30 - 5_850_000;
        else thue = x * 0.35 - 9_850_000;
        if (thue < 0) thue = 0;
        return BigDecimal.valueOf(thue).setScale(2, RoundingMode.HALF_UP);
    }

    public PayrollResponse approve(Long id) {
        Payroll p = payrollRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Khong tim thay bang luong"));
        p.setTrangThai("DA_CHOT");
        return toResponse(payrollRepository.save(p));
    }

    public List<PayrollResponse> byMonth(int thang, int nam) {
        return payrollRepository.findByThangAndNam(thang, nam)
                .stream().map(this::toResponse).toList();
    }

    public List<PayrollResponse> mine(Long nhanVienId) {
        return payrollRepository.findByNhanVienIdOrderByNamDescThangDesc(nhanVienId)
                .stream().map(this::toResponse).toList();
    }

    private PayrollResponse toResponse(Payroll p) {
        return new PayrollResponse(
                p.getId(), p.getNhanVien().getId(), p.getNhanVien().getHoTen(),
                p.getThang(), p.getNam(), p.getLuongCoBan(), p.getPhuCap(),
                p.getSoNgayCong(), p.getLuongGross(), p.getBaoHiem(),
                p.getThueTNCN(), p.getLuongThucNhan(), p.getTrangThai());
    }
}
