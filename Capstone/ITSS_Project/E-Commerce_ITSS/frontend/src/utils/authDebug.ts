// Debug utility to check authentication state
import { getCurrentTokenInfo, decodeJWT } from './tokenUtils';

export const debugAuth = () => {
    console.log('=== AUTH DEBUG ===');

    const token = localStorage.getItem('authToken');
    const user = localStorage.getItem('user');

    console.log('Token exists:', !!token);
    console.log('User data:', user);

    if (token) {
        const tokenInfo = getCurrentTokenInfo();
        console.log('Token info:', tokenInfo);

        const decoded = decodeJWT(token);
        console.log('Decoded token:', decoded);

        if (user && user !== 'undefined' && user !== 'null') {
            try {
                const userData = JSON.parse(user);
                console.log('Parsed user data:', userData);
                console.log('User role:', userData.role);
            } catch (e) {
                console.error('Error parsing user data:', e);
            }
        }
    }

    console.log('==================');
};

// Auto-run on import in development
if (import.meta.env.DEV) {
    debugAuth();
}
