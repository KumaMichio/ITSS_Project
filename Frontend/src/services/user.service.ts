import { apiService } from './api.service';
import { API_ENDPOINTS } from '../config/api';
import type { User } from '../types';

export class UserService {
    async getAllUsers() {
        return apiService.get<User[]>(API_ENDPOINTS.USERS.BASE);
    }

    async getUserById(id: number) {
        return apiService.get<User>(API_ENDPOINTS.USERS.BY_ID(id));
    }

    async updateUser(id: number, userData: Partial<Omit<User, 'userId'>>) {
        return apiService.put<User>(API_ENDPOINTS.USERS.UPDATE(id), userData);
    }

    async deleteUser(id: number) {
        return apiService.delete<void>(API_ENDPOINTS.USERS.DELETE(id));
    }

    async changeUserRole(id: number, role: string) {
        return apiService.put<User>(`${API_ENDPOINTS.USERS.CHANGE_ROLE(id)}?role=${encodeURIComponent(role)}`);
    }
}

export const userService = new UserService();
