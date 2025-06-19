import { apiService } from './api.service';
import { API_ENDPOINTS } from '../config/api';

export class TransactionService {
    async getTransactionsByOrderId(orderId: number) {
        return apiService.get<any>(API_ENDPOINTS.TRANSACTIONS.BY_ORDER_ID(orderId));
    }

    // Add other methods like create, update, delete if supported by backend
}

export const transactionService = new TransactionService();
