# TÀI LIỆU ĐẶC TẢ YÊU CẦU PHẦN MỀM (SRS)

## HỆ THỐNG QUẢN LÝ NHÂN SỰ (HRM SYSTEM - EPH)

---

### THÔNG TIN TÀI LIỆU (DOCUMENT CONTROL)

| Mục | Chi tiết |
| --- | --- |
| **Tên dự án** | Hệ thống Quản lý nhân sự (HRM Backend) |
| **Mã dự án** | EPH |
| **Repository** | tranhuyen133/EPH |
| **Phiên bản** | 1.0.0 |
| **Nền tảng** | Spring Boot 3.3.2 / Java 17 |
| **Ngày ban hành** | 14/07/2026 |

---

## I. GIỚI THIỆU

### 1.1 Mục đích

Tài liệu này mô tả các yêu cầu chức năng, phi chức năng và thiết kế kỹ thuật của **Hệ thống Quản lý nhân sự (EPH)** — phần backend được xây dựng bằng Spring Boot. Tài liệu dùng làm cơ sở cho phát triển, kiểm thử và bàn giao hệ thống.

### 1.2 Phạm vi hệ thống

Hệ thống cung cấp REST API phục vụ các nghiệp vụ quản lý nhân sự:

- **Trong phạm vi (In-Scope):**
  - Xác thực và phân quyền người dùng bằng JWT.
  - Quản lý hồ sơ nhân viên (CRUD, tìm kiếm, lọc, phân trang).
  - Quản lý cơ cấu tổ chức: Phòng ban (Department) và Chức vụ (Position).
  - Chấm công (Check-in / Check-out, tổng hợp bảng công theo tháng).
  - Quản lý đơn nghỉ phép (gửi đơn, xem đơn của mình, duyệt/từ chối).
  - Quản lý hợp đồng lao động.
  - Tính lương hàng tháng (lương cơ bản, phụ cấp, bảo hiểm, thuế TNCN, thực nhận).
  - Báo cáo thống kê.
- **Ngoài phạm vi (Out-of-Scope):**
  - Giao diện frontend (repo hiện chỉ có backend).
  - Tuyển dụng, đánh giá hiệu suất (KPI/OKR), đào tạo.
  - Thanh toán qua cổng ngân hàng.

### 1.3 Định nghĩa & Từ viết tắt

| Thuật ngữ | Định nghĩa |
| --- | --- |
| **HRM** | Human Resource Management — Quản lý nguồn nhân lực. |
| **JWT** | JSON Web Token — cơ chế xác thực API. |
| **RBAC** | Role-Based Access Control — phân quyền theo vai trò. |
| **JPA** | Java Persistence API — ánh xạ đối tượng - quan hệ (ORM). |
| **DTO** | Data Transfer Object — đối tượng truyền dữ liệu qua API. |
| **TNCN** | Thuế thu nhập cá nhân. |

---

## II. MÔ TẢ TỔNG QUAN

### 2.1 Công nghệ sử dụng

| Thành phần | Công nghệ |
| --- | --- |
| Ngôn ngữ | Java 17 |
| Framework | Spring Boot 3.3.2 |
| Web | Spring Web (REST API) |
| Truy cập dữ liệu | Spring Data JPA (Hibernate) |
| Bảo mật | Spring Security + JWT (jjwt 0.12.6) |
| Kiểm tra dữ liệu | Spring Boot Validation |
| Cơ sở dữ liệu | MySQL (mysql-connector-j) |
| Tiện ích | Lombok |
| Build tool | Maven |

### 2.2 Tác nhân & Phân quyền (RBAC)

Hệ thống định nghĩa 3 vai trò trong enum `Role`:

| Vai trò | Mô tả | Quyền chính |
| --- | --- | --- |
| **ROLE_ADMIN** | Quản trị viên / HR | Toàn quyền: CRUD nhân viên, cấu hình tổ chức, tính & chốt lương. |
| **ROLE_MANAGER** | Trưởng phòng / Quản lý | Xem nhân viên, duyệt đơn nghỉ, xem bảng lương. |
| **ROLE_EMPLOYEE** | Nhân viên | Chấm công, gửi đơn nghỉ, xem phiếu lương và bảng công cá nhân. |

