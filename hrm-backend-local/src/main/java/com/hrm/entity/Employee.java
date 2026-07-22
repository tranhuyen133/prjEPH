package com.hrm.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "employees")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String hoTen;

    private LocalDate ngaySinh;

    @Column(length = 10)
    private String gioiTinh;

    @Column(unique = true, length = 150)
    private String email;

    @Column(length = 20)
    private String soDienThoai;

    @Column(length = 255)
    private String diaChi;

    @Column(length = 500)
    private String anhDaiDien;

    private LocalDate ngayVaoLam;

    @Column(nullable = false, length = 20)
    @Builder.Default
    private String trangThai = "DANG_LAM";

    @Column(nullable = false)
    @Builder.Default
    private Integer soNguoiPhuThuoc = 0;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "phong_ban_id")
    private Department phongBan;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "chuc_vu_id")
    private Position chucVu;
}
