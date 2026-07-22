package com.hrm.repository;

import com.hrm.entity.Payroll;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PayrollRepository extends JpaRepository<Payroll, Long> {

    List<Payroll> findByThangAndNam(Integer thang, Integer nam);

    Optional<Payroll> findByNhanVienIdAndThangAndNam(Long nhanVienId, Integer thang, Integer nam);

    List<Payroll> findByNhanVienIdOrderByNamDescThangDesc(Long nhanVienId);
}
