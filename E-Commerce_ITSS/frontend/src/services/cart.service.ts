import { apiService } from './api.service';
import { API_ENDPOINTS } from '../config/api';
import type { CartItem } from '../types';

export class CartService {
    async getAllOrderItems() {
        console.log('CartService getAllOrderItems called');
        const result = await apiService.get<any[]>(API_ENDPOINTS.CART.BASE);
        console.log('CartService getAllOrderItems result:', result);
        return result;
    }

    async getOrderItemById(id: number) {
        return apiService.get<any>(API_ENDPOINTS.CART.BY_ID(id));
    } async addOrderItem(orderItem: any) {
        console.log('CartService addOrderItem called with:', orderItem);
        const result = await apiService.post<any>(API_ENDPOINTS.CART.ADD, orderItem);
        console.log('CartService addOrderItem result:', result);
        return result;
    }

    async addOrderItems(orderItems: any[]) {
        return apiService.post<any[]>(API_ENDPOINTS.CART.ADD_MULTIPLE, orderItems);
    } async updateOrderItem(id: number, orderItem: any) {
        console.log('CartService updateOrderItem:', { id, orderItem });
        const result = await apiService.put<any>(API_ENDPOINTS.CART.UPDATE(id), orderItem);
        console.log('CartService updateOrderItem result:', result);
        return result;
    }

    async updateOrderItemId(orderProductIds: number[], id: number) {
        return apiService.put<string>(API_ENDPOINTS.CART.UPDATE_ORDER(id), orderProductIds);
    } async deleteOrderItem(id: number) {
        console.log('CartService deleteOrderItem:', { id });
        const result = await apiService.delete<void>(API_ENDPOINTS.CART.DELETE(id));
        console.log('CartService deleteOrderItem result:', result);
        return result;
    }

    async clearCart() {
        return apiService.delete<void>(API_ENDPOINTS.CART.CLEAR);
    }    // Local cart management (if backend cart is not available)
    getLocalCart(): CartItem[] {
        try {
            const cartStr = localStorage.getItem('cart');
            const cart = cartStr ? JSON.parse(cartStr) : [];
            console.log('getLocalCart:', cart);
            return cart;
        } catch (error) {
            console.error('Error reading local cart:', error);
            return [];
        }
    }

    setLocalCart(cart: CartItem[]) {
        try {
            console.log('setLocalCart:', cart);
            localStorage.setItem('cart', JSON.stringify(cart));
        } catch (error) {
            console.error('Error saving local cart:', error);
        }
    } addToLocalCart(item: CartItem) {
        console.log('addToLocalCart called with:', item);
        const cart = this.getLocalCart();
        console.log('Current cart before adding:', cart);
        const existingIndex = cart.findIndex(cartItem => cartItem.productId === item.productId);

        if (existingIndex >= 0) {
            cart[existingIndex].quantity += item.quantity;
            console.log('Updated existing item:', cart[existingIndex]);
        } else {
            cart.push(item);
            console.log('Added new item to cart, cart length:', cart.length);
        }

        console.log('Final cart before saving:', cart);
        this.setLocalCart(cart);

        // Verify it was saved
        const savedCart = this.getLocalCart();
        console.log('Cart after saving to localStorage:', savedCart);
    }

    removeFromLocalCart(productId: number) {
        console.log('removeFromLocalCart:', productId);
        const cart = this.getLocalCart().filter(item => item.productId !== productId);
        console.log('Cart after removal:', cart);
        this.setLocalCart(cart);
    }

    clearLocalCart() {
        console.log('clearLocalCart');
        localStorage.removeItem('cart');
    }
}

export const cartService = new CartService();
