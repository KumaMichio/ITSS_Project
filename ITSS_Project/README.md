# ITSS Project - E-commerce Web Application

Ứng dụng thương mại điện tử bán sách, CD và DVD được xây dựng với Spring Boot (Backend) và React (Frontend).

## 🛠️ Công nghệ sử dụng

### Backend
- **Java 21** (hoặc Java 17+)
- **Spring Boot 3.4.4**
- **Spring Data JPA**
- **MySQL Database**
- **VNPay Payment Integration**

### Frontend
- **React 18**
- **TypeScript**
- **Tailwind CSS**
- **Vite**
- **React Router Dom**

## 📋 Yêu cầu hệ thống

- **Java 21** (khuyến nghị) hoặc Java 17+
- **Node.js 18+**
- **MySQL 8.0+**
- **Git**

## 🚀 Hướng dẫn cài đặt

### 1. Clone Repository
```powershell
git clone <repository-url>
cd ITSS_Project
```

### 2. Cài đặt MySQL
1. Tải và cài đặt MySQL từ: https://dev.mysql.com/downloads/
2. Hoặc sử dụng XAMPP: https://www.apachefriends.org/

### 3. Tạo Database
```sql
CREATE DATABASE AIMS_database;
USE AIMS_database;
-- Import schema từ file AIMS_database.sql
```

### 4. Cấu hình Backend

#### a. Cập nhật application.yml
```yaml
# Backend/Project_ITSS/Project_ITSS/src/main/resources/application.YML
spring:
  application:
    name: Project_ITSS
  datasource:
    url: jdbc:mysql://localhost:3306/AIMS_database?useSSL=false&serverTimezone=UTC
    username: root
    password: your_mysql_password  # Thay bằng password MySQL của bạn
    driver-class-name: com.mysql.cj.jdbc.Driver
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
    properties:
      hibernate:
        dialect: org.hibernate.dialect.MySQLDialect
```

#### b. Import Database Schema
Chạy script `AIMS_database.sql` trong MySQL Workbench hoặc command line.

### 5. Cài đặt Dependencies

#### Backend
```powershell
cd Backend/Project_ITSS/Project_ITSS
./mvnw clean install
```

#### Frontend
```powershell
cd Frontend
npm install
```

## 🎯 Chạy ứng dụng

### Cách 1: Chạy riêng biệt

#### Backend (Terminal 1)
```powershell
cd Backend/Project_ITSS/Project_ITSS
./mvnw spring-boot:run
```
Backend sẽ chạy tại: http://localhost:8080

#### Frontend (Terminal 2)
```powershell
cd Frontend
npm run dev
```
Frontend sẽ chạy tại: http://localhost:5173

### Cách 2: Chạy cùng lúc (Khuyến nghị)
```powershell
cd Frontend
npm run start:all
```

## 🔍 Kiểm tra kết nối

### Test Backend API
```powershell
# Trong Frontend directory
npm run test:api
```

### Hoặc test thủ công
- Backend: http://localhost:8080/api/products
- Frontend: http://localhost:5173

## 📁 Cấu trúc Project

```
ITSS_Project/
├── AIMS_database.sql           # Database schema
├── Backend/
│   └── Project_ITSS/
│       └── Project_ITSS/
│           ├── src/main/java/
│           │   ├── controllers/        # REST Controllers
│           │   ├── models/            # JPA Entities
│           │   ├── repositories/      # Data Repositories
│           │   ├── services/          # Business Logic
│           │   └── config/            # Configuration
│           └── src/main/resources/
│               └── application.YML    # App Configuration
└── Frontend/
    ├── src/
    │   ├── components/       # React Components
    │   ├── views/           # Pages
    │   ├── services/        # API Services
    │   ├── contexts/        # React Contexts
    │   ├── hooks/           # Custom Hooks
    │   └── types/           # TypeScript Types
    ├── public/              # Static Assets
    └── package.json
```

## 🔧 API Endpoints

### Products
- `GET /api/products` - Lấy tất cả sản phẩm
- `GET /api/products/{id}` - Lấy sản phẩm theo ID
- `GET /api/products/category/{category}` - Lấy sản phẩm theo danh mục
- `POST /api/products` - Tạo sản phẩm mới

### Authentication
- `POST /api/auth/login` - Đăng nhập
- `POST /api/auth/register` - Đăng ký
- `POST /api/auth/logout` - Đăng xuất

### Cart
- `GET /api/cart` - Lấy giỏ hàng
- `POST /api/cart/add` - Thêm vào giỏ hàng
- `PUT /api/cart/update` - Cập nhật giỏ hàng
- `DELETE /api/cart/remove/{id}` - Xóa khỏi giỏ hàng

### Orders
- `POST /api/orders` - Tạo đơn hàng
- `GET /api/orders/{id}` - Lấy đơn hàng theo ID
- `GET /api/orders/user/{userId}` - Lấy đơn hàng của user

### Payment
- `POST /api/payment/vnpay` - Tạo payment VNPay
- `POST /api/payment/vn-pay-callback` - Callback VNPay

## 🐛 Troubleshooting

### Java Version Error
```
Unsupported class file major version 61
```
**Giải pháp**: Cài đặt Java 21 hoặc downgrade Spring Boot xuống version 2.7

### MySQL Connection Error
```
Access denied for user 'root'@'localhost'
```
**Giải pháp**: Kiểm tra username/password trong `application.yml`

### Port đã được sử dụng
```
Port 8080 was already in use
```
**Giải pháp**: Thay đổi port trong `application.yml`:
```yaml
server:
  port: 8081
```

### CORS Error
**Giải pháp**: Đã được cấu hình trong backend, đảm bảo frontend chạy trên port 5173

## 📝 Ghi chú phát triển

1. **Database Changes**: Khi thay đổi entities, set `ddl-auto: update` để tự động cập nhật schema
2. **API Testing**: Sử dụng Postman hoặc file test trong `src/utils/testApi.ts`
3. **Environment Variables**: Cấu hình trong `.env` cho các thông số khác nhau
4. **Authentication**: JWT token được lưu trong localStorage
5. **Error Handling**: Tất cả API calls đều có error handling

## 🤝 Đóng góp

1. Fork repository
2. Tạo feature branch (`git checkout -b feature/amazing-feature`)
3. Commit changes (`git commit -m 'Add amazing feature'`)
4. Push to branch (`git push origin feature/amazing-feature`)
5. Open Pull Request

## 📄 License

Distributed under the MIT License. See `LICENSE` for more information.
