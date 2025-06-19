import { apiService } from './api.service';
import { API_ENDPOINTS } from '../config/api';
import type { LoginRequest, RegisterRequest, AuthResponse, User } from '../types';

export class AuthService {
    async login(credentials: LoginRequest) {
        const response = await apiService.post<AuthResponse>(
            API_ENDPOINTS.AUTH.LOGIN,
            credentials
        );

        if (response.success && response.data) {
            // Store token in localStorage
            localStorage.setItem('authToken', response.data.token);
            localStorage.setItem('user', JSON.stringify(response.data.user));
        }

        return response;
    }

    async register(userData: RegisterRequest) {
        return apiService.post<AuthResponse>(API_ENDPOINTS.AUTH.REGISTER, userData);
    }

    async changePassword(passwordData: { oldPassword: string; newPassword: string }) {
        return apiService.post<void>(API_ENDPOINTS.AUTH.CHANGE_PASSWORD, passwordData);
    }

    async logout() {
        try {
            await apiService.post<void>(API_ENDPOINTS.AUTH.LOGOUT);
        } finally {
            // Always clear local storage
            localStorage.removeItem('authToken');
            localStorage.removeItem('user');
        }
    }

    getCurrentUser(): User | null {
        const userStr = localStorage.getItem('user');
        return userStr ? JSON.parse(userStr) : null;
    }

    getToken(): string | null {
        return localStorage.getItem('authToken');
    }

    isAuthenticated(): boolean {
        return !!this.getToken();
    }
}

export const authService = new AuthService();
