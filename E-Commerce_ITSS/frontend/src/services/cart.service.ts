import { apiService } from './api.service';
import { API_ENDPOINTS } from '../config/api';
import type { CartItem } from '../types';

export class CartService {
    async getAllOrderItems() {
        return apiService.get<any[]>(API_ENDPOINTS.CART.BASE);
    }

    async getOrderItemById(id: number) {
        return apiService.get<any>(API_ENDPOINTS.CART.BY_ID(id));
    }

    async addOrderItem(orderItem: any) {
        return apiService.post<any>(API_ENDPOINTS.CART.ADD, orderItem);
    }

    async addOrderItems(orderItems: any[]) {
        return apiService.post<any[]>(API_ENDPOINTS.CART.ADD_MULTIPLE, orderItems);
    }

    async updateOrderItem(id: number, orderItem: any) {
        return apiService.put<any>(API_ENDPOINTS.CART.UPDATE(id), orderItem);
    }

    async updateOrderItemId(orderProductIds: number[], id: number) {
        return apiService.put<string>(API_ENDPOINTS.CART.UPDATE_ORDER(id), orderProductIds);
    } async deleteOrderItem(id: number) {
        return apiService.delete<void>(API_ENDPOINTS.CART.DELETE(id));
    }

    async clearCart() {
        return apiService.delete<void>(API_ENDPOINTS.CART.CLEAR);
    }

    // Local cart management (if backend cart is not available)
    getLocalCart(): CartItem[] {
        const cartStr = localStorage.getItem('cart');
        return cartStr ? JSON.parse(cartStr) : [];
    }

    setLocalCart(cart: CartItem[]) {
        localStorage.setItem('cart', JSON.stringify(cart));
    }

    addToLocalCart(item: CartItem) {
        const cart = this.getLocalCart();
        const existingIndex = cart.findIndex(cartItem => cartItem.productId === item.productId);

        if (existingIndex >= 0) {
            cart[existingIndex].quantity += item.quantity;
        } else {
            cart.push(item);
        }

        this.setLocalCart(cart);
    }

    removeFromLocalCart(productId: number) {
        const cart = this.getLocalCart().filter(item => item.productId !== productId);
        this.setLocalCart(cart);
    }

    clearLocalCart() {
        localStorage.removeItem('cart');
    }
}

export const cartService = new CartService();
