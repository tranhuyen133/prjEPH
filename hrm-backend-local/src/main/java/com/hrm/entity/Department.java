package com.hrm.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "departments")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Department {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String tenPhongBan;

    @Column(length = 500)
    private String moTa;

    @OneToMany(mappedBy = "phongBan")
    @Builder.Default
    private Set<Employee> nhanViens = new HashSet<>();
}