### 2.3 Kiến trúc theo lớp (Layered Architecture)

Mã nguồn tổ chức theo package chuẩn Spring Boot trong `com.hrm`:

```
com.hrm
├── config        # Cấu hình ứng dụng
├── controller    # REST Controller (tầng API)
├── dto           # Data Transfer Objects
├── entity        # Thực thể JPA (ánh xạ bảng CSDL)
├── exception     # Xử lý ngoại lệ
├── repository    # Spring Data JPA repositories
├── security      # JWT filter, cấu hình Spring Security
├── service       # Nghiệp vụ (business logic)
└── HrmBackendApplication.java   # Entry point
```

---

## III. YÊU CẦU CHỨC NĂNG

Danh sách controller và endpoint tương ứng với code thực tế:

| Nhóm | Controller | Base Path |
| --- | --- | --- |
| Xác thực | `AuthController` | `/api/v1/auth` |
| Nhân viên | `EmployeeController` | `/api/v1/employees` |
| Tổ chức | `OrgController` | (phòng ban / chức vụ) |
| Chấm công | `AttendanceController` | `/api/v1/attendance` |
| Nghỉ phép | `LeaveController` | `/api/v1/leaves` |
| Hợp đồng | `ContractController` | (hợp đồng) |
| Tính lương | `PayrollController` | `/api/v1/payroll` |
| Báo cáo | `ReportController` | (thống kê) |

### 3.1 Bảng phân tích yêu cầu chức năng

| ID | Chức năng | Mô tả | Quyền |
| --- | --- | --- | --- |
| **FR-1** | Đăng nhập | Xác thực username/password, trả về JWT access token. | Tất cả |
| **FR-2.1** | Tìm kiếm nhân viên | Tìm theo keyword, lọc theo phòng ban / chức vụ, phân trang. | Đã đăng nhập |
| **FR-2.2** | Xem chi tiết nhân viên | Lấy thông tin nhân viên theo ID. | Đã đăng nhập |
| **FR-2.3** | Thêm / sửa / vô hiệu hóa nhân viên | CRUD hồ sơ nhân viên. | ADMIN |
| **FR-3.1** | Chấm công vào ca | Check-in cho nhân viên đang đăng nhập. | Đã đăng nhập |
| **FR-3.2** | Chấm công ra ca | Check-out, tính tổng giờ làm. | Đã đăng nhập |
| **FR-3.3** | Xem bảng công | Xem chấm công theo tháng của mình hoặc theo nhân viên. | Đã đăng nhập |
| **FR-4.1** | Gửi đơn nghỉ phép | Nhân viên tạo đơn nghỉ (loại phép, từ ngày, đến ngày, lý do). | Đã đăng nhập |
| **FR-4.2** | Xem đơn của mình | Danh sách đơn nghỉ của nhân viên đang đăng nhập. | Đã đăng nhập |
| **FR-4.3** | Xem đơn chờ duyệt | Danh sách đơn ở trạng thái chờ duyệt. | ADMIN, MANAGER |
| **FR-4.4** | Duyệt / từ chối đơn | Cập nhật trạng thái đơn kèm phản hồi. | ADMIN, MANAGER |
| **FR-5** | Quản lý hợp đồng | Lưu và quản lý hợp đồng lao động. | ADMIN |
| **FR-6.1** | Tính lương tháng | Tính lương cho toàn bộ nhân viên theo tháng/năm. | ADMIN, MANAGER |
| **FR-6.2** | Chốt bảng lương | Phê duyệt bảng lương của một nhân viên. | ADMIN |
| **FR-6.3** | Xem bảng lương | Xem theo tháng (quản lý) hoặc phiếu lương cá nhân. | ADMIN/MANAGER / Nhân viên |
| **FR-7** | Báo cáo thống kê | Số liệu tổng hợp nhân sự / lương. | ADMIN, MANAGER |

