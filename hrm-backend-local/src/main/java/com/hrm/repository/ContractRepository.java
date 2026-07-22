package com.hrm.repository;

import com.hrm.entity.Contract;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface ContractRepository extends JpaRepository<Contract, Long> {

    List<Contract> findByNhanVienIdOrderByTuNgayDesc(Long nhanVienId);

    List<Contract> findByDenNgayBetween(LocalDate tuNgay, LocalDate denNgay);
}
