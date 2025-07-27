import type { CartItem } from '../types';

const SESSION_CART_KEY = 'session_cart';
const SESSION_ID_KEY = 'session_id';

export class SessionCartManager {
    private static generateSessionId(): string {
        return `session_${Date.now()}_${Math.random().toString(36).substr(2, 9)}`;
    }

    static getSessionId(): string {
        let sessionId = sessionStorage.getItem(SESSION_ID_KEY);
        if (!sessionId) {
            sessionId = this.generateSessionId();
            sessionStorage.setItem(SESSION_ID_KEY, sessionId);
        }
        return sessionId;
    }

    static getSessionCart(): CartItem[] {
        const cartStr = sessionStorage.getItem(SESSION_CART_KEY);
        return cartStr ? JSON.parse(cartStr) : [];
    }

    static setSessionCart(cart: CartItem[]) {
        sessionStorage.setItem(SESSION_CART_KEY, JSON.stringify(cart));
    }

    static addToSessionCart(item: CartItem) {
        const cart = this.getSessionCart();
        const existingIndex = cart.findIndex(cartItem => cartItem.productId === item.productId);

        if (existingIndex >= 0) {
            cart[existingIndex].quantity += item.quantity;
        } else {
            cart.push(item);
        }

        this.setSessionCart(cart);
    }

    static updateSessionCartItem(productId: number, quantity: number) {
        const cart = this.getSessionCart();
        const existingIndex = cart.findIndex(cartItem => cartItem.productId === productId);

        if (existingIndex >= 0) {
            if (quantity <= 0) {
                cart.splice(existingIndex, 1);
            } else {
                cart[existingIndex].quantity = quantity;
            }
        }

        this.setSessionCart(cart);
    }

    static removeFromSessionCart(productId: number) {
        const cart = this.getSessionCart().filter(item => item.productId !== productId);
        this.setSessionCart(cart);
    }

    static clearSessionCart() {
        sessionStorage.removeItem(SESSION_CART_KEY);
    }

    static clearSession() {
        sessionStorage.removeItem(SESSION_CART_KEY);
        sessionStorage.removeItem(SESSION_ID_KEY);
    }

    static getTotalItems(): number {
        const cart = this.getSessionCart();
        return cart.reduce((sum, item) => sum + item.quantity, 0);
    }

    static getTotalPrice(): number {
        const cart = this.getSessionCart();
        return cart.reduce((sum, item) => sum + (item.product.price * item.quantity), 0);
    }
}
