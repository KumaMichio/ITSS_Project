import { useEffect, useState, useRef } from 'react';
import { useSearchParams, useNavigate } from 'react-router-dom';
import { useCart } from '../../contexts/CartContext';
import Header from '../../components/Header';

const TransactionResult = () => {
    const [searchParams] = useSearchParams();
    const navigate = useNavigate();
    const { clearCart } = useCart();
    const hasSavedTransaction = useRef(false); // Prevent duplicate saves
    const hasCartCleared = useRef(false); // Prevent duplicate cart clearing
    const [transactionData, setTransactionData] = useState<{
        orderId?: string;
        amount?: string;
        responseCode?: string;
        transactionNo?: string;
        bankCode?: string;
        bankTranNo?: string;
        payDate?: string;
        error?: string;
        isSuccess: boolean;
    }>({ isSuccess: false });

    useEffect(() => {
        // Parse VNPay transaction data from URL parameters
        const vnpAmount = searchParams.get('vnp_Amount');
        const vnpResponseCode = searchParams.get('vnp_ResponseCode');
        const vnpTransactionStatus = searchParams.get('vnp_TransactionStatus');
        const vnpTxnRef = searchParams.get('vnp_TxnRef'); // Order ID
        const vnpTransactionNo = searchParams.get('vnp_TransactionNo');
        const vnpBankCode = searchParams.get('vnp_BankCode');
        const vnpBankTranNo = searchParams.get('vnp_BankTranNo');
        const vnpPayDate = searchParams.get('vnp_PayDate');
        const vnpOrderInfo = searchParams.get('vnp_OrderInfo');

        // Check if transaction was successful
        // VNPay: ResponseCode = "00" means success
        const isSuccess = vnpResponseCode === '00' && vnpTransactionStatus === '00';

        setTransactionData({
            orderId: vnpTxnRef || undefined,
            amount: vnpAmount || undefined,
            responseCode: vnpResponseCode || undefined,
            transactionNo: vnpTransactionNo || undefined,
            bankCode: vnpBankCode || undefined,
            bankTranNo: vnpBankTranNo || undefined,
            payDate: vnpPayDate || undefined,
            error: !isSuccess ? 'Giao dịch không thành công' : undefined,
            isSuccess
        });

        // Clear cart if transaction is successful
        if (isSuccess && !hasCartCleared.current) {
            console.log('🛒 Clearing cart after successful payment');
            hasCartCleared.current = true;
            try {
                clearCart();
                console.log('✅ Cart cleared successfully');
            } catch (error) {
                console.error('❌ Failed to clear cart:', error);
            }
        }

        // Save transaction to database if successful
        if (isSuccess && vnpTxnRef && vnpAmount && !hasSavedTransaction.current) {
            console.log('🔄 Preparing to save transaction (first time)');
            hasSavedTransaction.current = true; // Mark as being saved to prevent duplicates
            saveTransactionToDatabase({
                vnp_TxnRef: vnpTxnRef,
                vnp_Amount: vnpAmount,
                vnp_ResponseCode: vnpResponseCode || '',
                vnp_TransactionNo: vnpTransactionNo || '',
                vnp_BankCode: vnpBankCode || '',
                vnp_BankTranNo: vnpBankTranNo || '',
                vnp_OrderInfo: vnpOrderInfo || `Thanh toan don hang ${vnpTxnRef}`
            });
        } else if (hasSavedTransaction.current) {
            console.log('⚠️ Transaction already saved, skipping duplicate save');
        }

        // Auto redirect to home after 15 seconds
        const timer = setTimeout(() => {
            navigate('/');
        }, 15000);

        return () => clearTimeout(timer);
    }, [searchParams, navigate, clearCart]);

    const saveTransactionToDatabase = async (transactionParams: Record<string, string>) => {
        try {
            console.log('🔄 Starting transaction save process...');
            console.log('🔄 Transaction params:', transactionParams);
            console.log('🔄 Backend URL:', 'http://localhost:8080/api/payment/vnpay/save-transaction');

            // Check if backend is reachable
            console.log('🔄 Testing backend connection...');

            const response = await fetch('http://localhost:8080/api/payment/vnpay/save-transaction', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Accept': 'application/json',
                },
                body: JSON.stringify(transactionParams)
            });

            console.log('📡 Response received:', {
                status: response.status,
                statusText: response.statusText,
                ok: response.ok,
                headers: Object.fromEntries(response.headers.entries())
            });

            if (!response.ok) {
                const errorText = await response.text();
                console.error('❌ HTTP Error:', response.status, response.statusText, errorText);
                throw new Error(`HTTP ${response.status}: ${response.statusText}`);
            }

            const result = await response.json();
            console.log('📡 Response data:', result);

            if (result.success) {
                console.log('✅ Transaction saved successfully:', result);
                console.log('✅ Transaction ID:', result.transactionId);
            } else {
                console.error('❌ Backend reported failure:', result.error);
            }
        } catch (error) {
            console.error('❌ Network/Parse Error saving transaction:', error);
            console.error('❌ Error details:', {
                name: error instanceof Error ? error.name : 'Unknown',
                message: error instanceof Error ? error.message : String(error),
                stack: error instanceof Error ? error.stack : undefined
            });

            // Try to test if backend is running
            try {
                const healthCheck = await fetch('http://localhost:8080/api/payment/vnpay', {
                    method: 'GET',
                    mode: 'cors'
                });
                console.log('🔍 Backend health check:', healthCheck.status);
            } catch (healthError) {
                console.error('❌ Backend appears to be down:', healthError);
            }
        }
    };

    const formatAmount = (amount: string | undefined) => {
        if (!amount) return 'N/A';
        try {
            const numAmount = parseInt(amount);
            return new Intl.NumberFormat('vi-VN', {
                style: 'currency',
                currency: 'VND'
            }).format(numAmount / 100); // VNPay returns amount in smallest unit
        } catch {
            return amount;
        }
    };

    const getErrorMessage = (error: string | undefined, responseCode: string | undefined) => {
        if (responseCode === '07') return 'Trừ tiền thành công. Giao dịch bị nghi ngờ (liên quan tới lừa đảo, giao dịch bất thường).';
        if (responseCode === '09') return 'Giao dịch không thành công do: Thẻ/Tài khoản của khách hàng chưa đăng ký dịch vụ InternetBanking tại ngân hàng.';
        if (responseCode === '10') return 'Giao dịch không thành công do: Khách hàng xác thực thông tin thẻ/tài khoản không đúng quá 3 lần';
        if (responseCode === '11') return 'Giao dịch không thành công do: Đã hết hạn chờ thanh toán. Xin quý khách vui lòng thực hiện lại giao dịch.';
        if (responseCode === '12') return 'Giao dịch không thành công do: Thẻ/Tài khoản của khách hàng bị khóa.';
        if (responseCode === '13') return 'Giao dịch không thành công do Quý khách nhập sai mật khẩu xác thực giao dịch (OTP).';
        if (responseCode === '24') return 'Giao dịch không thành công do: Khách hàng hủy giao dịch';
        if (responseCode === '51') return 'Giao dịch không thành công do: Tài khoản của quý khách không đủ số dư để thực hiện giao dịch.';
        if (responseCode === '65') return 'Giao dịch không thành công do: Tài khoản của Quý khách đã vượt quá hạn mức giao dịch trong ngày.';
        if (responseCode === '75') return 'Ngân hàng thanh toán đang bảo trì.';
        if (responseCode === '79') return 'Giao dịch không thành công do: KH nhập sai mật khẩu thanh toán quá số lần quy định.';
        if (responseCode && responseCode !== '00') return `Giao dịch thất bại với mã lỗi: ${responseCode}`;
        return error || 'Lỗi không xác định';
    };

    const formatPayDate = (payDate: string | undefined) => {
        if (!payDate) return 'N/A';
        try {
            // VNPay format: YYYYMMDDHHMMSS
            const year = payDate.substring(0, 4);
            const month = payDate.substring(4, 6);
            const day = payDate.substring(6, 8);
            const hour = payDate.substring(8, 10);
            const minute = payDate.substring(10, 12);
            const second = payDate.substring(12, 14);

            return `${day}/${month}/${year} ${hour}:${minute}:${second}`;
        } catch {
            return payDate;
        }
    };

    return (
        <div className="min-h-screen bg-gray-50">
            <Header />

            <div className="container mx-auto px-4 py-16">
                <div className="max-w-md mx-auto bg-white rounded-lg shadow-lg p-8 text-center">
                    {transactionData.isSuccess ? (
                        <>
                            {/* Success State */}
                            <div className="mb-6">
                                <div className="w-16 h-16 mx-auto mb-4 bg-green-100 rounded-full flex items-center justify-center">
                                    <i className="fas fa-check-circle text-3xl text-green-500"></i>
                                </div>
                                <h1 className="text-2xl font-bold text-green-600 mb-2">
                                    Thanh toán thành công!
                                </h1>
                                <p className="text-gray-600">
                                    Giao dịch của bạn đã được xử lý thành công.
                                </p>
                            </div>

                            <div className="bg-green-50 p-4 rounded-lg mb-6">
                                <div className="space-y-2 text-sm">
                                    {transactionData.orderId && (
                                        <div className="flex justify-between">
                                            <span className="font-medium">Mã đơn hàng:</span>
                                            <span>#{transactionData.orderId}</span>
                                        </div>
                                    )}
                                    {transactionData.amount && (
                                        <div className="flex justify-between">
                                            <span className="font-medium">Số tiền:</span>
                                            <span className="font-bold text-green-600">
                                                {formatAmount(transactionData.amount)}
                                            </span>
                                        </div>
                                    )}
                                    {transactionData.transactionNo && (
                                        <div className="flex justify-between">
                                            <span className="font-medium">Mã giao dịch VNPay:</span>
                                            <span>{transactionData.transactionNo}</span>
                                        </div>
                                    )}
                                    {transactionData.bankCode && (
                                        <div className="flex justify-between">
                                            <span className="font-medium">Ngân hàng:</span>
                                            <span>{transactionData.bankCode}</span>
                                        </div>
                                    )}
                                    {transactionData.bankTranNo && (
                                        <div className="flex justify-between">
                                            <span className="font-medium">Mã giao dịch ngân hàng:</span>
                                            <span>{transactionData.bankTranNo}</span>
                                        </div>
                                    )}
                                    {transactionData.payDate && (
                                        <div className="flex justify-between">
                                            <span className="font-medium">Thời gian thanh toán:</span>
                                            <span>{formatPayDate(transactionData.payDate)}</span>
                                        </div>
                                    )}
                                    <div className="flex justify-between">
                                        <span className="font-medium">Trạng thái:</span>
                                        <span className="text-green-600 font-medium">Thành công</span>
                                    </div>
                                </div>
                            </div>
                        </>
                    ) : (
                        <>
                            {/* Failure State */}
                            <div className="mb-6">
                                <div className="w-16 h-16 mx-auto mb-4 bg-red-100 rounded-full flex items-center justify-center">
                                    <i className="fas fa-times-circle text-3xl text-red-500"></i>
                                </div>
                                <h1 className="text-2xl font-bold text-red-600 mb-2">
                                    Thanh toán thất bại!
                                </h1>
                                <p className="text-gray-600">
                                    Đã xảy ra lỗi trong quá trình xử lý thanh toán.
                                </p>
                            </div>

                            <div className="bg-red-50 p-4 rounded-lg mb-6">
                                <div className="space-y-2 text-sm">
                                    {transactionData.orderId && (
                                        <div className="flex justify-between">
                                            <span className="font-medium">Mã đơn hàng:</span>
                                            <span>#{transactionData.orderId}</span>
                                        </div>
                                    )}
                                    {transactionData.amount && (
                                        <div className="flex justify-between">
                                            <span className="font-medium">Số tiền:</span>
                                            <span className="font-bold text-red-600">
                                                {formatAmount(transactionData.amount)}
                                            </span>
                                        </div>
                                    )}
                                    {transactionData.responseCode && (
                                        <div className="flex justify-between">
                                            <span className="font-medium">Mã lỗi:</span>
                                            <span>{transactionData.responseCode}</span>
                                        </div>
                                    )}
                                    <div className="flex justify-between">
                                        <span className="font-medium">Lý do:</span>
                                        <span className="text-red-600">
                                            {getErrorMessage(transactionData.error, transactionData.responseCode)}
                                        </span>
                                    </div>
                                    <div className="flex justify-between">
                                        <span className="font-medium">Trạng thái:</span>
                                        <span className="text-red-600 font-medium">Thất bại</span>
                                    </div>
                                </div>
                            </div>
                        </>
                    )}

                    {/* Action Buttons */}
                    <div className="space-y-3">
                        <button
                            onClick={() => navigate('/')}
                            className={`w-full py-3 px-4 rounded-lg font-medium transition ${transactionData.isSuccess
                                ? 'bg-green-600 hover:bg-green-700 text-white'
                                : 'bg-blue-600 hover:bg-blue-700 text-white'
                                }`}
                        >
                            <i className="fas fa-home mr-2"></i>
                            Về trang chủ
                        </button>

                        {!transactionData.isSuccess && (
                            <button
                                onClick={() => navigate('/checkout')}
                                className="w-full py-3 px-4 rounded-lg font-medium transition bg-gray-600 hover:bg-gray-700 text-white"
                            >
                                <i className="fas fa-redo mr-2"></i>
                                Thử lại thanh toán
                            </button>
                        )}

                        <button
                            onClick={() => navigate('/shop')}
                            className="w-full py-3 px-4 rounded-lg font-medium transition bg-gray-200 hover:bg-gray-300 text-gray-700"
                        >
                            <i className="fas fa-shopping-cart mr-2"></i>
                            Tiếp tục mua sắm
                        </button>
                    </div>

                    <div className="mt-6 text-xs text-gray-500">
                        <p>Trang này sẽ tự động chuyển về trang chủ sau 15 giây.</p>
                        <p className="mt-1">
                            <i className="fas fa-shield-alt mr-1"></i>
                            Giao dịch được bảo mật bởi VNPay
                        </p>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default TransactionResult;
