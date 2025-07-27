export interface User {
    userId: number;
    username: string;
    email: string;
    role?: string;
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
    methodID: number; // Match backend methodID
    methodName: string;
    isRush: boolean;
    shippingFees: number;
}

export interface DeliveryInformation {
    id: number; // Match backend
    name: string; // Match backend field names
    phone: string;
    address: string;
    province: string;
    instruction?: string;
    user?: User; // Reference to user
}

export interface Order {
    orderId: number;
    user?: User; // Nullable for guest orders
    transactionId?: string; // String type as per schema
    shippingMethod: ShippingMethod;
    shippingFees: number;
    totalAmount: number;
    createdAt: string;
    isPayment: boolean; // Match backend
    payment: boolean; // Alternative field name used by backend serialization
    // New fields from schema
    customerEmail: string;
    customerFullName: string;
    customerPhone: string;
    deliveryAddress: string;
    deliveryProvince: string;
    subtotalAmount: number;
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
    id: number; // Match backend field name
    order?: Order; // Reference to order
    productId: number;
    quantity: number;
    price: number; // Match backend field name
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

// Transaction Information
export interface TransactionInformation {
    transactionId: number;
    order?: Order;
    totalFee: number;
    status: string;
    transactionTime: string;
    content: string;
    paymentMethod: string;
    vnpTransactionNo?: string;
    vnpBankCode?: string;
    vnpBankTranNo?: string;
    vnpResponseCode?: string;
    orderReference?: string;
}

export interface PaymentStatistics {
    totalTransactions: number;
    successfulTransactions: number;
    failedTransactions: number;
    pendingTransactions: number;
    totalRevenue: number;
}

export interface OrderDetails {
    order: Order;
    orderItems: OrderItem[];
    transaction: TransactionInformation | null;
    hasTransaction: boolean;
}

export interface OrderStatistics {
    totalOrders: number;
    paidOrders: number;
    unpaidOrders: number;
    totalRevenue: number;
}
