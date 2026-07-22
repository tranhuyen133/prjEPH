package com.hrm.config;

import com.hrm.entity.*;
import com.hrm.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;
    private final PositionRepository positionRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (userRepository.count() > 0) return;

        Department it = departmentRepository.save(Department.builder()
                .tenPhongBan("Phong Ky thuat").moTa("Phat trien phan mem").build());
        Department hr = departmentRepository.save(Department.builder()
                .tenPhongBan("Phong Nhan su").moTa("Quan ly nhan su").build());

        Position dev = positionRepository.save(Position.builder()
                .tenChucVu("Lap trinh vien").luongCoBan(new BigDecimal("15000000"))
                .phuCap(new BigDecimal("2000000")).build());
        Position mgr = positionRepository.save(Position.builder()
                .tenChucVu("Truong phong").luongCoBan(new BigDecimal("25000000"))
                .phuCap(new BigDecimal("5000000")).build());
        Position hrStaff = positionRepository.save(Position.builder()
                .tenChucVu("Chuyen vien nhan su").luongCoBan(new BigDecimal("12000000"))
                .phuCap(new BigDecimal("1500000")).build());

        Employee adminEmp = employeeRepository.save(Employee.builder()
                .hoTen("Quan Tri Vien").email("admin@hrm.com").gioiTinh("Nam")
                .soDienThoai("0900000001").ngayVaoLam(LocalDate.of(2020, 1, 1))
                .phongBan(hr).chucVu(mgr).soNguoiPhuThuoc(0).build());
        Employee mgrEmp = employeeRepository.save(Employee.builder()
                .hoTen("Tran Van Manager").email("manager@hrm.com").gioiTinh("Nam")
                .soDienThoai("0900000002").ngayVaoLam(LocalDate.of(2021, 3, 1))
                .phongBan(it).chucVu(mgr).soNguoiPhuThuoc(1).build());
        Employee nvEmp = employeeRepository.save(Employee.builder()
                .hoTen("Le Thi Nhan Vien").email("nhanvien@hrm.com").gioiTinh("Nu")
                .soDienThoai("0900000003").ngayVaoLam(LocalDate.of(2022, 6, 15))
                .phongBan(it).chucVu(dev).soNguoiPhuThuoc(2).build());

        userRepository.save(User.builder().username("admin")
                .password(passwordEncoder.encode("123456"))
                .role(Role.ROLE_ADMIN).enabled(true).nhanVien(adminEmp).build());
        userRepository.save(User.builder().username("manager")
                .password(passwordEncoder.encode("123456"))
                .role(Role.ROLE_MANAGER).enabled(true).nhanVien(mgrEmp).build());
        userRepository.save(User.builder().username("nhanvien")
                .password(passwordEncoder.encode("123456"))
                .role(Role.ROLE_EMPLOYEE).enabled(true).nhanVien(nvEmp).build());

        System.out.println(">>> DataSeeder: da tao 3 tai khoan mau (admin/manager/nhanvien - mat khau 123456)");
    }
}
