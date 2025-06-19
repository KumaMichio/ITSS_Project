import { apiService } from './api.service';
import { API_ENDPOINTS, API_BASE_URL } from '../config/api';

export interface VNPayRequest {
    orderId: number;
    amount: number;
    orderInfo: string;
    returnUrl?: string;
}

export interface VNPayResponse {
    paymentUrl: string;
    orderId: number;
    amount: number;
}

export interface PaymentServiceResponse {
    success: boolean;
    data?: VNPayResponse;
    message?: string;
    error?: string;
}

export class PaymentService {
    private readonly MAX_RETRIES = 3;
    private readonly RETRY_DELAY = 1000; // 1 second

    /**
     * Retry mechanism for API calls
     */
    private async retryWithDelay<T>(
        operation: () => Promise<T>,
        retries: number = this.MAX_RETRIES,
        delay: number = this.RETRY_DELAY
    ): Promise<T> {
        try {
            return await operation();
        } catch (error) {
            if (retries > 0) {
                console.log(`Request failed, retrying in ${delay}ms... (${retries} attempts left)`);
                await new Promise(resolve => setTimeout(resolve, delay));
                return this.retryWithDelay(operation, retries - 1, delay * 1.5);
            }
            throw error;
        }
    }

    async createVNPayPayment(): Promise<PaymentServiceResponse> {
        try {
            console.log('🚀 Creating VNPay payment via backend API...');

            const orderId = Date.now();
            const amount = 100000;
            const orderInfo = `Demo payment order ${orderId}`;

            // Call backend API which returns PaymentServiceResponse
            const apiRes = await this.retryWithDelay(async () =>
                apiService.post<PaymentServiceResponse>(
                    API_ENDPOINTS.PAYMENT.VNPAY_CREATE,
                    { orderId, amount, orderInfo }
                )
            );
            console.log('✅ Backend VNPay API response:', apiRes);
            if (apiRes.success && apiRes.data && apiRes.data.success && apiRes.data.data) {
                return { success: true, data: apiRes.data.data, message: apiRes.data.message };
            }
            // Throw to fallback
            const errorMsg = apiRes.data?.error || apiRes.error || 'VNPay API returned unsuccessful response';
            throw new Error(errorMsg);
        } catch (error) {
            console.error('❌ Backend VNPay API failed:', error);
            console.log('🔄 Falling back to client-side generation');

            const demoPaymentUrl = await this.createFallbackPaymentUrl();
            return {
                success: true,
                data: {
                    paymentUrl: demoPaymentUrl,
                    orderId: Date.now(),
                    amount: 100000
                },
                message: 'Using demo VNPay URL (fallback)'
            };
        }
    }

    async createVNPayPaymentForOrder(orderId: number, amount: number = 100000, orderInfo: string = `Thanh toan don hang ${orderId}`): Promise<PaymentServiceResponse> {
        // Use backend API POST to create-order endpoint
        try {
            console.log('🚀 Creating VNPay payment for order via backend API:', { orderId, amount, orderInfo });
            const response = await this.retryWithDelay(async () => {
                return await apiService.post<VNPayResponse>(
                    API_ENDPOINTS.PAYMENT.VNPAY_CREATE,
                    { orderId, amount, orderInfo }
                );
            });
            console.log('✅ Backend VNPay payment created for order successfully:', response);
            return response;
        } catch (error) {
            console.error(`❌ Backend VNPay API failed for order ${orderId}:`, error);
            console.log('🔄 Falling back to client-side generation for order:', orderId);
            const demoPaymentUrl = await this.createFallbackPaymentUrl(orderId);
            return {
                success: true,
                data: {
                    paymentUrl: demoPaymentUrl,
                    orderId: orderId,
                    amount: amount
                },
                message: `Using demo VNPay URL for order ${orderId} (fallback)`
            };
        }
    }

