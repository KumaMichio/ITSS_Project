import { apiService } from './api.service';
import { API_ENDPOINTS } from '../config/api';
import type { Order } from '../types';

export interface CreateOrderRequest {
    orderProductIds: number[];
    userId?: number;
    deliveryId?: number;
}

export interface CreateUserOrderV2Request {
    productIds: number[];
    quantities: number[];
    userId: number;
    deliveryId?: number;
    express: boolean;
}

export class OrderService {
    // Legacy methods
    async createOrder(orderProductIds: number[], userId: number) {
        const params = new URLSearchParams({
            orderProductIds: orderProductIds.join(','),
            userId: userId.toString()
        });
        return apiService.post<Order>(`${API_ENDPOINTS.ORDERS.CREATE}?${params.toString()}`);
    }

    async createOrderByDeliveryId(orderProductIds: number[], deliveryId: number) {
        const params = new URLSearchParams({
            orderProductIds: orderProductIds.join(','),
            deliveryId: deliveryId.toString()
        });
        return apiService.post<Order>(`${API_ENDPOINTS.ORDERS.CREATE_BY_DELIVERY}?${params.toString()}`);
    }

    async createExpressOrder(orderProductIds: number[], deliveryId: number) {
        const params = new URLSearchParams({
            orderProductIds: orderProductIds.join(','),
            deliveryId: deliveryId.toString()
        });
        return apiService.post<Order>(`${API_ENDPOINTS.ORDERS.CREATE_EXPRESS}?${params.toString()}`);
    }

    // V2 API methods
    async createUserOrderV2(request: CreateUserOrderV2Request) {
        return apiService.post<Order>(API_ENDPOINTS.ORDERS.V2.CREATE_USER_ORDER, request);
    }

    async markOrderAsPaid(orderId: number, transactionId: string) {
        const params = new URLSearchParams({ transactionId });
        return apiService.put<Order>(`${API_ENDPOINTS.ORDERS.V2.MARK_PAID(orderId)}?${params.toString()}`);
    }

    // Common methods
    async getOrderById(id: number) {
        return apiService.get<any>(API_ENDPOINTS.ORDERS.BY_ID(id));
    }

    async getAllOrders() {
        return apiService.get<Order[]>(API_ENDPOINTS.ORDERS.BASE);
    }

    async updateOrderStatus(orderId: number) {
        return apiService.put<Order>(API_ENDPOINTS.ORDERS.UPDATE_STATUS(orderId));
    }
}

export const orderService = new OrderService();
