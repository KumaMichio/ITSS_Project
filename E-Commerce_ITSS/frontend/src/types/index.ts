export interface User {
    userId: number;
    username: string;
    email: string;
}

export interface Product {
    id: number;
    title: string;
    price: number;
    category: string;
    imageURL: string;
    quantity: number;
    entryDate: string;
    dimension: number;
    weight: number;
    sellerId?: User;
}

export interface Book extends Product {
    author: string;
    publisher: string;
    coverType: string;
    publicationDate: string;
    pagesNumber: number;
    language: string;
    genre: string;
}

export interface CDLP extends Product {
    artist: string;
    recordLabel: string;
    tracklist: string;
}

export interface DVD extends Product {
    discType: string;
    director: string;
    runtime: string;
    studio: string;
    language: string;
    subtitles: string;
    releaseDate: string;
}

export interface ShippingMethod {
    methodId: number;
    methodName: string;
    isRush: boolean;
    shippingFees: number;
}

export interface DeliveryInformation {
    deliveryId: number;
    shippingMethodId: number;
    recipientName: string;
    email: string;
    phone: string;
    address: string;
    province: string;
    shippingFee: number;
    instruction?: string;
}

export interface Order {
    orderId: number;
    userId: number;
    deliveryId: number;
    transactionId: number;
    shippingFees: number;
    totalAmount: number;
    createdAt: string;
    vat: number;
    totalFees: number;
}

export interface Transaction {
    transactionId: number;
    orderId: number;
    amount: number;
    paymentMethod: string;
    status: string;
    createdAt: string;
    updatedAt?: string;
}

export interface OrderItem {
    orderItemId: number;
    orderId: number;
    productId: number;
    quantity: number;
    unitPrice: number;
    product?: Product;
}

export interface CartItem {
    productId: number;
    quantity: number;
    product: Product;
}

export interface CartValidationItem {
    productId: number;
    quantity: number;
    price: number;
}

export interface CartValidationResult {
    isValid: boolean;
    errors: string[];
    totalPrice: number;
    updatedTotalPrice: number;
}

// API Response Types
export interface ApiResponse<T> {
    success: boolean;
    data: T;
    message?: string;
    error?: string;
}

export interface LoginRequest {
    username: string;
    password: string;
}

export interface RegisterRequest {
    username: string;
    email: string;
    password: string;
}

export interface AuthResponse {
    token: string;
    user: User;
}

// Pagination
export interface PageRequest {
    page: number;
    size: number;
    sort?: string;
}

export interface PageResponse<T> {
    content: T[];
    totalElements: number;
    totalPages: number;
    size: number;
    number: number;
    first: boolean;
    last: boolean;
}
