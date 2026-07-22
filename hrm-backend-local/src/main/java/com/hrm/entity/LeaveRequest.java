package com.hrm.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "leave_requests")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class LeaveRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "nhan_vien_id")
    private Employee nhanVien;

    @Column(nullable = false, length = 30)
    private String loaiPhep;

    @Column(nullable = false)
    private LocalDate tuNgay;

    @Column(nullable = false)
    private LocalDate denNgay;

    @Column(length = 500)
    private String lyDo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private LeaveStatus trangThai = LeaveStatus.CHO_DUYET;

    @Column(length = 500)
    private String phanHoi;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nguoi_duyet_id")
    private Employee nguoiDuyet;
}
