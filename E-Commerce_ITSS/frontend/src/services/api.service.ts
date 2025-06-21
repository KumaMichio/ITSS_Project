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

            // Don't include auth headers for auth endpoints
            const includeAuth = !endpoint.includes('/api/auth/');

            const config: RequestInit = {
                headers: getHeaders(includeAuth),
                ...options,
            };

            console.log('API Request:', { url, method: config.method || 'GET', includeAuth });

            const response = await fetch(url, config);

            console.log('API Response:', { url, status: response.status, statusText: response.statusText });

            // Check if response has content
            let data = null;
            const contentType = response.headers.get('content-type');
            if (contentType && contentType.includes('application/json')) {
                const text = await response.text();
                if (text.trim()) {
                    try {
                        data = JSON.parse(text);
                    } catch (error) {
                        console.error('Failed to parse JSON:', error);
                        data = null;
                    }
                }
            }

            console.log('API Response Data:', data);

            if (!response.ok) {
                // Handle 401 Unauthorized specifically
                if (response.status === 401) {
                    console.warn('401 Unauthorized - token may be expired or invalid');
                    // Optionally clear token here if it's invalid
                    // localStorage.removeItem('authToken');
                }

                return {
                    success: false,
                    data: null as T,
                    error: data?.error || data?.message || `HTTP ${response.status}: ${response.statusText}`,
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
