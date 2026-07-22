package com.hrm.repository;

import com.hrm.entity.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    Optional<Attendance> findByNhanVienIdAndNgay(Long nhanVienId, LocalDate ngay);

    List<Attendance> findByNhanVienIdAndNgayBetweenOrderByNgayAsc(
            Long nhanVienId, LocalDate tuNgay, LocalDate denNgay);
}
