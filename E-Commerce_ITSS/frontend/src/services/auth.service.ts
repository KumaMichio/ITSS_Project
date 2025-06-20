import { apiService } from './api.service';
import { API_ENDPOINTS } from '../config/api';
import type { LoginRequest, RegisterRequest, AuthResponse, User } from '../types';
import '../utils/debugAuth'; // Auto-clear corrupted localStorage data

export class AuthService {
    async login(credentials: LoginRequest) {
        const response = await apiService.post<AuthResponse>(
            API_ENDPOINTS.AUTH.LOGIN,
            credentials
        );

        if (response.success && response.data && response.data.token && response.data.user) {
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
    } async logout() {
        try {
            await apiService.post<void>(API_ENDPOINTS.AUTH.LOGOUT);
        } finally {
            // Always clear local storage
            this.clearStorage();
        }
    }

    clearStorage(): void {
        localStorage.removeItem('authToken');
        localStorage.removeItem('user');
    } getCurrentUser(): User | null {
        try {
            const userStr = localStorage.getItem('user');
            if (!userStr || userStr === 'undefined' || userStr === 'null') {
                return null;
            }
            return JSON.parse(userStr);
        } catch (error) {
            console.error('Error parsing user data from localStorage:', error);
            // Clear invalid data
            localStorage.removeItem('user');
            return null;
        }
    } getToken(): string | null {
        const token = localStorage.getItem('authToken');
        if (!token || token === 'undefined' || token === 'null') {
            return null;
        }
        return token;
    }

    isAuthenticated(): boolean {
        return !!this.getToken();
    }
}

export const authService = new AuthService();
