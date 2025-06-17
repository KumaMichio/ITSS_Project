import { API_BASE_URL, getHeaders } from '../config/api';
import type { ApiResponse } from '../types';

class ApiService {
    private baseURL: string;

    constructor() {
        this.baseURL = API_BASE_URL;
    } private async request<T>(
        endpoint: string,
        options: RequestInit = {}
    ): Promise<ApiResponse<T>> {
        try {
            const url = `${this.baseURL}${endpoint}`;
            const config: RequestInit = {
                headers: getHeaders(),
                ...options,
            };

            console.log('API Request:', { url, config });

            const response = await fetch(url, config);

            console.log('API Response:', { status: response.status, statusText: response.statusText });

            const data = await response.json();
            console.log('API Response Data:', data);

            if (!response.ok) {
                return {
                    success: false,
                    data: null as T,
                    error: data.error || data.message || `HTTP ${response.status}: ${response.statusText}`,
                };
            }

            return {
                success: true,
                data,
            };
        } catch (error) {
            console.error('API request failed:', error);
            return {
                success: false,
                data: null as T,
                error: error instanceof Error ? error.message : 'Unknown error occurred',
            };
        }
    }

    async get<T>(endpoint: string): Promise<ApiResponse<T>> {
        return this.request<T>(endpoint, { method: 'GET' });
    }

    async post<T>(endpoint: string, data?: any): Promise<ApiResponse<T>> {
        return this.request<T>(endpoint, {
            method: 'POST',
            body: data ? JSON.stringify(data) : undefined,
        });
    }

    async put<T>(endpoint: string, data?: any): Promise<ApiResponse<T>> {
        return this.request<T>(endpoint, {
            method: 'PUT',
            body: data ? JSON.stringify(data) : undefined,
        });
    }

    async delete<T>(endpoint: string): Promise<ApiResponse<T>> {
        return this.request<T>(endpoint, { method: 'DELETE' });
    }
}

export const apiService = new ApiService();
