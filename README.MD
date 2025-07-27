# ISD.20242 GROUP 13
## CAPSTONE PROJECT: AIMS
### MEMBERS

| Name| Student ID| Role|
| - | - | - |
| Phan Sỹ Hùng | 20225631 | Team Leader|
| Trương Công Thái Đức | 20225815 | Member|
| Vũ Ngọc Lâm | 20225645 | Member|
| Nguyễn Mạnh Tuấn | 20225679 | Member|


## 🎵 AIMS – An Internet Media Store

AIMS (An Internet Media Store) là một ứng dụng thương mại điện tử cho phép người dùng mua bán các sản phẩm media vật lý như sách, CD, LP, DVD. Ứng dụng hoạt động 24/7, hỗ trợ thanh toán qua VNPay, có khả năng xử lý tới 1,000 người dùng đồng thời và duy trì vận hành ổn định trong thời gian dài.

---

## 🌐 Tech Stack

| Layer       | Technology           |
|-------------|----------------------|
| Backend     | Spring Boot (Java)   |
| Frontend    | TypeScript + Vite    |
| Database    | MySQL                |
| Payment API | VNPay (Sandbox)      |

---

## 📦 Chức năng chính

### 👨‍💼 Quản lý sản phẩm (Product Manager)
- Thêm, chỉnh sửa, xóa sản phẩm (giới hạn logic áp dụng cho số lượng thao tác)
- Quản lý các thông tin đặc thù của từng loại sản phẩm: Book, CD, LP, DVD
- Lưu lịch sử thao tác và hiển thị thông báo nếu có lỗi định dạng

### 🛒 Khách hàng (Customer)
- Tìm kiếm, lọc, xem chi tiết và thêm sản phẩm vào giỏ hàng
- Xem/Chỉnh sửa giỏ hàng, đặt hàng không cần đăng nhập
- Nhập thông tin giao hàng, lựa chọn giao nhanh (nội thành Hà Nội)
- Thanh toán qua VNPay (sandbox)

### 👨‍💻 Quản trị viên (Admin)
- Tạo, chỉnh sửa, xóa người dùng
- Phân quyền: Product Manager, Admin (nhiều vai trò trên 1 tài khoản)
- Reset mật khẩu, khóa/mở tài khoản

---

## 🧾 Quy trình đặt hàng

1. Khách chọn sản phẩm và thêm vào giỏ hàng
2. Nhập thông tin giao hàng và chọn phương thức giao hàng
3. Xác nhận hóa đơn (VAT, phí ship, tổng tiền)
4. Thanh toán qua VNPay (Sandbox)
5. Nhận email xác nhận và xem/cancel đơn hàng nếu cần

---

## 🚚 Chính sách giao hàng

- Tính phí dựa trên cân nặng và địa điểm giao hàng
- Giao nhanh (2 giờ): chỉ áp dụng cho nội thành Hà Nội
- Miễn phí ship cho đơn hàng > 100.000 VND (tối đa 25k, không áp dụng với giao nhanh)

---

## 💳 Thanh toán (VNPay Sandbox)

- Demo: https://sandbox.vnpayment.vn/apis/vnpay-demo/
- Thanh toán: https://sandbox.vnpayment.vn/apis/docs/thanh-toan-pay/pay.html
- Hoàn tiền: https://sandbox.vnpayment.vn/apis/docs/truy-van-hoan-tien/querydr&refund.html

---

## 📌 Ghi chú phát triển

- Hỗ trợ mở rộng thêm phương thức thanh toán trong tương lai
- Mỗi đơn hàng có thể có nhiều trạng thái: pending, approved, rejected
- Tách xử lý cho giao nhanh và giao thường
- Chuyển đổi giữa roles thông qua trang admin

---

## 🚀 Triển khai & Hướng dẫn chạy

### 📌 Yêu cầu hệ thống

- Java 21 trở lên
- Node.js 18.x hoặc mới hơn
- MySQL 8.x
- Maven (nếu không dùng wrapper)
- Tài khoản VNPay Sandbox (để test thanh toán)

---

### 1️⃣ Cài đặt & cấu hình cơ sở dữ liệu (MySQL)

1. Khởi chạy MySQL server
2. Tạo cơ sở dữ liệu:
   ```sql
   CREATE DATABASE aims_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
   ```
3. Nhập dữ liệu và bảng ban đầu:
   ```bash
   mysql -u root -p aims_db < sql/aims_schema.sql
   ```

---

### 2️⃣ Chạy backend (Spring Boot)

1. Di chuyển vào thư mục `backend/`
2. Cập nhật file `src/main/resources/application.properties`:
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/aims_db
   spring.datasource.username=YOUR_DB_USERNAME
   spring.datasource.password=YOUR_DB_PASSWORD

   vnpay.returnUrl=http://localhost:5173/payment-return
   ```
3. Khởi chạy server backend:
   ```bash
   ./mvnw spring-boot:run
   ```
   Hoặc nếu bạn đã cài Maven toàn cục:
   ```bash
   mvn spring-boot:run
   ```

📍 Mặc định, backend chạy tại: `http://localhost:8080`

---

### 3️⃣ Chạy frontend (Vite + TypeScript)

1. Di chuyển vào thư mục `frontend/`
2. Cài đặt các dependencies:
   ```bash
   npm install
   ```
3. (Tuỳ chọn) Cập nhật endpoint backend trong file cấu hình (ví dụ: `.env` hoặc `src/config.ts`)
4. Khởi chạy frontend:
   ```bash
   npm run dev
   ```

📍 Mặc định, frontend chạy tại: `http://localhost:5173`
