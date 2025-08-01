import { apiService } from './api.service';
import { API_ENDPOINTS } from '../config/api';

export interface GuestDeliveryInfo {
    name: string;
    email: string;
    phone: string;
    address: string;
    province: string;
}

export interface GuestOrderRequest {
    productIds: number[];
    quantities: number[];
    deliveryInfo: GuestDeliveryInfo;
    express: boolean;
}

export interface StockValidationRequest {
    productIds: number[];
    quantities: number[];
}

export interface StockValidationResponse {
    isValid: boolean;
    errors: string[];
    availableStock: Record<number, number>;
}

export interface ShippingPreviewRequest {
    productIds: number[];
    quantities: number[];
    province: string;
    address: string;
    isExpress?: boolean;
}

export interface ShippingPreviewResponse {
    subtotal: number;
    regularShippingFee: number;
    expressShippingFee: number;
    expressAvailable: boolean;
    total: number;
    currentShippingFee: number;
}

export interface GuestOrderResponse {
    orderId: number;
    totalAmount: number;
    shippingFees: number;
    totalFees: number;
    message: string;
}

export class GuestOrderService {
    async validateStock(request: StockValidationRequest) {
        return apiService.post<StockValidationResponse>(API_ENDPOINTS.GUEST.VALIDATE_STOCK, request);
    }

    async getShippingPreview(request: ShippingPreviewRequest) {
        return apiService.post<ShippingPreviewResponse>(API_ENDPOINTS.GUEST.SHIPPING_PREVIEW, request);
    }

    async createGuestOrder(request: GuestOrderRequest) {
        return apiService.post<GuestOrderResponse>(API_ENDPOINTS.GUEST.CREATE_ORDER, request);
    }

    /**
     * Validate cart items before checkout
     */
    async validateCartForCheckout(cartItems: Array<{ productId: number, quantity: number }>) {
        const productIds = cartItems.map(item => item.productId);
        const quantities = cartItems.map(item => item.quantity);

        return this.validateStock({ productIds, quantities });
    }

    /**
     * Calculate shipping options for an address
     */
    async calculateShippingOptions(cartItems: Array<{ productId: number, quantity: number }>,
        province: string, address: string) {
        const productIds = cartItems.map(item => item.productId);
        const quantities = cartItems.map(item => item.quantity);

        // Get shipping preview with all information in one call
        const response = await this.getShippingPreview({
            productIds,
            quantities,
            province,
            address,
            isExpress: false
        });

        return {
            subtotal: response.data.subtotal,
            regularShippingFee: response.data.regularShippingFee,
            expressShippingFee: response.data.expressShippingFee,
            expressAvailable: response.data.expressAvailable,
            total: response.data.total
        };
    }
}

export const guestOrderService = new GuestOrderService();
