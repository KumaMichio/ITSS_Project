# API Documentation - ITSS Project

Tài liệu này mô tả tất cả các API endpoints đã được đồng bộ hóa giữa Frontend và Backend.

## Base URL
```
http://localhost:8080
```

## Authentication Endpoints

### POST /api/auth/login
- **Description**: Đăng nhập
- **Body**: `{ username: string, password: string }`
- **Response**: `{ token: string, role: string, email: string, userId: number }`

### POST /api/auth/signup
- **Description**: Đăng ký
- **Body**: `{ username: string, password: string, email: string }`
- **Response**: `{ message: string }`

### POST /api/auth/change-password
- **Description**: Đổi mật khẩu
- **Body**: `{ oldPassword: string, newPassword: string }`
- **Response**: `{ message: string }`

## Product Endpoints

### General Products
- **GET /api/products** - Lấy tất cả sản phẩm
- **GET /api/products/{id}** - Lấy sản phẩm theo ID
- **GET /api/products/search/{title}** - Tìm kiếm sản phẩm theo title
- **POST /api/products/add-product** - Thêm sản phẩm mới
- **PUT /api/products/modify/{id}** - Sửa sản phẩm
- **DELETE /api/products/delete/{id}** - Xóa sản phẩm

### Books
- **GET /api/products/books** - Lấy tất cả sách
- **GET /api/products/book/{id}** - Lấy sách theo ID
- **GET /api/products/book/search/{title}** - Tìm kiếm sách theo title
- **POST /api/products/add-product/books** - Thêm sách mới
- **PUT /api/products/book/modify/{id}** - Sửa sách
- **DELETE /api/products/book/delete/{id}** - Xóa sách
- **DELETE /api/products/book/delete** - Xóa tất cả sách

### DVDs
- **GET /api/products/dvd** - Lấy tất cả DVD
- **GET /api/products/dvd/{id}** - Lấy DVD theo ID
- **GET /api/products/dvd/search/{title}** - Tìm kiếm DVD theo title
- **POST /api/products/add-product/dvd** - Thêm DVD mới
- **PUT /api/products/dvd/modify/{id}** - Sửa DVD
- **DELETE /api/products/dvd/delete/{id}** - Xóa DVD

### CDLPs
- **GET /api/products/cdlp** - Lấy tất cả CDLP
- **GET /api/products/cdlp/{id}** - Lấy CDLP theo ID
- **GET /api/products/cdlp/search/{title}** - Tìm kiếm CDLP theo title
- **POST /api/products/add-product/cdlp** - Thêm CDLP mới
- **PUT /api/products/cdlp/modify/{id}** - Sửa CDLP
- **DELETE /api/products/cdlp/delete/{id}** - Xóa CDLP
- **DELETE /api/products/cdlp/delete** - Xóa tất cả CDLP

## User Endpoints

- **GET /api/user** - Lấy tất cả users
- **GET /api/user/{id}** - Lấy user theo ID
- **PUT /api/update-user/{id}** - Cập nhật user
- **DELETE /api/delete-user/{id}** - Xóa user
- **PUT /api/user/role/{id}?role={role}** - Thay đổi role của user

## Cart Endpoints

- **GET /api/cart** - Lấy tất cả order items
- **GET /api/cart/{id}** - Lấy order item theo ID
- **POST /api/cart** - Thêm order item
- **POST /api/cart/add** - Thêm nhiều order items
- **PUT /api/cart/{id}** - Cập nhật order item
- **PUT /api/cart/order/{id}** - Cập nhật order ID cho các items
- **DELETE /api/cart/{id}** - Xóa order item

## Order Endpoints

- **GET /order** - Lấy tất cả orders
- **GET /order/{id}** - Lấy orders theo user ID
- **POST /order/create?orderProductIds={ids}&userId={id}** - Tạo order thường
- **POST /order/create-by-delivery-id?orderProductIds={ids}&deliveryId={id}** - Tạo order với delivery ID
- **POST /order/create-express-order?orderProductIds={ids}&deliveryId={id}** - Tạo express order
- **PUT /order/status/{id}** - Cập nhật trạng thái order

## Delivery Information Endpoints

- **GET /api/delivery-info** - Lấy tất cả delivery info
- **GET /api/delivery-info/{id}** - Lấy delivery info theo ID
- **GET /api/delivery-info/user/{userId}** - Lấy delivery info theo user ID
- **POST /api/delivery-info** - Thêm delivery info
- **PUT /api/delivery-info/{id}** - Cập nhật delivery info
- **DELETE /api/delivery-info/{id}** - Xóa delivery info

## Shipping Method Endpoints

- **GET /api/shipping-method** - Lấy tất cả shipping methods

## Transaction Endpoints

- **GET /transactions/order/{orderId}** - Lấy transactions theo order ID

## Payment Endpoints

- **GET /api/payment/vn-pay** - Tạo VNPay payment
- **GET /api/payment/vn-pay-order/{orderId}** - Tạo VNPay payment cho order
- **GET /api/payment/vn-pay-callback** - VNPay callback handler

## Notes

1. Tất cả endpoints yêu cầu authentication (trừ auth endpoints) phải gửi kèm JWT token trong header:
   ```
   Authorization: Bearer {token}
   ```

2. API prefix đã được cấu hình trong `application.yml`:
   ```yaml
   spring:
     application:
       api-prefix: /api
   ```

3. Frontend đã được cập nhật để sử dụng đúng các endpoints này.

4. Các parameter trong URL path được encode properly.

5. Query parameters được sử dụng cho các endpoints như order creation và user role change.