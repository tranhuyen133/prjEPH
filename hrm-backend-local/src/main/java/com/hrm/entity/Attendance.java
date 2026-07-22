package com.hrm.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "attendances",
       uniqueConstraints = @UniqueConstraint(columnNames = {"nhan_vien_id", "ngay"}))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Attendance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "nhan_vien_id")
    private Employee nhanVien;

    @Column(nullable = false)
    private LocalDate ngay;

    private LocalTime gioVao;
    private LocalTime gioRa;

    @Builder.Default
    private Double soGioLam = 0.0;

    @Builder.Default
    private boolean diTre = false;

    @Builder.Default
    private boolean veSom = false;
}
