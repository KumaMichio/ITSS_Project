import { apiService } from './api.service';
import { API_ENDPOINTS } from '../config/api';
import type { Order } from '../types';

export interface CreateOrderRequest {
    orderProductIds: number[];
    userId?: number;
    deliveryId?: number;
}

export class OrderService {
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