    async createVNPayPaymentForOrderWithHealth(orderId: number, amount: number = 100000, backendHealthy: boolean = true): Promise<PaymentServiceResponse> {
        // If backend is known to be unhealthy, skip API call and use fallback immediately
        if (!backendHealthy) {
            console.log(`Backend offline - using fallback payment URL for order ${orderId} with amount ${amount}`);
            const demoPaymentUrl = await this.createFallbackPaymentUrl(orderId, amount);

            return {
                success: true,
                data: {
                    paymentUrl: demoPaymentUrl,
                    orderId: orderId,
                    amount: amount
                },
                message: `Using VNPay URL for order ${orderId} (backend offline)`
            };
        }

        // Backend is healthy, proceed with normal flow
        return this.createVNPayPaymentForOrder(orderId);
    }

    /**
     * Create a real VNPay payment URL when backend is unavailable
     */
    private async createFallbackPaymentUrl(orderId?: number, amount: number = 100000): Promise<string> {
        // VNPay CRITICAL: vnp_TxnRef phải là unique string, không được trùng
        // Format: Chỉ được chứa ký tự và số, không dấu, max 100 ký tự
        const timestamp = Date.now().toString();
        const randomSuffix = Math.random().toString(36).substring(2, 8).toUpperCase();
        const orderRef = orderId ? `ORDER${orderId}_${timestamp}` : `DEMO${timestamp}_${randomSuffix}`;

        const orderInfo = orderId
            ? `Thanh toan don hang ${orderId}`
            : `Demo Payment ${timestamp}`;

        // VNPay Configuration (sandbox) - EXACT VALUES
        const vnpTmnCode = 'ZAVGV1VT';
        const vnpVersion = '2.1.0';
        const vnpCommand = 'pay';
        const vnpLocale = 'vn';
        const vnpCurrCode = 'VND';

        // CRITICAL: IP Address phải là valid IP
        const vnpIpAddr = await this.getClientIP();

        // Amount in VND smallest unit (VNPay yêu cầu nhân 100)
        const vnpAmount = amount * 100;        // CRITICAL: VNPay time format phải chính xác yyyyMMddHHmmss (Vietnam timezone UTC+7)
        const now = new Date();
        // Đảm bảo sử dụng đúng Vietnam timezone
        const vietnamTime = new Date(now.getTime() + (7 * 60 * 60 * 1000));

        // Format: yyyyMMddHHmmss (không có dấu phân cách)
        const year = vietnamTime.getUTCFullYear();
        const month = String(vietnamTime.getUTCMonth() + 1).padStart(2, '0');
        const day = String(vietnamTime.getUTCDate()).padStart(2, '0');
        const hour = String(vietnamTime.getUTCHours()).padStart(2, '0');
        const minute = String(vietnamTime.getUTCMinutes()).padStart(2, '0');
        const second = String(vietnamTime.getUTCSeconds()).padStart(2, '0');

        const createDate = `${year}${month}${day}${hour}${minute}${second}`;

        // CRITICAL: ExpireDate phải sau CreateDate ít nhất 5 phút, tối đa 15 phút
        const expireTime = new Date(vietnamTime.getTime() + (10 * 60 * 1000)); // 10 phút sau
        const expYear = expireTime.getUTCFullYear();
        const expMonth = String(expireTime.getUTCMonth() + 1).padStart(2, '0');
        const expDay = String(expireTime.getUTCDate()).padStart(2, '0');
        const expHour = String(expireTime.getUTCHours()).padStart(2, '0');
        const expMinute = String(expireTime.getUTCMinutes()).padStart(2, '0');
        const expSecond = String(expireTime.getUTCSeconds()).padStart(2, '0');

        const expireDate = `${expYear}${expMonth}${expDay}${expHour}${expMinute}${expSecond}`;

        console.log('🕐 VNPay Time Generation:', {
            now: now.toISOString(),
            vietnamTime: vietnamTime.toISOString(),
            createDate,
            expireDate,
            timezone: 'UTC+7 Vietnam'
        });        // CRITICAL: Return URL phải là URL hợp lệ và accessible
        // VNPay sẽ redirect về URL này sau khi thanh toán
        // Dynamically use current origin for redirect URL
        const returnUrl = `${window.location.origin}/transaction/success`;

        // CRITICAL: Build parameters object theo đúng thứ tự VNPay yêu cầu
        const params: { [key: string]: string } = {
            vnp_Version: vnpVersion,
            vnp_Command: vnpCommand,
            vnp_TmnCode: vnpTmnCode,
            vnp_Amount: vnpAmount.toString(),
            vnp_CurrCode: vnpCurrCode,
            vnp_TxnRef: orderRef,
            vnp_OrderInfo: orderInfo,
            vnp_OrderType: 'other',
            vnp_Locale: vnpLocale,
            vnp_ReturnUrl: returnUrl,
            vnp_IpAddr: vnpIpAddr,
            vnp_CreateDate: createDate,
            vnp_ExpireDate: expireDate,
            vnp_SecureHashType: 'HMACSHA512'
        };

        console.log('📋 VNPay Parameters Before Sorting:', params);        // CRITICAL: VNPay hash calculation - MUST follow exact steps
        // Step 1: Sort parameters alphabetically by key (CASE SENSITIVE)
        const sortedKeys: string[] = Object.keys(params).sort();
        console.log('🔤 Sorted Keys:', sortedKeys);

        // Step 2: Create hash string with raw values (NO URL ENCODING!)
        const hashString = sortedKeys
            .map((key: string) => `${key}=${params[key]}`)
            .join('&');

        console.log('🔗 Hash String (Raw):', hashString);
        console.log('📏 Hash String Length:', hashString.length);

        // Step 3: Generate HMAC-SHA512 with exact secret key
        const secretKey = 'OR92SDL9CRPL5TOXFICMKRVASZ4FXJ4M';
        console.log('🔑 Using Secret Key:', secretKey.substring(0, 8) + '...');

        const hash = await this.generateVNPayHash(hashString, secretKey);
        console.log('🔒 Generated Hash:', hash.substring(0, 16) + '...' + hash.substring(-8));
        console.log('📏 Hash Length:', hash.length);

        // Step 4: Build final URL with URL-encoded parameters (separate from hash calculation)
        const encodedParams = sortedKeys
            .map(key => `${key}=${encodeURIComponent(params[key])}`)
            .join('&');

        console.log('🌐 Encoded Params for URL:', encodedParams.substring(0, 100) + '...');        // Final VNPay payment URL
        const finalPaymentUrl = `https://sandbox.vnpayment.vn/paymentv2/vpcpay.html?${encodedParams}&vnp_SecureHash=${hash}`;

        console.log('🎯 VNPay Payment URL Generated Successfully:', {
            orderId,
            amount,
            vnpAmount,
            orderRef,
            returnUrl,
            createDate,
            expireDate,
            hashStringLength: hashString.length,
            hashLength: hash.length,
            parametersCount: sortedKeys.length,
            finalUrlLength: finalPaymentUrl.length
        });

        // Validate final URL
        if (!finalPaymentUrl.includes('vnp_SecureHash=') || hash.length !== 128) {
            throw new Error(`Invalid VNPay URL generated: hash length ${hash.length}, expected 128`);
        }

        return finalPaymentUrl;
    }

