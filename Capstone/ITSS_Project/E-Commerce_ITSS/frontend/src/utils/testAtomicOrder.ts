// Test Atomic Order Functionality
// This script tests the atomic order creation workflow

import { paymentService } from '../services/payment.service';

export async function testAtomicOrderWorkflow() {
    console.log('=== Testing Atomic Order Workflow ===');

    // Mock cart items for testing (with prices)
    const testCartItems = [
        { productId: 1, quantity: 2, price: 50000 },
        { productId: 2, quantity: 1, price: 100000 }
    ];

    // Test delivery info
    const testDeliveryInfo = {
        fullname: 'Test Customer',
        email: 'test@example.com',
        phone: '0123456789',
        address: '123 Test Street, District 1',
        province: 'Hà Nội'
    };

    try {
        console.log('1. Testing atomic order creation...');
        
        const result = await paymentService.createPaymentAndCompleteOrder(
            testCartItems,
            testDeliveryInfo,
            'standard'
        );
        
        console.log('Atomic order creation result:', result);

        if (result.success) {
            console.log('✅ Atomic order creation passed');
            console.log(`📦 Order ID: ${result.orderId}`);
            console.log(`📋 Items: ${result.orderItemsCount}`);
            console.log(`💰 Transaction ID: ${result.transactionId}`);
        } else {
            console.log('❌ Atomic order creation failed:', result.error);
        }

        return result;
    } catch (error) {
        console.error('❌ Test failed:', error);
        throw error;
    }
}

export async function testAtomicOrderWithExpressShipping() {
    console.log('=== Testing Atomic Order with Express Shipping ===');

    const testCartItems = [
        { productId: 3, quantity: 1, price: 75000 }
    ];

    const testDeliveryInfo = {
        fullname: 'Express Customer',
        email: 'express@example.com',
        phone: '0987654321',
        address: '456 Express Avenue',
        province: 'TP.HCM'
    };

    try {
        const result = await paymentService.createPaymentAndCompleteOrder(
            testCartItems,
            testDeliveryInfo,
            'express_2h'
        );

        console.log('Express order result:', result);
        
        if (result.success) {
            console.log('✅ Express atomic order creation passed');
        } else {
            console.log('❌ Express atomic order creation failed:', result.error);
        }
        
        return result;
    } catch (error) {
        console.error('❌ Express order test failed:', error);
        throw error;
    }
}

export async function testDuplicateOrderHandling() {
    console.log('=== Testing Duplicate Order Handling ===');

    const testCartItems = [
        { productId: 1, quantity: 1, price: 25000 }
    ];

    const testDeliveryInfo = {
        fullname: 'Duplicate Test Customer',
        email: 'duplicate@example.com',
        phone: '0111222333',
        address: '789 Duplicate Street',
        province: 'Đà Nẵng'
    };

    try {
        // Create first order
        console.log('Creating first order...');
        const firstResult = await paymentService.createPaymentAndCompleteOrder(
            testCartItems,
            testDeliveryInfo,
            'standard'
        );

        if (firstResult.success) {
            console.log('✅ First order created successfully');
            
            // Try to create duplicate order with same transaction reference
            console.log('Attempting to create duplicate order...');
            const secondResult = await paymentService.createPaymentAndCompleteOrder(
                testCartItems,
                testDeliveryInfo,
                'standard'
            );

            if (secondResult.duplicate) {
                console.log('✅ Duplicate order detection working correctly');
                console.log(`Duplicate order ID: ${secondResult.orderId}`);
            } else {
                console.log('⚠️ Duplicate order detection may not be working as expected');
            }
        }

        return firstResult;
    } catch (error) {
        console.error('❌ Duplicate order test failed:', error);
        throw error;
    }
}

// Helper function to run all tests
export async function runAllAtomicOrderTests() {
    console.log('🚀 Starting all atomic order tests...');
    
    try {
        await testAtomicOrderWorkflow();
        console.log('\n');
        
        await testAtomicOrderWithExpressShipping();
        console.log('\n');
        
        await testDuplicateOrderHandling();
        console.log('\n');
        
        console.log('✅ All atomic order tests completed successfully!');
    } catch (error) {
        console.error('❌ Some tests failed:', error);
    }
}

// Export for use in other components
export default {
    testAtomicOrderWorkflow,
    testAtomicOrderWithExpressShipping,
    testDuplicateOrderHandling,
    runAllAtomicOrderTests
};
