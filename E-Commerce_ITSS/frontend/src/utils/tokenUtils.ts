// Token utility functions for JWT handling

export interface DecodedToken {
    exp: number;
    iat: number;
    sub: string;
    username?: string;
    role?: string;
}

/**
 * Decode JWT token without verification (client-side only)
 */
export const decodeJWT = (token: string): DecodedToken | null => {
    try {
        const parts = token.split('.');
        if (parts.length !== 3) {
            console.warn('Invalid JWT format');
            return null;
        }

        const payload = parts[1];
        const decoded = JSON.parse(atob(payload.replace(/-/g, '+').replace(/_/g, '/')));
        return decoded;
    } catch (error) {
        console.error('Failed to decode JWT:', error);
        return null;
    }
};

/**
 * Check if JWT token is expired
 */
export const isTokenExpired = (token: string): boolean => {
    try {
        const decoded = decodeJWT(token);
        if (!decoded || !decoded.exp) {
            console.warn('Token missing expiration');
            return true;
        }

        const currentTime = Date.now() / 1000; // Convert to seconds
        const isExpired = decoded.exp < currentTime;

        console.log('Token expiration check:', {
            currentTime: new Date(currentTime * 1000).toISOString(),
            expiration: new Date(decoded.exp * 1000).toISOString(),
            isExpired
        });

        return isExpired;
    } catch (error) {
        console.error('Error checking token expiration:', error);
        return true;
    }
};

/**
 * Get time until token expires (in seconds)
 */
export const getTokenTimeLeft = (token: string): number => {
    try {
        const decoded = decodeJWT(token);
        if (!decoded || !decoded.exp) {
            return 0;
        }

        const currentTime = Date.now() / 1000;
        const timeLeft = Math.max(0, decoded.exp - currentTime);

        return timeLeft;
    } catch (error) {
        console.error('Error getting token time left:', error);
        return 0;
    }
};

/**
 * Check if token is valid (exists and not expired)
 */
export const isTokenValid = (token: string | null): boolean => {
    if (!token) {
        console.log('No token provided');
        return false;
    }

    if (isTokenExpired(token)) {
        console.log('Token is expired');
        return false;
    }

    console.log('Token is valid');
    return true;
};

/**
 * Get current token from localStorage and check validity
 */
export const getCurrentTokenInfo = () => {
    const token = localStorage.getItem('authToken');

    if (!token) {
        return {
            hasToken: false,
            isValid: false,
            timeLeft: 0,
            decoded: null
        };
    }

    const decoded = decodeJWT(token);
    const isValid = isTokenValid(token);
    const timeLeft = getTokenTimeLeft(token);

    return {
        hasToken: true,
        isValid,
        timeLeft,
        decoded,
        token: token.substring(0, 20) + '...' // Only log first 20 chars for security
    };
};
