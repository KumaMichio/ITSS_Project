import React, { createContext, useContext, useState, useEffect, ReactNode } from 'react';
import { authService } from '../services/auth.service';
import { getCurrentTokenInfo } from '../utils/tokenUtils';
import type { User, LoginRequest, RegisterRequest } from '../types';

interface AuthContextType {
    user: User | null;
    isAuthenticated: boolean;
    isLoading: boolean;
    login: (credentials: LoginRequest) => Promise<boolean>;
    register: (userData: RegisterRequest) => Promise<boolean>;
    logout: () => void;
    checkTokenValidity: () => boolean;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const useAuth = () => {
    const context = useContext(AuthContext);
    if (context === undefined) {
        throw new Error('useAuth must be used within an AuthProvider');
    }
    return context;
};

interface AuthProviderProps {
    children: ReactNode;
}

export const AuthProvider: React.FC<AuthProviderProps> = ({ children }) => {
    const [user, setUser] = useState<User | null>(null);
    const [isLoading, setIsLoading] = useState(true); useEffect(() => {
        // Check if user is already logged in and token is valid
        const currentUser = authService.getCurrentUser();
        const tokenInfo = getCurrentTokenInfo();

        console.log('AuthContext init - checking stored user and token:', {
            hasUser: !!currentUser,
            tokenInfo
        });

        if (currentUser && tokenInfo.hasToken && tokenInfo.isValid) {
            console.log('Valid user and token found, setting authenticated state');
            setUser(currentUser);
        } else if (currentUser && (!tokenInfo.hasToken || !tokenInfo.isValid)) {
            console.log('User found but token invalid, clearing user data');
            authService.clearStorage();
            setUser(null);
        } else {
            console.log('No valid user session found');
            setUser(null);
        }

        setIsLoading(false);
    }, []);

    const login = async (credentials: LoginRequest): Promise<boolean> => {
        try {
            setIsLoading(true);
            const response = await authService.login(credentials);

            if (response.success && response.data) {
                setUser(response.data.user);
                return true;
            }

            return false;
        } catch (error) {
            console.error('Login failed:', error);
            return false;
        } finally {
            setIsLoading(false);
        }
    };

    const register = async (userData: RegisterRequest): Promise<boolean> => {
        try {
            setIsLoading(true);
            const response = await authService.register(userData);

            if (response.success && response.data) {
                setUser(response.data.user);
                return true;
            }

            return false;
        } catch (error) {
            console.error('Registration failed:', error);
            return false;
        } finally {
            setIsLoading(false);
        }
    }; const logout = () => {
        console.log('AuthContext logout - clearing user and token');
        authService.logout();
        setUser(null);
    }; const checkTokenValidity = (): boolean => {
        const tokenInfo = getCurrentTokenInfo();
        console.log('Checking token validity:', tokenInfo);

        if (!tokenInfo.hasToken) {
            console.log('No token found');
            if (user) {
                console.log('Clearing user data (no token)');
                authService.clearStorage();
                setUser(null);
            }
            return false;
        }

        if (!tokenInfo.isValid) {
            console.log('Token is invalid or expired');
            // Clear expired token and user
            console.log('Clearing expired user session');
            authService.clearStorage();
            setUser(null);
            return false;
        }

        console.log(`Token is valid - ${Math.floor(tokenInfo.timeLeft / 60)} minutes left`);
        return true;
    };

    const value: AuthContextType = {
        user,
        isAuthenticated: !!user,
        isLoading,
        login,
        register,
        logout,
        checkTokenValidity,
    };

    return (
        <AuthContext.Provider value={value}>
            {children}
        </AuthContext.Provider>
    );
};
