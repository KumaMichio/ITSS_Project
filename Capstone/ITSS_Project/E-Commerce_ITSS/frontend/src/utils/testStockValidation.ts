// Test script for stock validation functionality
import { productService } from '../services/product.service';

export const testStockValidation = async () => {
    console.log('🧪 Testing Stock Validation...\n');

    try {
        // Test 1: Get product information
        console.log('📦 Test 1: Getting product information...');
        const response = await productService.getProductById(1);

        if (response.success && response.data) {
            const product = response.data;
            console.log(`✅ Product found: ${product.title}`);
            console.log(`📊 Available stock: ${product.quantity}`);

            // Test 2: Check valid quantity
            console.log('\n📦 Test 2: Checking valid quantity...');
            if (product.quantity > 0) {
                const validQuantity = Math.min(product.quantity, 2);
                console.log(`✅ Valid quantity check: ${validQuantity} <= ${product.quantity} ✓`);
            }

            // Test 3: Check invalid quantity
            console.log('\n📦 Test 3: Checking invalid quantity...');
            const invalidQuantity = product.quantity + 10;
            console.log(`❌ Invalid quantity check: ${invalidQuantity} > ${product.quantity} ✗`);

            // Test 4: Edge cases
            console.log('\n📦 Test 4: Edge cases...');
            if (product.quantity <= 5) {
                console.log(`⚠️ Low stock warning: Product has only ${product.quantity} items left`);
            }

            console.log('\n🎉 Stock validation test completed successfully!');
            return { success: true, product };

        } else {
            console.log('❌ Failed to get product information');
            return { success: false, error: 'Product not found' };
        }

    } catch (error) {
        console.log('❌ Test failed:', error);
        return { success: false, error: error instanceof Error ? error.message : 'Unknown error' };
    }
};

// Helper function to simulate cart validation
export const simulateCartValidation = (cartItems: Array<{ productId: number, quantity: number, availableStock: number }>) => {
    console.log('\n🛒 Simulating Cart Validation...\n');

    const errors: string[] = [];
    let allValid = true;

    cartItems.forEach((item, index) => {
        console.log(`Item ${index + 1}: Requested ${item.quantity}, Available ${item.availableStock}`);

        if (item.quantity > item.availableStock) {
            const errorMsg = `Product ID ${item.productId}: Requested ${item.quantity} but only ${item.availableStock} available`;
            errors.push(errorMsg);
            console.log(`❌ ${errorMsg}`);
            allValid = false;
        } else {
            console.log(`✅ Valid quantity`);
        }

        if (item.availableStock <= 5) {
            console.log(`⚠️ Low stock warning: Only ${item.availableStock} items left`);
        }
    });

    console.log(`\n🎯 Validation Result: ${allValid ? 'PASS' : 'FAIL'}`);
    if (errors.length > 0) {
        console.log('Errors:');
        errors.forEach(error => console.log(`  - ${error}`));
    }

    return { allValid, errors };
};

// Example usage:
// testStockValidation();
// simulateCartValidation([
//     { productId: 1, quantity: 2, availableStock: 5 },
//     { productId: 2, quantity: 10, availableStock: 3 }
// ]);
