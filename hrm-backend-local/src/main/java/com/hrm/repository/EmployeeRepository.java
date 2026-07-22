package com.hrm.repository;

import com.hrm.entity.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    @Query("""
        SELECT e FROM Employee e
        WHERE (:keyword IS NULL OR LOWER(e.hoTen) LIKE LOWER(CONCAT('%', :keyword, '%'))
               OR LOWER(e.email) LIKE LOWER(CONCAT('%', :keyword, '%')))
          AND (:phongBanId IS NULL OR e.phongBan.id = :phongBanId)
          AND (:chucVuId IS NULL OR e.chucVu.id = :chucVuId)
        """)
    Page<Employee> search(@Param("keyword") String keyword,
                          @Param("phongBanId") Long phongBanId,
                          @Param("chucVuId") Long chucVuId,
                          Pageable pageable);

    long countByTrangThai(String trangThai);

    List<Employee> findByPhongBanId(Long phongBanId);
}