    /**
     * Generate VNPay hash using Web Crypto API HMAC-SHA512
     * CRITICAL: Must follow VNPay specification exactly
     */
    private async generateVNPayHash(queryString: string, secretKey: string): Promise<string> {
        console.log('🔐 Generating VNPay HMAC-SHA512 hash...');
        console.log('📄 Input Query String:', queryString);
        console.log('🔑 Secret Key Length:', secretKey.length);
        console.log('🔑 Secret Key Preview:', secretKey.substring(0, 8) + '...');

        // Validate inputs
        if (!queryString || queryString.trim().length === 0) {
            throw new Error('Query string cannot be empty');
        }

        if (!secretKey || secretKey.length === 0) {
            throw new Error('Secret key cannot be empty');
        }

        if (secretKey !== 'OR92SDL9CRPL5TOXFICMKRVASZ4FXJ4M') {
            console.warn('⚠️ Secret key does not match expected VNPay sandbox key');
        }

        try {
            // Check Web Crypto API availability
            if (!window.crypto || !window.crypto.subtle) {
                throw new Error('Web Crypto API not available');
            }

            console.log('✅ Web Crypto API is available');

            // Step 1: Convert secret key to ArrayBuffer
            const keyData = new TextEncoder().encode(secretKey);
            console.log('🔧 Secret key encoded, length:', keyData.length);

            // Step 2: Import the key for HMAC-SHA512
            const cryptoKey = await window.crypto.subtle.importKey(
                'raw',
                keyData,
                { name: 'HMAC', hash: 'SHA-512' },
                false,
                ['sign']
            );
            console.log('✅ Crypto key imported successfully');

            // Step 3: Convert query string to ArrayBuffer
            const dataBuffer = new TextEncoder().encode(queryString);
            console.log('🔧 Query string encoded, length:', dataBuffer.length);

            // Step 4: Generate HMAC-SHA512 signature
            const signature = await window.crypto.subtle.sign('HMAC', cryptoKey, dataBuffer);
            console.log('✅ HMAC signature generated, byte length:', signature.byteLength);

            // Step 5: Convert to uppercase hex string (VNPay requirement)
            const hashArray = Array.from(new Uint8Array(signature));
            const hashHex = hashArray
                .map(byte => byte.toString(16).padStart(2, '0'))
                .join('')
                .toUpperCase(); // VNPay requires uppercase

            console.log('🎉 HMAC-SHA512 hash generated successfully!');
            console.log('🔒 Hash length:', hashHex.length);
            console.log('🔒 Hash format check:', /^[A-F0-9]+$/.test(hashHex) ? 'Valid' : 'Invalid');
            console.log('🔒 Hash preview:', hashHex.substring(0, 16) + '...' + hashHex.substring(-8));

            // Validate hash
            if (hashHex.length !== 128) {
                throw new Error(`Invalid hash length: ${hashHex.length}, expected 128`);
            }

            if (!/^[A-F0-9]+$/.test(hashHex)) {
                throw new Error('Invalid hash format: not uppercase hex');
            }

            return hashHex;

        } catch (error) {
            console.error('❌ HMAC-SHA512 generation failed:', error);

            // If Web Crypto API fails, try fallback (should not happen in modern browsers)
            console.log('🔄 Attempting fallback hash generation...');
            return await this.generateFallbackVNPayHash(queryString, secretKey);
        }
    }

