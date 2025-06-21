# Cart Troubleshooting Guide

## Vấn đề: Cart không hiển thị sản phẩm sau khi thêm

### Triệu chứng:
- Nhấn "Add to cart" nhưng Debug Panel hiển thị:
  - Backend Items: 0
  - LocalStorage Items: 0
  - Total Items: 0
- Console log hiển thị "Backend cart failed, using localStorage: HTTP 401"

### Nguyên nhân có thể:

#### 1. Token Authentication Issues
- Token không hợp lệ hoặc đã hết hạn
- Backend từ chối token (401 Unauthorized)

#### 2. LocalStorage Fallback Không Hoạt Động
- Logic fallback có bug
- LocalStorage operations fail
- State không được update sau khi thêm vào localStorage

#### 3. Mock Data Issues
- Product categories không match với filter system
- Product data bị thiếu hoặc corrupt

### Giải pháp đã triển khai:

#### 1. Enhanced Token Validation
```typescript
// Check token validity BEFORE making API calls
const tokenInfo = getCurrentTokenInfo();
if (tokenInfo.hasToken && tokenInfo.isValid) {
    // Try backend
} else {
    // Go straight to localStorage
}
```

#### 2. Improved Fallback Logic
```typescript
// Ensure localStorage cart is always updated and state refreshed
cartService.addToLocalCart(cartItem);
const updatedCart = cartService.getLocalCart();
console.log('Updated local cart:', updatedCart);
setItems(updatedCart);
```

#### 3. Fixed Mock Data Categories
- Updated categories to match filter system: "Book", "CD", "DVD", "CDLP"

### Debug Steps:

#### Step 1: Check Debug Panel
1. Click "Debug" button (bottom right)
2. Check token status:
   - Has Token: Should be ✅ or ❌
   - Valid: Should show if token is valid
   - Time Left: Should show remaining time

#### Step 2: Test localStorage Directly
1. In Debug Panel, click "Test Cart"
2. This will run comprehensive localStorage tests
3. Check console for detailed logs

#### Step 3: Manual Console Testing
```javascript
// In browser console:
runCartTests(); // Run all cart tests
testLocalStorageCart(); // Test localStorage operations
testCartItemCreation(1); // Test creating cart item for product ID 1
```

#### Step 4: Check Console Logs
Look for these key log messages:
```
CartContext addToCart: {productId: 1, quantity: 1, isAuthenticated: true}
Token check for addToCart: {hasToken: true, isValid: false, ...}
Token is invalid, adding directly to localStorage
Updated local cart (invalid token): [...]
```

#### Step 5: Verify localStorage Content
```javascript
// Check what's actually in localStorage
console.log('localStorage cart:', localStorage.getItem('cart'));
console.log('localStorage token:', localStorage.getItem('authToken'));
```

### Expected Behavior:

#### For Authenticated Users (Valid Token):
1. Try backend API first
2. If successful: Update from backend
3. If fail: Fallback to localStorage

#### For Authenticated Users (Invalid Token):
1. Skip backend API (avoid 401)
2. Go directly to localStorage
3. Update state immediately

#### For Guest Users:
1. Always use localStorage
2. Update state immediately

### Troubleshooting Commands:

#### Clear Everything and Start Fresh:
```javascript
localStorage.removeItem('cart');
localStorage.removeItem('authToken');
window.location.reload();
```

#### Test Adding Product Manually:
```javascript
const { cartService } = await import('./services/cart.service');
const { mockProducts } = await import('./data/mockData');

const product = mockProducts[0];
const cartItem = {
    productId: product.id,
    quantity: 1,
    product: product
};

cartService.addToLocalCart(cartItem);
console.log('Cart after manual add:', cartService.getLocalCart());
```

#### Check Token Details:
```javascript
const { getCurrentTokenInfo } = await import('./utils/tokenUtils');
console.log('Token info:', getCurrentTokenInfo());
```

### Common Issues & Solutions:

#### Issue: "Backend cart failed, using localStorage" but cart still empty
**Solution**: Check if localStorage operations are working:
```javascript
localStorage.setItem('test', 'hello');
console.log(localStorage.getItem('test')); // Should print 'hello'
localStorage.removeItem('test');
```

#### Issue: State not updating after localStorage operations
**Solution**: Make sure `setItems()` is called with fresh data:
```javascript
// Wrong:
setItems(cartService.getLocalCart());

// Right:
const updatedCart = cartService.getLocalCart();
console.log('Fresh cart data:', updatedCart);
setItems(updatedCart);
```

#### Issue: Token shows as valid but API still returns 401
**Solution**: Token might be valid format but rejected by backend. Check:
1. Backend server is running
2. Token hasn't been revoked
3. User still exists in database

### Prevention:

#### 1. Always Use Token Validation
- Check token before making API calls
- Implement proper fallback logic

#### 2. Comprehensive Logging
- Log all cart operations
- Log token status
- Log localStorage operations

#### 3. Robust Error Handling
- Always have fallback to localStorage
- Never let cart operations completely fail
- Preserve user data

#### 4. Regular Testing
- Use Debug Panel regularly
- Test both authenticated and guest flows
- Test token expiration scenarios

### Success Indicators:

#### Debug Panel Should Show:
- Backend Items: 0 (if token invalid)
- LocalStorage Items: > 0 (after adding products)
- Total Items: > 0
- Total Price: > 0 VND

#### Console Should Show:
- Clear operation flow logs
- No error messages
- "Updated local cart" with actual data

#### UI Should Show:
- Cart icon with item count
- Products visible in cart page
- Success messages when adding products
