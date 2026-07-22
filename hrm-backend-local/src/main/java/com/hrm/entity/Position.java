package com.hrm.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "positions")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Position {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String tenChucVu;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal luongCoBan;

    @Column(precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal phuCap = BigDecimal.ZERO;
}