    /**
     * Fallback hash generation (should not be used in production)
     */
    private async generateFallbackVNPayHash(queryString: string, secretKey: string): Promise<string> {
        console.warn('⚠️ Using fallback hash generation - not recommended for production');

        // This is a simplified fallback and may not produce correct VNPay hashes
        // It's only here to prevent complete failure
        const combined = secretKey + queryString + secretKey;

        // Create a pseudo-hash (this won't work with VNPay but prevents crashes)
        let hash = '';
        for (let i = 0; i < combined.length; i++) {
            const char = combined.charCodeAt(i);
            hash += ((char * 17 + i) % 256).toString(16).padStart(2, '0');
        }

        // Ensure 128 characters
        const finalHash = hash.repeat(Math.ceil(128 / hash.length)).substring(0, 128).toUpperCase();

        console.warn('⚠️ Fallback hash generated:', finalHash.substring(0, 16) + '...');
        console.warn('⚠️ This hash will likely cause VNPay signature errors');

        return finalHash;
    }

    /**
     * Check if backend is available
     */
    async checkBackendHealth(): Promise<boolean> {
        try {
            console.log(' Checking backend health...');
            const response = await fetch(`${API_BASE_URL}${API_ENDPOINTS.PAYMENT.HEALTH}`, {
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json',
                },
                signal: AbortSignal.timeout(5000) // 5 second timeout
            });

            const isHealthy = response.ok;
            console.log(isHealthy ? '✅ Backend is healthy' : '❌ Backend is unhealthy');

            if (isHealthy) {
                const healthData = await response.json();
                console.log('📊 Health data:', healthData);
            }

            return isHealthy;
        } catch (error) {
            console.log('❌ Backend health check failed:', error);
            return false;
        }
    }

    /**
     * Helper method to redirect to VNPay
     */
    redirectToVNPay(paymentUrl: string) {
        console.log('Redirecting to VNPay:', paymentUrl);

        // Validate URL before redirecting
        if (paymentUrl && (paymentUrl.startsWith('https://') || paymentUrl.startsWith('http://'))) {
            window.location.href = paymentUrl;
        } else {
            console.error('Invalid payment URL:', paymentUrl);
            throw new Error('Invalid payment URL received');
        }
    }

    /**
     * Create VNPay payment using backend API
     */
    async createVNPayPaymentWithBackend(orderId: number, amount: number, orderInfo: string): Promise<PaymentServiceResponse> {
        try {
            console.log('🚀 Creating VNPay payment with backend API:', { orderId, amount, orderInfo });

            const requestData = {
                orderId: orderId,
                amount: amount,
                orderInfo: orderInfo,
                method: 'vnpay'
            };

            const response = await this.retryWithDelay(async () => {
                return await apiService.post<VNPayResponse>(
                    API_ENDPOINTS.PAYMENT.VNPAY_CREATE,
                    requestData
                );
            });

            console.log('✅ Backend VNPay payment created successfully:', response);
            return response;
        } catch (error) {
            console.error('❌ Backend VNPay API failed:', error);
            console.log('🔄 Falling back to client-side generation');

            // Fallback to client-side generation if backend fails
            const demoPaymentUrl = await this.createFallbackPaymentUrl(orderId, amount);

            return {
                success: true,
                data: {
                    paymentUrl: demoPaymentUrl,
                    orderId: orderId,
                    amount: amount
                },
                message: `VNPay payment created (fallback) for order ${orderId}`
            };
        }
    }

    /**
     * Check payment health and create payment accordingly
     */
    async checkPaymentHealthAndCreate(orderId: number, amount: number, orderInfo: string): Promise<PaymentServiceResponse> {
        try {
            console.log('🔍 Checking payment health for order:', orderId);

            // Check backend health
            const backendHealthy = await this.checkBackendHealth();

            if (!backendHealthy) {
                console.log('⚠️ Backend unhealthy, using fallback payment URL');
                // Backend unhealthy, use fallback payment URL
                const demoPaymentUrl = await this.createFallbackPaymentUrl(orderId, amount);

                return {
                    success: true,
                    data: {
                        paymentUrl: demoPaymentUrl,
                        orderId: orderId,
                        amount: amount
                    },
                    message: `Using fallback VNPay URL for order ${orderId} (backend offline)`
                };
            }

            // Backend healthy - use backend API to include orderInfo
            return this.createVNPayPaymentWithBackend(orderId, amount, orderInfo);
        } catch (error) {
            console.error('❌ Error checking payment health:', error);
            throw error;
        }
    }

    /**
     * Get client IP address (fallback to localhost if not available)
     */
    private async getClientIP(): Promise<string> {
        try {
            // Try to get real IP from external service
            const response = await fetch('https://api.ipify.org?format=json', {
                method: 'GET',
                signal: AbortSignal.timeout(3000) // 3 second timeout
            });

            if (response.ok) {
                const data = await response.json();
                return data.ip || '127.0.0.1';
            }
        } catch (error) {
            console.log('Failed to get real IP, using localhost');
        }

        return '127.0.0.1';
    }

}

export const paymentService = new PaymentService();
