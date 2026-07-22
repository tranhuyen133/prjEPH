package com.hrm.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "contracts")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Contract {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "nhan_vien_id")
    private Employee nhanVien;

    @Column(nullable = false, length = 50)
    private String soHopDong;

    @Column(nullable = false, length = 50)
    private String loaiHopDong;

    @Column(nullable = false)
    private LocalDate tuNgay;

    private LocalDate denNgay;

    @Column(precision = 15, scale = 2)
    private BigDecimal luong;

    @Column(nullable = false, length = 20)
    @Builder.Default
    private String trangThai = "HIEU_LUC";
}