---

## IV. ĐẶC TẢ API ENDPOINTS

### 4.1 Xác thực — `/api/v1/auth`

| Method | Endpoint | Mô tả | Quyền |
| --- | --- | --- | --- |
| `POST` | `/login` | Đăng nhập, trả về JWT. | Public |

**Request:**

```json
{
  "username": "admin",
  "password": "password123"
}
```

### 4.2 Nhân viên — `/api/v1/employees`

| Method | Endpoint | Mô tả | Quyền |
| --- | --- | --- | --- |
| `GET` | `/` | Tìm kiếm & lọc nhân viên (phân trang). Params: `keyword`, `phongBanId`, `chucVuId`, `page`, `size`. | Đã đăng nhập |
| `GET` | `/{id}` | Lấy nhân viên theo ID. | Đã đăng nhập |
| `POST` | `/` | Thêm nhân viên mới. | ADMIN |
| `PUT` | `/{id}` | Cập nhật nhân viên. | ADMIN |
| `DELETE` | `/{id}` | Vô hiệu hóa nhân viên. | ADMIN |

### 4.3 Chấm công — `/api/v1/attendance`

| Method | Endpoint | Mô tả | Quyền |
| --- | --- | --- | --- |
| `POST` | `/check-in` | Check-in cho nhân viên hiện tại. | Đã đăng nhập |
| `POST` | `/check-out` | Check-out cho nhân viên hiện tại. | Đã đăng nhập |
| `GET` | `/me` | Bảng công của mình theo `thang`, `nam` (mặc định tháng hiện tại). | Đã đăng nhập |
| `GET` | `/{nhanVienId}` | Bảng công của một nhân viên theo tháng/năm. | Đã đăng nhập |

### 4.4 Nghỉ phép — `/api/v1/leaves`

| Method | Endpoint | Mô tả | Quyền |
| --- | --- | --- | --- |
| `POST` | `/request` | Gửi đơn nghỉ phép. | Đã đăng nhập |
| `GET` | `/me` | Danh sách đơn của mình. | Đã đăng nhập |
| `GET` | `/pending` | Danh sách đơn chờ duyệt. | ADMIN, MANAGER |
| `PUT` | `/approve/{id}` | Duyệt / từ chối đơn (kèm phản hồi). | ADMIN, MANAGER |

### 4.5 Tính lương — `/api/v1/payroll`

| Method | Endpoint | Mô tả | Quyền |
| --- | --- | --- | --- |
| `POST` | `/calculate` | Tính lương toàn bộ nhân viên theo `thang`, `nam`. | ADMIN, MANAGER |
| `PUT` | `/{id}/approve` | Chốt bảng lương của một nhân viên. | ADMIN |
| `GET` | `/` | Xem bảng lương theo `thang`, `nam`. | ADMIN, MANAGER |
| `GET` | `/me` | Xem phiếu lương cá nhân. | Đã đăng nhập |

---

## V. MÔ HÌNH DỮ LIỆU (DATABASE SCHEMA)

Các thực thể JPA thực tế trong package `com.hrm.entity`.

### 5.1 Sơ đồ quan hệ (ERD)

