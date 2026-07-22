package com.hrm.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "payrolls",
       uniqueConstraints = @UniqueConstraint(columnNames = {"nhan_vien_id", "thang", "nam"}))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Payroll {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "nhan_vien_id")
    private Employee nhanVien;

    @Column(nullable = false)
    private Integer thang;

    @Column(nullable = false)
    private Integer nam;

    @Column(precision = 15, scale = 2)
    private BigDecimal luongCoBan;

    @Column(precision = 15, scale = 2)
    private BigDecimal phuCap;

    private Double soNgayCong;

    @Column(precision = 15, scale = 2)
    private BigDecimal luongGross;

    @Column(precision = 15, scale = 2)
    private BigDecimal baoHiem;

    @Column(precision = 15, scale = 2)
    private BigDecimal thueTNCN;

    @Column(precision = 15, scale = 2)
    private BigDecimal luongThucNhan;

    @Column(nullable = false, length = 20)
    @Builder.Default
    private String trangThai = "TAM_TINH";
}
