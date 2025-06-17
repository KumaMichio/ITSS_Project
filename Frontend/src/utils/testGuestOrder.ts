// Test Guest Order Functionality
// This script tests the guest order workflow

import { guestOrderService } from '../services/guest-order.service';

export async function testGuestOrderWorkflow() {
    console.log('=== Testing Guest Order Workflow ===');

    // Mock cart items for testing
    const testCartItems = [
        { productId: 1, quantity: 2 },
        { productId: 2, quantity: 1 }
    ];

    // Test data
    const testDeliveryInfo = {
        name: 'Test Customer',
        email: 'test@example.com',
        phone: '0123456789',
        address: '123 Test Street, District 1',
        province: 'Hà Nội'
    };

    try {
        console.log('1. Testing stock validation...');
        const stockValidation = await guestOrderService.validateCartForCheckout(testCartItems);
        console.log('Stock validation result:', stockValidation);

        if (stockValidation.success && stockValidation.data?.isValid) {
            console.log('✅ Stock validation passed');
        } else {
            console.log('❌ Stock validation failed:', stockValidation.data?.errors);
            return;
        }

        console.log('\n2. Testing shipping calculation...');
        const shippingOptions = await guestOrderService.calculateShippingOptions(
            testCartItems,
            testDeliveryInfo.province,
            testDeliveryInfo.address
        );
        console.log('Shipping options:', shippingOptions);

        if (shippingOptions.regular.success && shippingOptions.express.success) {
            console.log('✅ Shipping calculation passed');
            console.log('Regular shipping:', shippingOptions.regular.data);
            console.log('Express shipping:', shippingOptions.express.data);
        } else {
            console.log('❌ Shipping calculation failed');
        }

        console.log('\n3. Testing guest order creation...');
        const orderRequest = {
            productIds: testCartItems.map(item => item.productId),
            quantities: testCartItems.map(item => item.quantity),
            deliveryInfo: testDeliveryInfo,
            express: false
        };

        const orderResult = await guestOrderService.createGuestOrder(orderRequest);
        console.log('Order creation result:', orderResult);

        if (orderResult.success && orderResult.data) {
            console.log('✅ Guest order creation passed');
            console.log('Order ID:', orderResult.data.orderId);
            console.log('Total amount:', orderResult.data.totalAmount);
        } else {
            console.log('❌ Guest order creation failed:', orderResult.error);
        }

    } catch (error) {
        console.error('Test failed with error:', error);
    }
}

// Test with fallback when backend is offline
export async function testGuestOrderFallback() {
    console.log('\n=== Testing Guest Order Fallback (Backend Offline) ===');

    // This should demonstrate fallback behavior when backend is not available
    const testCartItems = [
        { productId: 1, quantity: 1 },
        { productId: 2, quantity: 2 }
    ];

    try {
        console.log('Testing fallback validation...');
        const result = await guestOrderService.validateCartForCheckout(testCartItems);
        console.log('Fallback validation result:', result);

        console.log('Testing fallback shipping calculation...');
        const shipping = await guestOrderService.calculateShippingOptions(
            testCartItems,
            'Hà Nội',
            '123 Test Address'
        );
        console.log('Fallback shipping result:', shipping);

    } catch (error) {
        console.log('Expected fallback behavior:', error.message);
    }
}

// Export for use in other components
export default {
    testGuestOrderWorkflow,
    testGuestOrderFallback
};
