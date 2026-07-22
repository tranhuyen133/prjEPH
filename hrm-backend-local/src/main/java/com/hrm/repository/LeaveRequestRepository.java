package com.hrm.repository;

import com.hrm.entity.LeaveRequest;
import com.hrm.entity.LeaveStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Long> {

    List<LeaveRequest> findByNhanVienIdOrderByIdDesc(Long nhanVienId);

    List<LeaveRequest> findByTrangThaiOrderByIdDesc(LeaveStatus trangThai);

    List<LeaveRequest> findByNhanVienIdAndTrangThai(Long nhanVienId, LeaveStatus trangThai);

    List<LeaveRequest> findByNhanVienIdAndTrangThaiAndTuNgayLessThanEqualAndDenNgayGreaterThanEqual(
            Long nhanVienId, LeaveStatus trangThai, LocalDate denNgay, LocalDate tuNgay);
}
