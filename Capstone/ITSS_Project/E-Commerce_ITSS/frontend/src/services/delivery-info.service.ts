import { apiService } from './api.service';
import { API_ENDPOINTS } from '../config/api';
import type { DeliveryInformation } from '../types'; // Assuming DeliveryInformation type exists in types/index.ts

export class DeliveryInfoService {
    async getAllDeliveryInfo() {
        return apiService.get<DeliveryInformation[]>(API_ENDPOINTS.DELIVERY_INFO.BASE);
    }

    async getDeliveryInfoById(id: number) {
        return apiService.get<DeliveryInformation>(API_ENDPOINTS.DELIVERY_INFO.BY_ID(id));
    }

    async getDeliveryInfoByUserId(userId: number) {
        return apiService.get<DeliveryInformation[]>(API_ENDPOINTS.DELIVERY_INFO.BY_USER(userId));
    }

    async createDeliveryInfo(data: Omit<DeliveryInformation, 'deliveryId' | 'userId'>) {
        return apiService.post<DeliveryInformation>(API_ENDPOINTS.DELIVERY_INFO.BASE, data);
    }

    async updateDeliveryInfo(id: number, data: Partial<Omit<DeliveryInformation, 'deliveryId' | 'userId'>>) {
        return apiService.put<DeliveryInformation>(API_ENDPOINTS.DELIVERY_INFO.BY_ID(id), data);
    }

    async deleteDeliveryInfo(id: number) {
        return apiService.delete<void>(API_ENDPOINTS.DELIVERY_INFO.BY_ID(id));
    }
}

export const deliveryInfoService = new DeliveryInfoService();