```
erDiagram
    DEPARTMENT ||--o{ EMPLOYEE : "có"
    POSITION   ||--o{ EMPLOYEE : "giữ"
    EMPLOYEE   ||--o{ ATTENDANCE : "chấm công"
    EMPLOYEE   ||--o{ LEAVE_REQUEST : "gửi đơn"
    EMPLOYEE   ||--o{ CONTRACT : "ký"
    EMPLOYEE   ||--o{ PAYROLL : "nhận lương"
    EMPLOYEE   ||--|| USER : "đăng nhập"
    EMPLOYEE   ||--o{ LEAVE_REQUEST : "duyệt (nguoiDuyet)"

    EMPLOYEE {
        Long id PK
        String hoTen
        LocalDate ngaySinh
        String gioiTinh
        String email "unique"
        String soDienThoai
        String diaChi
        String anhDaiDien
        LocalDate ngayVaoLam
        String trangThai "mặc định DANG_LAM"
        Integer soNguoiPhuThuoc "mặc định 0"
        Long phong_ban_id FK
        Long chuc_vu_id FK
    }

    DEPARTMENT {
        Long id PK
        String tenPhongBan
        String moTa
    }

    POSITION {
        Long id PK
        String tenChucVu
        BigDecimal luongCoBan
        BigDecimal phuCap "mặc định 0"
    }

    ATTENDANCE {
        Long id PK
        Long nhan_vien_id FK
        LocalDate ngay
        LocalTime gioVao
        LocalTime gioRa
        Double soGioLam "mặc định 0.0"
        boolean diTre "mặc định false"
        boolean veSom "mặc định false"
    }

    LEAVE_REQUEST {
        Long id PK
        Long nhan_vien_id FK
        String loaiPhep
        LocalDate tuNgay
        LocalDate denNgay
        String lyDo
        LeaveStatus trangThai "CHO_DUYET / DA_DUYET / TU_CHOI"
        String phanHoi
        Long nguoi_duyet_id FK
    }

    CONTRACT {
        Long id PK
        Long nhan_vien_id FK
        String soHopDong
        String loaiHopDong
        LocalDate tuNgay
        LocalDate denNgay
        BigDecimal luong
        String trangThai "mặc định HIEU_LUC"
    }

    PAYROLL {
        Long id PK
        Long nhan_vien_id FK
        Integer thang
        Integer nam
        BigDecimal luongCoBan
        BigDecimal phuCap
        Double soNgayCong
        BigDecimal luongGross
        BigDecimal baoHiem
        BigDecimal thueTNCN
        BigDecimal luongThucNhan
        String trangThai "mặc định TAM_TINH"
    }

    USER {
        Long id PK
        String username "unique"
        String password
        Role role "ROLE_ADMIN / ROLE_MANAGER / ROLE_EMPLOYEE"
        boolean enabled "mặc định true"
        Long nhan_vien_id FK "unique"
    }
```

### 5.2 Từ điển dữ liệu

#### 5.2.1 `employees` (Employee)

| Trường | Kiểu | Ràng buộc | Mô tả |
| --- | --- | --- | --- |
| `id` | BIGINT | PK, IDENTITY | Khóa chính. |
| `hoTen` | VARCHAR(150) | NOT NULL | Họ tên đầy đủ. |
| `ngaySinh` | DATE | | Ngày sinh. |
| `gioiTinh` | VARCHAR(10) | | Giới tính. |
| `email` | VARCHAR(150) | UNIQUE | Email. |
| `soDienThoai` | VARCHAR(20) | | Số điện thoại. |
| `diaChi` | VARCHAR(255) | | Địa chỉ. |
| `anhDaiDien` | VARCHAR(500) | | Đường dẫn ảnh đại diện. |
| `ngayVaoLam` | DATE | | Ngày vào làm. |
| `trangThai` | VARCHAR(20) | NOT NULL, default `DANG_LAM` | Trạng thái làm việc. |
| `soNguoiPhuThuoc` | INT | NOT NULL, default `0` | Số người phụ thuộc (tính giảm trừ thuế). |
| `phong_ban_id` | BIGINT | FK → `departments(id)` | Phòng ban. |
| `chuc_vu_id` | BIGINT | FK → `positions(id)` | Chức vụ. |

#### 5.2.2 `departments` (Department)

| Trường | Kiểu | Ràng buộc | Mô tả |
| --- | --- | --- | --- |
| `id` | BIGINT | PK, IDENTITY | Khóa chính. |
| `tenPhongBan` | VARCHAR(150) | NOT NULL | Tên phòng ban. |
| `moTa` | VARCHAR(500) | | Mô tả. |

#### 5.2.3 `positions` (Position)

| Trường | Kiểu | Ràng buộc | Mô tả |
| --- | --- | --- | --- |
| `id` | BIGINT | PK, IDENTITY | Khóa chính. |
| `tenChucVu` | VARCHAR(150) | NOT NULL | Tên chức vụ. |
| `luongCoBan` | DECIMAL(15,2) | NOT NULL | Lương cơ bản. |
| `phuCap` | DECIMAL(15,2) | default `0` | Phụ cấp chức vụ. |

