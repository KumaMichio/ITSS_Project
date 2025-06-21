import { getCurrentTokenInfo } from '../utils/tokenUtils';

export const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080';

export const API_ENDPOINTS = {
    // Authentication
    AUTH: {
        LOGIN: '/api/auth/login',
        REGISTER: '/api/auth/signup', // Updated to match backend endpoint
        LOGOUT: '/api/auth/logout',
        CHANGE_PASSWORD: '/api/auth/change-password'
    },

    // Products
    PRODUCTS: {
        BASE: '/api/products',
        BY_ID: (id: number) => `/api/products/${id}`,
        SEARCH: (title: string) => `/api/products/search/${encodeURIComponent(title)}`,
        BY_CATEGORY: (category: string) => `/api/products/category/${encodeURIComponent(category)}`,
        CATEGORIES: '/api/products/categories',
        RANDOM: '/api/products/random',
        // Books
        BOOKS: {
            BASE: '/api/products/books',
            BY_ID: (id: number) => `/api/products/book/${id}`,
            SEARCH: (title: string) => `/api/products/book/search/${encodeURIComponent(title)}`,
            ADD: '/api/products/add-product/books',
            UPDATE: (id: number) => `/api/products/book/modify/${id}`,
            DELETE: (id: number) => `/api/products/book/delete/${id}`,
            DELETE_ALL: '/api/products/book/delete'
        },
        // DVDs
        DVDS: {
            BASE: '/api/products/dvd',
            BY_ID: (id: number) => `/api/products/dvd/${id}`,
            SEARCH: (title: string) => `/api/products/dvd/search/${encodeURIComponent(title)}`,
            ADD: '/api/products/add-product/dvd',
            UPDATE: (id: number) => `/api/products/dvd/modify/${id}`,
            DELETE: (id: number) => `/api/products/dvd/delete/${id}`
        },
        // CDLPs
        CDLPS: {
            BASE: '/api/products/cdlp',
            BY_ID: (id: number) => `/api/products/cdlp/${id}`,
            SEARCH: (title: string) => `/api/products/cdlp/search/${encodeURIComponent(title)}`,
            ADD: '/api/products/add-product/cdlp',
            UPDATE: (id: number) => `/api/products/cdlp/modify/${id}`,
            DELETE: (id: number) => `/api/products/cdlp/delete/${id}`,
            DELETE_ALL: '/api/products/cdlp/delete'
        }
    },

    // Users
    USERS: {
        BASE: '/api/user',
        BY_ID: (id: number) => `/api/user/${id}`,
        UPDATE: (id: number) => `/api/update-user/${id}`,
        DELETE: (id: number) => `/api/delete-user/${id}`,
        CHANGE_ROLE: (id: number) => `/api/user/role/${id}`
    },

    // Delivery Information
    DELIVERY_INFO: {
        BASE: '/api/delivery-info',
        BY_ID: (id: number) => `/api/delivery-info/${id}`,
        BY_USER: (userId: number) => `/api/delivery-info/user/${userId}`
    },

    // Shipping Methods
    SHIPPING_METHODS: {
        BASE: '/api/shipping-method'
    },

    // Transactions
    TRANSACTIONS: {
        BASE: '/transactions',
        BY_ORDER_ID: (orderId: number) => `/transactions/order/${orderId}`
    },

    // Orders
    ORDERS: {
        BASE: '/order',
        BY_ID: (id: number) => `/order/${id}`,
        CREATE: '/order/create',
        CREATE_BY_DELIVERY: '/order/create-by-delivery-id',
        CREATE_EXPRESS: '/order/create-express-order',
        UPDATE_STATUS: (id: number) => `/order/status/${id}`
    },

    // Cart
    CART: {
        BASE: '/api/cart',
        BY_ID: (id: number) => `/api/cart/${id}`,
        ADD: '/api/cart',
        ADD_MULTIPLE: '/api/cart/add',
        UPDATE: (id: number) => `/api/cart/${id}`,
        UPDATE_ORDER: (id: number) => `/api/cart/order/${id}`,
        DELETE: (id: number) => `/api/cart/${id}`,
        CLEAR: '/api/cart/clear'
    },

    // Payment - Updated to match new VNPayController endpoints
    PAYMENT: {
        VNPAY: '/api/payment/vnpay',
        VNPAY_ORDER: (orderId: number) => `/api/payment/vnpay/order/${orderId}`,
        VNPAY_CREATE: '/api/payment/vnpay/create-order',
        VNPAY_CALLBACK: '/api/payment/vnpay/callback',
        CREATE: '/api/payment/create',
        STATUS: (transactionId: string) => `/api/payment/status/${transactionId}`,
        CANCEL: (transactionId: string) => `/api/payment/cancel/${transactionId}`,
        REFUND: '/api/payment/refund',
        METHODS: '/api/payment/methods',
        HEALTH: '/api/payment/health'
    },

    // Guest Orders
    GUEST: {
        VALIDATE_STOCK: '/api/guest/validate-stock',
        SHIPPING_PREVIEW: '/api/guest/shipping-preview',
        CREATE_ORDER: '/api/guest/create-order'
    }
};

// HTTP Headers
export const getHeaders = (includeAuth = true) => {
    const headers: Record<string, string> = {
        'Content-Type': 'application/json',
    };

    if (includeAuth) {
        const tokenInfo = getCurrentTokenInfo();
        console.log('Token info for headers:', tokenInfo);

        if (tokenInfo.hasToken && tokenInfo.isValid) {
            const token = localStorage.getItem('authToken');
            if (token) {
                headers['Authorization'] = `Bearer ${token}`;
                console.log('Added valid token to headers');
            }
        } else if (tokenInfo.hasToken && !tokenInfo.isValid) {
            console.warn('Token exists but is invalid/expired, not including in headers');
            // Optionally clear expired token
            // localStorage.removeItem('authToken');
        } else {
            console.log('No token found, not including auth headers');
        }
    }

    return headers;
};
