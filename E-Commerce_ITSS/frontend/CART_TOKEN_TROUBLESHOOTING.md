# Troubleshooting Guide - Token và Cart Issues

## Vấn đề chính đã được giải quyết

### 1. Lỗi 401 Unauthorized khi thao tác Cart

**Nguyên nhân:**
- Token JWT hết hạn hoặc không hợp lệ
- Backend không nhận được token hoặc từ chối token

**Giải pháp đã triển khai:**
- **Fallback mechanism**: Khi API backend trả về 401, hệ thống tự động chuyển sang localStorage
- **Token validation**: Kiểm tra token trước khi gửi request
- **Enhanced logging**: Log chi tiết để debug dễ dàng

### 2. Đồng bộ Cart giữa Backend và LocalStorage

**Tính năng mới:**
- **Auto-merge cart**: Khi user đăng nhập, local cart sẽ được merge vào backend cart
- **Seamless fallback**: Thao tác cart luôn hoạt động dù backend có vấn đề
- **Dual-mode operation**: Support cả authenticated user và guest

### 3. Token Management System

**Cải tiến:**
- **JWT decoder**: Decode và kiểm tra expiration time
- **Token validation utilities**: Helper functions để kiểm tra token
- **Automatic cleanup**: Clear expired token tự động

## Cải tiến Code

### 1. Token Utilities (`/src/utils/tokenUtils.ts`)

```typescript
// Các function chính:
- decodeJWT(): Decode JWT token
- isTokenExpired(): Kiểm tra token có hết hạn không
- getTokenTimeLeft(): Tính thời gian còn lại của token
- getCurrentTokenInfo(): Lấy thông tin tổng hợp về token
```

### 2. Enhanced API Service (`/src/services/api.service.ts`)

**Cải tiến:**
- Log chi tiết request/response
- Xử lý 401 errors đặc biệt
- Better error handling

### 3. Smart Cart Context (`/src/contexts/CartContext.tsx`)

**Tính năng mới:**
- `mergeLocalCartToBackend()`: Merge local cart vào backend
- Enhanced fallback cho tất cả operations
- Automatic cart sync khi login/logout

### 4. Improved Auth Context (`/src/contexts/AuthContext.tsx`)

**Cải tiến:**
- `checkTokenValidity()`: Kiểm tra tính hợp lệ của token
- Automatic token cleanup
- Better token management

### 5. Debug Panel (`/src/components/DebugPanel.tsx`)

**Tính năng:**
- Real-time monitoring của token status
- Cart state debugging
- Quick actions (clear token, refresh, etc.)
- Chỉ hiển thị trong development mode

## Workflow mới

### Khi User Đăng nhập:
1. Check local cart có items không
2. Nếu có: Merge vào backend cart
3. Nếu không: Load cart từ backend
4. Clear local cart sau khi merge thành công

### Khi Thao tác Cart:
1. Check authentication status
2. Nếu authenticated: Thử backend API first
3. Nếu backend fail (401/error): Fallback sang localStorage
4. Log tất cả thao tác để debug

### Khi Token Expires:
1. API service detect 401 error
2. Token utilities validate và confirm expiration
3. Clear expired token
4. Fallback operations sang localStorage
5. User có thể continue như guest

## Debug và Monitoring

### Debug Panel
- Click nút "Debug" ở góc phải màn hình (chỉ dev mode)
- Xem real-time token status, cart state
- Quick actions để clear data

### Console Logs
Tất cả operations đều có comprehensive logging:
```
CartContext addToCart: {productId: 1, quantity: 2, isAuthenticated: true}
Token info for headers: {hasToken: true, isValid: false, timeLeft: 0}
401 Unauthorized - token may be expired or invalid
Backend add failed, falling back to localStorage
```

## Best Practices Implemented

### 1. Graceful Degradation
- System luôn hoạt động dù backend có vấn đề
- Guest users có trải nghiệm tương tự authenticated users

### 2. Security
- Token validation trước khi gửi requests
- Automatic cleanup của expired tokens
- Secure token handling

### 3. User Experience
- Seamless transitions giữa guest/authenticated modes
- No data loss khi switch modes
- Consistent cart behavior

### 4. Developer Experience
- Comprehensive logging
- Debug panel cho monitoring
- Clear error messages

## Testing Scenarios

### 1. Token Expiration
- Login, wait for token to expire
- Try cart operations → Should fallback to localStorage
- Verify no errors, operations continue

### 2. Network Issues
- Disconnect internet
- Try cart operations → Should use localStorage
- Reconnect → Should sync properly

### 3. Guest to Authenticated
- Add items to cart as guest
- Login → Local cart should merge to backend
- Verify all items present

### 4. Authenticated to Guest
- Have items in backend cart
- Logout → Should load cart from localStorage
- Operations should continue smoothly

## Monitoring và Alerting

### Console Warnings to Watch:
- "401 Unauthorized - token may be expired"
- "Backend cart failed, using localStorage"
- "Token is invalid or expired"

### Success Indicators:
- "Token is valid - X minutes left"
- "Loaded cart items from backend"
- "Merge item response: {success: true}"

## Future Improvements (Optional)

1. **Token Refresh**: Implement automatic token refresh
2. **Offline Support**: Enhanced offline cart management
3. **Conflict Resolution**: Handle cart conflicts better
4. **Performance**: Optimize cart sync operations
5. **Analytics**: Track fallback usage patterns

---

**Note**: All changes are backward compatible và không ảnh hưởng đến existing functionality. Hệ thống sẽ hoạt động tốt hơn với enhanced error handling và fallback mechanisms.