#### 5.2.4 `attendances` (Attendance)

| Trường | Kiểu | Ràng buộc | Mô tả |
| --- | --- | --- | --- |
| `id` | BIGINT | PK, IDENTITY | Khóa chính. |
| `nhan_vien_id` | BIGINT | FK, NOT NULL | Nhân viên chấm công. |
| `ngay` | DATE | NOT NULL | Ngày chấm công. |
| `gioVao` | TIME | | Giờ check-in. |
| `gioRa` | TIME | | Giờ check-out. |
| `soGioLam` | DOUBLE | default `0.0` | Tổng giờ làm. |
| `diTre` | BOOLEAN | default `false` | Đánh dấu đi muộn. |
| `veSom` | BOOLEAN | default `false` | Đánh dấu về sớm. |

> Ràng buộc UNIQUE: `(nhan_vien_id, ngay)` — mỗi nhân viên chỉ có 1 bản ghi chấm công/ngày.

#### 5.2.5 `leave_requests` (LeaveRequest)

| Trường | Kiểu | Ràng buộc | Mô tả |
| --- | --- | --- | --- |
| `id` | BIGINT | PK, IDENTITY | Khóa chính. |
| `nhan_vien_id` | BIGINT | FK, NOT NULL | Nhân viên gửi đơn. |
| `loaiPhep` | VARCHAR(30) | NOT NULL | Loại nghỉ phép. |
| `tuNgay` | DATE | NOT NULL | Từ ngày. |
| `denNgay` | DATE | NOT NULL | Đến ngày. |
| `lyDo` | VARCHAR(500) | | Lý do. |
| `trangThai` | ENUM | NOT NULL, default `CHO_DUYET` | `CHO_DUYET` / `DA_DUYET` / `TU_CHOI`. |
| `phanHoi` | VARCHAR(500) | | Phản hồi người duyệt. |
| `nguoi_duyet_id` | BIGINT | FK → `employees(id)` | Người duyệt. |

#### 5.2.6 `contracts` (Contract)

| Trường | Kiểu | Ràng buộc | Mô tả |
| --- | --- | --- | --- |
| `id` | BIGINT | PK, IDENTITY | Khóa chính. |
| `nhan_vien_id` | BIGINT | FK, NOT NULL | Nhân viên. |
| `soHopDong` | VARCHAR(50) | NOT NULL | Số hợp đồng. |
| `loaiHopDong` | VARCHAR(50) | NOT NULL | Loại hợp đồng. |
| `tuNgay` | DATE | NOT NULL | Ngày bắt đầu. |
| `denNgay` | DATE | | Ngày kết thúc. |
| `luong` | DECIMAL(15,2) | | Lương thỏa thuận. |
| `trangThai` | VARCHAR(20) | NOT NULL, default `HIEU_LUC` | Trạng thái hợp đồng. |

#### 5.2.7 `payrolls` (Payroll)

| Trường | Kiểu | Ràng buộc | Mô tả |
| --- | --- | --- | --- |
| `id` | BIGINT | PK, IDENTITY | Khóa chính. |
| `nhan_vien_id` | BIGINT | FK, NOT NULL | Nhân viên. |
| `thang` | INT | NOT NULL | Tháng tính lương. |
| `nam` | INT | NOT NULL | Năm tính lương. |
| `luongCoBan` | DECIMAL(15,2) | | Lương cơ bản. |
| `phuCap` | DECIMAL(15,2) | | Phụ cấp. |
| `soNgayCong` | DOUBLE | | Số ngày công. |
| `luongGross` | DECIMAL(15,2) | | Lương gross. |
| `baoHiem` | DECIMAL(15,2) | | Khấu trừ bảo hiểm. |
| `thueTNCN` | DECIMAL(15,2) | | Thuế TNCN. |
| `luongThucNhan` | DECIMAL(15,2) | | Lương thực nhận. |
| `trangThai` | VARCHAR(20) | NOT NULL, default `TAM_TINH` | Trạng thái bảng lương. |

