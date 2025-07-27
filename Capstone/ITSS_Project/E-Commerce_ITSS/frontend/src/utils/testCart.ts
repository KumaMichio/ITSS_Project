import { cartService } from '../services/cart.service';
import { mockProducts } from '../data/mockData';
import type { CartItem } from '../types';

/**
 * Test localStorage cart functionality
 */
export const testLocalStorageCart = () => {
    console.log('=== Testing localStorage Cart ===');

    // Clear existing cart
    cartService.clearLocalCart();
    console.log('1. Cleared local cart');

    // Get empty cart
    let cart = cartService.getLocalCart();
    console.log('2. Empty cart:', cart);

    // Add first product
    const product1 = mockProducts[0];
    if (product1) {
        const cartItem1: CartItem = {
            productId: product1.id,
            quantity: 2,
            product: product1
        };

        cartService.addToLocalCart(cartItem1);
        console.log('3. Added first product:', cartItem1);

        cart = cartService.getLocalCart();
        console.log('4. Cart after adding first product:', cart);
    }

    // Add second product
    const product2 = mockProducts[1];
    if (product2) {
        const cartItem2: CartItem = {
            productId: product2.id,
            quantity: 1,
            product: product2
        };

        cartService.addToLocalCart(cartItem2);
        console.log('5. Added second product:', cartItem2);

        cart = cartService.getLocalCart();
        console.log('6. Cart after adding second product:', cart);
    }

    // Add same product again (should update quantity)
    if (product1) {
        const cartItem1Again: CartItem = {
            productId: product1.id,
            quantity: 1,
            product: product1
        };

        cartService.addToLocalCart(cartItem1Again);
        console.log('7. Added first product again:', cartItem1Again);

        cart = cartService.getLocalCart();
        console.log('8. Cart after adding first product again (should be quantity 3):', cart);
    }

    // Remove product
    if (product2) {
        cartService.removeFromLocalCart(product2.id);
        console.log('9. Removed second product');

        cart = cartService.getLocalCart();
        console.log('10. Cart after removing second product:', cart);
    }

    console.log('=== localStorage Cart Test Complete ===');
    return cart;
};

/**
 * Test cart item creation
 */
export const testCartItemCreation = (productId: number = 1) => {
    console.log('=== Testing Cart Item Creation ===');

    const product = mockProducts.find(p => p.id === productId);
    if (!product) {
        console.error('Product not found:', productId);
        return null;
    }

    const cartItem: CartItem = {
        productId: product.id,
        quantity: 1,
        product: product
    };

    console.log('Created cart item:', cartItem);
    console.log('Product details:', {
        id: product.id,
        title: product.title,
        price: product.price,
        category: product.category,
        quantity: product.quantity
    });

    return cartItem;
};

/**
 * Run all cart tests
 */
export const runCartTests = () => {
    console.log('🧪 Running Cart Tests...');

    // Test 1: Cart item creation
    const testItem = testCartItemCreation(1);

    // Test 2: localStorage operations
    const finalCart = testLocalStorageCart();

    console.log('✅ All cart tests completed');
    console.log('Final cart state:', finalCart);

    return {
        testItem,
        finalCart
    };
};

// Make functions available globally for browser console testing
if (typeof window !== 'undefined') {
    (window as any).testLocalStorageCart = testLocalStorageCart;
    (window as any).testCartItemCreation = testCartItemCreation;
    (window as any).runCartTests = runCartTests;
}
