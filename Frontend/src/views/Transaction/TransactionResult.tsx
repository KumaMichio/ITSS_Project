import { useEffect, useState } from 'react';
import { useSearchParams, useNavigate } from 'react-router-dom';
import Header from '../../components/Header';

const TransactionResult = () => {
    const [searchParams] = useSearchParams();
    const navigate = useNavigate();
    const [transactionData, setTransactionData] = useState<{
        orderId?: string;
        amount?: string;
        code?: string;
        error?: string;
        isSuccess: boolean;
    }>({ isSuccess: false });

    useEffect(() => {
        // Parse transaction data from URL parameters
        const orderId = searchParams.get('orderId');
        const amount = searchParams.get('amount');
        const code = searchParams.get('code');
        const error = searchParams.get('error');

        // Determine if transaction was successful based on current path
        const isSuccess = window.location.pathname.includes('success');

        setTransactionData({
            orderId: orderId || undefined,
            amount: amount || undefined,
            code: code || undefined,
            error: error || undefined,
            isSuccess
        });

        // Auto redirect to home after 10 seconds
        const timer = setTimeout(() => {
            navigate('/');
        }, 10000);

        return () => clearTimeout(timer);
    }, [searchParams, navigate]);

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

    const getErrorMessage = (error: string | undefined, code: string | undefined) => {
        if (error === 'invalid_signature') return 'Chữ ký không hợp lệ';
        if (error === 'callback_error') return 'Lỗi xử lý callback';
        if (error === 'invalid_order_id') return 'Mã đơn hàng không hợp lệ';
        if (code && code !== '00') return `Lỗi thanh toán với mã: ${code}`;
        return 'Lỗi không xác định';
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
                                    Giao dịch không thể được thực hiện.
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
                                    <div className="flex justify-between">
                                        <span className="font-medium">Lý do:</span>
                                        <span className="text-red-600">
                                            {getErrorMessage(transactionData.error, transactionData.code)}
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
                        <p>
                            <i className="fas fa-info-circle mr-1"></i>
                            Trang này sẽ tự động chuyển hướng sau 10 giây
                        </p>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default TransactionResult;