> Ràng buộc UNIQUE: `(nhan_vien_id, thang, nam)` — mỗi nhân viên chỉ có 1 bảng lương/tháng.

#### 5.2.8 `users` (User)

| Trường | Kiểu | Ràng buộc | Mô tả |
| --- | --- | --- | --- |
| `id` | BIGINT | PK, IDENTITY | Khóa chính. |
| `username` | VARCHAR(100) | UNIQUE, NOT NULL | Tên đăng nhập. |
| `password` | VARCHAR | NOT NULL | Mật khẩu (mã hóa). |
| `role` | ENUM | NOT NULL | `ROLE_ADMIN` / `ROLE_MANAGER` / `ROLE_EMPLOYEE`. |
| `enabled` | BOOLEAN | default `true` | Trạng thái kích hoạt. |
| `nhan_vien_id` | BIGINT | FK, UNIQUE | Liên kết 1-1 với nhân viên. |

---

## VI. YÊU CẦU PHI CHỨC NĂNG

### 6.1 Bảo mật

- Xác thực bằng **JWT** (thư viện jjwt 0.12.6); token đính kèm ở header `Authorization: Bearer <token>`.
- Phân quyền theo **RBAC** tại tầng controller bằng `@PreAuthorize` (ví dụ `hasRole('ADMIN')`, `hasAnyRole('ADMIN','MANAGER')`).
- Mật khẩu được mã hóa trước khi lưu (Spring Security).
- Nhân viên chỉ truy cập dữ liệu của mình qua các endpoint `/me`; các thao tác quản trị bị chặn theo vai trò.

### 6.2 Dữ liệu & Toàn vẹn

- Ràng buộc UNIQUE đảm bảo không trùng chấm công trong ngày và không trùng bảng lương trong tháng.
- Sử dụng JPA/Hibernate ánh xạ entity → bảng MySQL.
- Kiểm tra dữ liệu đầu vào bằng Spring Boot Validation.

### 6.3 Khả năng bảo trì

- Cấu trúc phân lớp rõ ràng (controller / service / repository / entity / dto).
- Dùng Lombok giảm mã lặp (getter/setter/builder).

---

## VII. HƯỚNG DẪN CHẠY DỰ ÁN

### 7.1 Yêu cầu môi trường

- JDK 17
- Maven
- MySQL

### 7.2 Cấu hình

Cập nhật thông tin kết nối CSDL trong `src/main/resources/application.properties` (hoặc `application.yml`): URL, username, password MySQL và khóa bí mật JWT.

### 7.3 Build & Run

```bash
# Build
mvn clean package

# Chạy ứng dụng
mvn spring-boot:run
```

Ứng dụng chạy mặc định tại `http://localhost:8080`, các API dưới tiền tố `/api/v1`.

---

## VIII. CẤU TRÚC THƯ MỤC

```
EPH/
├── pom.xml
└── src/main
    ├── java/com/hrm
    │   ├── config/
    │   ├── controller/
    │   │   ├── AttendanceController.java
    │   │   ├── AuthController.java
    │   │   ├── ContractController.java
    │   │   ├── EmployeeController.java
    │   │   ├── LeaveController.java
    │   │   ├── OrgController.java
    │   │   ├── PayrollController.java
    │   │   └── ReportController.java
    │   ├── dto/
    │   ├── entity/
    │   │   ├── Attendance.java
    │   │   ├── Contract.java
    │   │   ├── Department.java
    │   │   ├── Employee.java
    │   │   ├── LeaveRequest.java
    │   │   ├── LeaveStatus.java
    │   │   ├── Payroll.java
    │   │   ├── Position.java
    │   │   ├── Role.java
    │   │   └── User.java
    │   ├── exception/
    │   ├── repository/
    │   ├── security/
    │   ├── service/
    │   └── HrmBackendApplication.java
    └── resources/
```
