package com.hrm.service;

import com.hrm.dto.EmployeeDtos.EmployeeRequest;
import com.hrm.dto.EmployeeDtos.EmployeeResponse;
import com.hrm.entity.Department;
import com.hrm.entity.Employee;
import com.hrm.entity.Position;
import com.hrm.exception.BusinessException;
import com.hrm.repository.DepartmentRepository;
import com.hrm.repository.EmployeeRepository;
import com.hrm.repository.PositionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;
    private final PositionRepository positionRepository;

    public Page<EmployeeResponse> search(String keyword, Long phongBanId, Long chucVuId, int page, int size) {
        String kw = (keyword == null || keyword.isBlank()) ? null : keyword.trim();
        Page<Employee> result = employeeRepository.search(kw, phongBanId, chucVuId,
                PageRequest.of(page, size, Sort.by("id").descending()));
        return result.map(this::toResponse);
    }

    public EmployeeResponse getById(Long id) {
        return toResponse(findOrThrow(id));
    }

    public EmployeeResponse create(EmployeeRequest req) {
        Employee e = new Employee();
        apply(e, req);
        return toResponse(employeeRepository.save(e));
    }

    public EmployeeResponse update(Long id, EmployeeRequest req) {
        Employee e = findOrThrow(id);
        apply(e, req);
        return toResponse(employeeRepository.save(e));
    }

    public void deactivate(Long id) {
        Employee e = findOrThrow(id);
        e.setTrangThai("NGHI_VIEC");
        employeeRepository.save(e);
    }

    private Employee findOrThrow(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Khong tim thay nhan vien id=" + id));
    }

    private void apply(Employee e, EmployeeRequest req) {
        e.setHoTen(req.hoTen());
        e.setNgaySinh(req.ngaySinh());
        e.setGioiTinh(req.gioiTinh());
        e.setEmail(req.email());
        e.setSoDienThoai(req.soDienThoai());
        e.setDiaChi(req.diaChi());
        e.setAnhDaiDien(req.anhDaiDien());
        e.setNgayVaoLam(req.ngayVaoLam());
        if (req.trangThai() != null) e.setTrangThai(req.trangThai());
        if (req.soNguoiPhuThuoc() != null) e.setSoNguoiPhuThuoc(req.soNguoiPhuThuoc());

        if (req.phongBanId() != null) {
            Department d = departmentRepository.findById(req.phongBanId())
                    .orElseThrow(() -> new BusinessException("Khong tim thay phong ban"));
            e.setPhongBan(d);
        }
        if (req.chucVuId() != null) {
            Position p = positionRepository.findById(req.chucVuId())
                    .orElseThrow(() -> new BusinessException("Khong tim thay chuc vu"));
            e.setChucVu(p);
        }
    }

    private EmployeeResponse toResponse(Employee e) {
        return new EmployeeResponse(
                e.getId(), e.getHoTen(), e.getNgaySinh(), e.getGioiTinh(), e.getEmail(),
                e.getSoDienThoai(), e.getDiaChi(), e.getAnhDaiDien(), e.getNgayVaoLam(),
                e.getTrangThai(), e.getSoNguoiPhuThuoc(),
                e.getPhongBan() != null ? e.getPhongBan().getId() : null,
                e.getPhongBan() != null ? e.getPhongBan().getTenPhongBan() : null,
                e.getChucVu() != null ? e.getChucVu().getId() : null,
                e.getChucVu() != null ? e.getChucVu().getTenChucVu() : null,
                e.getChucVu() != null ? e.getChucVu().getLuongCoBan() : null
        );
    }
}
