import { apiService } from './api.service';
import { API_ENDPOINTS } from '../config/api';
import type { ShippingMethod } from '../types'; // Assuming ShippingMethod type exists in types/index.ts

export class ShippingMethodService {
    async getAllShippingMethods() {
        return apiService.get<ShippingMethod[]>(API_ENDPOINTS.SHIPPING_METHODS.BASE);
    }

    // Add other methods like getById, create, update, delete if supported by backend
}

export const shippingMethodService = new ShippingMethodService();
