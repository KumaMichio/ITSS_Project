import React, { useState, useEffect } from 'react';
import type {
    Order,
    OrderDetails,
    OrderStatistics,
    TransactionInformation,
    PaymentStatistics
} from '../../types';

const AdminOrderPaymentManagement: React.FC = () => {
    // Tab state
    const [activeTab, setActiveTab] = useState<'orders' | 'payments'>('orders');

    // Orders state
    const [orders, setOrders] = useState<Order[]>([]);
    const [orderStatistics, setOrderStatistics] = useState<OrderStatistics | null>(null);

    // Payments state
    const [payments, setPayments] = useState<TransactionInformation[]>([]);
    const [paymentStatistics, setPaymentStatistics] = useState<PaymentStatistics | null>(null);

    // Common state
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);

    // Order details modal
    const [showOrderDetails, setShowOrderDetails] = useState(false);
    const [selectedOrderDetails, setSelectedOrderDetails] = useState<OrderDetails | null>(null);
    const [detailsLoading, setDetailsLoading] = useState(false);

    // Refund modal
    const [showRefundModal, setShowRefundModal] = useState(false);
    const [selectedTransaction, setSelectedTransaction] = useState<TransactionInformation | null>(null);
    const [refundReason, setRefundReason] = useState('');
    const [refundLoading, setRefundLoading] = useState(false);

    // Pagination and filtering
    const [currentPage] = useState(0);
    const [totalPages, setTotalPages] = useState(0);
    const [totalItems, setTotalItems] = useState(0);
    const [pageSize] = useState(10);
    const [statusFilter, setStatusFilter] = useState('');
    const [searchTerm, setSearchTerm] = useState('');

    useEffect(() => {
        if (activeTab === 'orders') {
            fetchOrders();
            fetchOrderStatistics();
        } else {
            fetchPayments();
            fetchPaymentStatistics();
        }
    }, [activeTab, currentPage, statusFilter, searchTerm]);

    // Orders functions
    const fetchOrders = async () => {
        try {
            setLoading(true);
            setError(null);

            const response = await fetch('http://localhost:8080/api/admin/orders', {
                headers: {
                    'Authorization': `Bearer ${localStorage.getItem('authToken')}`,
                    'Content-Type': 'application/json'
                }
            });

            if (response.ok) {
                const data = await response.json();
                if (data.success) {
                    setOrders(data.content);
                } else {
                    setError(data.message || 'Lỗi khi tải danh sách đơn hàng');
                }
            } else {
                setError('Lỗi kết nối đến server');
            }
        } catch (error) {
            console.error('Error fetching orders:', error);
            setError('Lỗi không xác định khi tải dữ liệu');
        } finally {
            setLoading(false);
        }
    };

    const fetchOrderStatistics = async () => {
        try {
            const response = await fetch('http://localhost:8080/api/admin/orders/statistics', {
                headers: {
                    'Authorization': `Bearer ${localStorage.getItem('authToken')}`,
                    'Content-Type': 'application/json'
                }
            });

            if (response.ok) {
                const data = await response.json();
                if (data.success) {
                    setOrderStatistics(data.data);
                }
            }
        } catch (error) {
            console.error('Error fetching order statistics:', error);
        }
    };

    const fetchOrderDetails = async (orderId: number) => {
        try {
            setDetailsLoading(true);

            const response = await fetch(`http://localhost:8080/api/admin/orders/${orderId}`, {
                headers: {
                    'Authorization': `Bearer ${localStorage.getItem('authToken')}`,
                    'Content-Type': 'application/json'
                }
            });

            if (response.ok) {
                const data = await response.json();
                if (data.success) {
                    setSelectedOrderDetails(data.data);
                    setShowOrderDetails(true);
                } else {
                    alert('Lỗi: ' + data.message);
                }
            } else {
                alert('Lỗi kết nối đến server');
            }
        } catch (error) {
            console.error('Error fetching order details:', error);
            alert('Lỗi không xác định khi tải chi tiết đơn hàng');
        } finally {
            setDetailsLoading(false);
        }
    };

    // Payments functions
    const fetchPayments = async () => {
        try {
            setLoading(true);
            setError(null);

            const params = new URLSearchParams({
                page: currentPage.toString(),
                size: pageSize.toString(),
                sortBy: 'transactionTime',
                sortDir: 'desc'
            });

            if (statusFilter) {
                params.append('status', statusFilter);
            }

            if (searchTerm.trim()) {
                params.append('search', searchTerm.trim());
            }

            const response = await fetch(`http://localhost:8080/api/admin/payments?${params}`, {
                headers: {
                    'Authorization': `Bearer ${localStorage.getItem('authToken')}`,
                    'Content-Type': 'application/json'
                }
            });

            if (response.ok) {
                const data = await response.json();
                if (data.success) {
                    setPayments(data.content);
                    setTotalPages(data.totalPages);
                    setTotalItems(data.totalItems);
                } else {
                    setError(data.message || 'Lỗi khi tải danh sách thanh toán');
                }
            } else {
                setError('Lỗi kết nối đến server');
            }
        } catch (error) {
            console.error('Error fetching payments:', error);
            setError('Lỗi không xác định khi tải dữ liệu');
        } finally {
            setLoading(false);
        }
    };

    const fetchPaymentStatistics = async () => {
        try {
            const response = await fetch('http://localhost:8080/api/admin/payments/statistics', {
                headers: {
                    'Authorization': `Bearer ${localStorage.getItem('authToken')}`,
                    'Content-Type': 'application/json'
                }
            });

            if (response.ok) {
                const data = await response.json();
                if (data.success) {
                    setPaymentStatistics(data.data);
                }
            }
        } catch (error) {
            console.error('Error fetching payment statistics:', error);
        }
    };

    const handleRefund = async () => {
        if (!selectedTransaction) return;

        try {
            setRefundLoading(true);

            const response = await fetch(`http://localhost:8080/api/admin/payments/${selectedTransaction.transactionId}/refund`, {
                method: 'POST',
                headers: {
                    'Authorization': `Bearer ${localStorage.getItem('authToken')}`,
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({
                    reason: refundReason.trim() || 'Hoàn tiền theo yêu cầu'
                })
            });

            if (response.ok) {
                const data = await response.json();
                if (data.success) {
                    alert('Hoàn tiền thành công!');
                    setShowRefundModal(false);
                    setRefundReason('');
                    setSelectedTransaction(null);
                    fetchPayments();
                    fetchPaymentStatistics();
                } else {
                    alert('Lỗi: ' + data.message);
                }
            } else {
                alert('Lỗi kết nối đến server');
            }
        } catch (error) {
            console.error('Error processing refund:', error);
            alert('Lỗi không xác định khi xử lý hoàn tiền');
        } finally {
            setRefundLoading(false);
        }
    };

    // Utility functions
    const formatCurrency = (amount: number) => {
        return new Intl.NumberFormat('vi-VN', {
            style: 'currency',
            currency: 'VND'
        }).format(amount);
    };

    const formatDateTime = (dateTime: string) => {
        return new Date(dateTime).toLocaleString('vi-VN');
    };

    const getPaymentStatusBadge = (isPayment: boolean) => {
        return (
            <span className={`px-2 py-1 text-xs font-medium rounded-full ${isPayment
                ? 'bg-green-100 text-green-800'
                : 'bg-red-100 text-red-800'
                }`}>
                {isPayment ? 'Đã thanh toán' : 'Chưa thanh toán'}
            </span>
        );
    };

    const getTransactionStatusBadge = (status: string) => {
        const statusConfig = {
            'SUCCESS': { bg: 'bg-green-100', text: 'text-green-800', label: 'Thành công' },
            'PENDING': { bg: 'bg-yellow-100', text: 'text-yellow-800', label: 'Đang xử lý' },
            'FAILED': { bg: 'bg-red-100', text: 'text-red-800', label: 'Thất bại' },
            'REFUNDED': { bg: 'bg-purple-100', text: 'text-purple-800', label: 'Đã hoàn tiền' },
        };

        const config = statusConfig[status as keyof typeof statusConfig] ||
            { bg: 'bg-gray-100', text: 'text-gray-800', label: status };

        return (
            <span className={`px-2 py-1 text-xs font-medium rounded-full ${config.bg} ${config.text}`}>
                {config.label}
            </span>
        );
    };

    const getShippingMethodBadge = (methodId: number) => {
        const methodConfig = {
            1: { bg: 'bg-blue-100', text: 'text-blue-800', label: 'Giao hàng tiêu chuẩn' },
            2: { bg: 'bg-green-100', text: 'text-green-800', label: 'Giao hàng nhanh 2h' },
        };

        const config = methodConfig[methodId as keyof typeof methodConfig] ||
            { bg: 'bg-gray-100', text: 'text-gray-800', label: 'Không xác định' };

        return (
            <span className={`px-2 py-1 text-xs font-medium rounded-full ${config.bg} ${config.text}`}>
                {config.label}
            </span>
        );
    };

    const closeOrderDetails = () => {
        setShowOrderDetails(false);
        setSelectedOrderDetails(null);
    };

    if (loading && (orders.length === 0 && payments.length === 0)) {
        return (
            <div className="flex justify-center items-center h-64">
                <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-[#088178]"></div>
            </div>
        );
    }

    return (
        <div className="p-6">
            <div className="flex justify-between items-center mb-6">
                <h1 className="text-3xl font-bold text-gray-800">Quản Lý Đơn Hàng & Thanh Toán</h1>
                <button
                    onClick={() => {
                        if (activeTab === 'orders') {
                            fetchOrders();
                            fetchOrderStatistics();
                        } else {
                            fetchPayments();
                            fetchPaymentStatistics();
                        }
                    }}
                    className="bg-[#088178] text-white px-4 py-2 rounded hover:bg-[#066e6a] transition flex items-center"
                >
                    <svg className="w-4 h-4 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15" />
                    </svg>
                    Làm mới
                </button>
            </div>

            {error && (
                <div className="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded mb-6">
                    <p className="font-bold">Lỗi</p>
                    <p>{error}</p>
                </div>
            )}

            {/* Tab Navigation */}
            <div className="mb-6">
                <div className="border-b border-gray-200">
                    <nav className="-mb-px flex space-x-8">
                        <button
                            onClick={() => setActiveTab('orders')}
                            className={`py-2 px-1 border-b-2 font-medium text-sm ${activeTab === 'orders'
                                ? 'border-[#088178] text-[#088178]'
                                : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300'
                                }`}
                        >
                            <svg className="w-5 h-5 inline mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
                            </svg>
                            Quản Lý Đơn Hàng
                        </button>
                        <button
                            onClick={() => setActiveTab('payments')}
                            className={`py-2 px-1 border-b-2 font-medium text-sm ${activeTab === 'payments'
                                ? 'border-[#088178] text-[#088178]'
                                : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300'
                                }`}
                        >
                            <svg className="w-5 h-5 inline mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M3 10h18M7 15h1m4 0h1m-7 4h12a3 3 0 003-3V8a3 3 0 00-3-3H6a3 3 0 00-3 3v8a3 3 0 003 3z" />
                            </svg>
                            Quản Lý Thanh Toán
                        </button>
                    </nav>
                </div>
            </div>

            {/* Statistics Cards */}
            {activeTab === 'orders' && orderStatistics && (
                <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-6">
                    <div className="bg-white rounded-lg shadow-md p-6">
                        <div className="flex items-center">
                            <div className="p-3 rounded-full bg-blue-100 text-blue-600 mr-4">
                                <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
                                </svg>
                            </div>
                            <div>
                                <p className="text-sm font-medium text-gray-600">Tổng đơn hàng</p>
                                <p className="text-2xl font-bold text-gray-900">{orderStatistics.totalOrders}</p>
                            </div>
                        </div>
                    </div>

                    <div className="bg-white rounded-lg shadow-md p-6">
                        <div className="flex items-center">
                            <div className="p-3 rounded-full bg-green-100 text-green-600 mr-4">
                                <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
                                </svg>
                            </div>
                            <div>
                                <p className="text-sm font-medium text-gray-600">Đã thanh toán</p>
                                <p className="text-2xl font-bold text-gray-900">{orderStatistics.paidOrders}</p>
                            </div>
                        </div>
                    </div>

                    <div className="bg-white rounded-lg shadow-md p-6">
                        <div className="flex items-center">
                            <div className="p-3 rounded-full bg-red-100 text-red-600 mr-4">
                                <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
                                </svg>
                            </div>
                            <div>
                                <p className="text-sm font-medium text-gray-600">Chưa thanh toán</p>
                                <p className="text-2xl font-bold text-gray-900">{orderStatistics.unpaidOrders}</p>
                            </div>
                        </div>
                    </div>

                    <div className="bg-white rounded-lg shadow-md p-6">
                        <div className="flex items-center">
                            <div className="p-3 rounded-full bg-purple-100 text-purple-600 mr-4">
                                <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 8c-1.657 0-3 .895-3 2s1.343 2 3 2 3 .895 3 2-1.343 2-3 2m0-8c1.11 0 2.08.402 2.599 1M12 8V7m0 1v8m0 0v1m0-1c-1.11 0-2.08-.402-2.599-1" />
                                </svg>
                            </div>
                            <div>
                                <p className="text-sm font-medium text-gray-600">Tổng doanh thu</p>
                                <p className="text-2xl font-bold text-gray-900">{formatCurrency(orderStatistics.totalRevenue)}</p>
                            </div>
                        </div>
                    </div>
                </div>
            )}

            {activeTab === 'payments' && paymentStatistics && (
                <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-6">
                    <div className="bg-white rounded-lg shadow-md p-6">
                        <div className="flex items-center">
                            <div className="p-3 rounded-full bg-blue-100 text-blue-600 mr-4">
                                <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M3 10h18M7 15h1m4 0h1m-7 4h12a3 3 0 003-3V8a3 3 0 00-3-3H6a3 3 0 00-3 3v8a3 3 0 003 3z" />
                                </svg>
                            </div>
                            <div>
                                <p className="text-sm font-medium text-gray-600">Tổng giao dịch</p>
                                <p className="text-2xl font-bold text-gray-900">{paymentStatistics.totalTransactions}</p>
                            </div>
                        </div>
                    </div>

                    <div className="bg-white rounded-lg shadow-md p-6">
                        <div className="flex items-center">
                            <div className="p-3 rounded-full bg-green-100 text-green-600 mr-4">
                                <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
                                </svg>
                            </div>
                            <div>
                                <p className="text-sm font-medium text-gray-600">Thành công</p>
                                <p className="text-2xl font-bold text-gray-900">{paymentStatistics.successfulTransactions}</p>
                            </div>
                        </div>
                    </div>

                    <div className="bg-white rounded-lg shadow-md p-6">
                        <div className="flex items-center">
                            <div className="p-3 rounded-full bg-red-100 text-red-600 mr-4">
                                <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
                                </svg>
                            </div>
                            <div>
                                <p className="text-sm font-medium text-gray-600">Thất bại</p>
                                <p className="text-2xl font-bold text-gray-900">{paymentStatistics.failedTransactions}</p>
                            </div>
                        </div>
                    </div>

                    <div className="bg-white rounded-lg shadow-md p-6">
                        <div className="flex items-center">
                            <div className="p-3 rounded-full bg-purple-100 text-purple-600 mr-4">
                                <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 8c-1.657 0-3 .895-3 2s1.343 2 3 2 3 .895 3 2-1.343 2-3 2m0-8c1.11 0 2.08.402 2.599 1M12 8V7m0 1v8m0 0v1m0-1c-1.11 0-2.08-.402-2.599-1" />
                                </svg>
                            </div>
                            <div>
                                <p className="text-sm font-medium text-gray-600">Tổng doanh thu</p>
                                <p className="text-2xl font-bold text-gray-900">{formatCurrency(paymentStatistics.totalRevenue)}</p>
                            </div>
                        </div>
                    </div>
                </div>
            )}

            {/* Orders Table */}
            {activeTab === 'orders' && (
                <div className="bg-white rounded-lg shadow-md overflow-hidden">
                    <div className="overflow-x-auto">
                        <table className="min-w-full divide-y divide-gray-200">
                            <thead className="bg-gray-50">
                                <tr>
                                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                                        Mã đơn hàng
                                    </th>
                                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                                        Khách hàng
                                    </th>
                                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                                        Loại giao hàng
                                    </th>
                                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                                        Phí giao hàng
                                    </th>
                                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                                        Tổng tiền
                                    </th>
                                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                                        Trạng thái
                                    </th>
                                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                                        Ngày tạo
                                    </th>
                                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                                        Hành động
                                    </th>
                                </tr>
                            </thead>
                            <tbody className="bg-white divide-y divide-gray-200">
                                {orders.length > 0 ? (
                                    orders.map((order) => (
                                        <tr key={order.orderId} className="hover:bg-gray-50">
                                            <td className="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">
                                                #{order.orderId}
                                            </td>
                                            <td className="px-6 py-4 whitespace-nowrap">
                                                <div className="text-sm text-gray-900">{order.customerFullName}</div>
                                                <div className="text-sm text-gray-500">{order.customerEmail}</div>
                                            </td>
                                            <td className="px-6 py-4 whitespace-nowrap">
                                                {getShippingMethodBadge(order.shippingMethod?.methodID || 1)}
                                            </td>
                                            <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                                                {formatCurrency(order.shippingFees)}
                                            </td>
                                            <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                                                {formatCurrency(order.totalAmount)}
                                            </td>
                                            <td className="px-6 py-4 whitespace-nowrap">
                                                {getPaymentStatusBadge(order.payment)}
                                            </td>
                                            <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                                                {formatDateTime(order.createdAt)}
                                            </td>
                                            <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                                                <div className="flex space-x-2">
                                                    <button
                                                        onClick={() => fetchOrderDetails(order.orderId)}
                                                        disabled={detailsLoading}
                                                        className="bg-blue-500 text-white px-3 py-1 rounded text-xs hover:bg-blue-600 transition disabled:opacity-50"
                                                    >
                                                        {detailsLoading ? 'Đang tải...' : 'Xem chi tiết'}
                                                    </button>
                                                    {order.payment && (
                                                        <button
                                                            onClick={() => {
                                                                if (confirm('Bạn có chắc chắn muốn hủy thanh toán cho đơn hàng này?')) {
                                                                    // TODO: Implement cancel payment functionality
                                                                    alert('Chức năng hủy thanh toán sẽ được triển khai sau');
                                                                }
                                                            }}
                                                            className="bg-red-500 text-white px-3 py-1 rounded text-xs hover:bg-red-600 transition"
                                                        >
                                                            Hủy thanh toán
                                                        </button>
                                                    )}
                                                </div>
                                            </td>
                                        </tr>
                                    ))
                                ) : (
                                    <tr>
                                        <td colSpan={8} className="px-6 py-4 text-center text-gray-500">
                                            {loading ? 'Đang tải...' : 'Không có đơn hàng nào'}
                                        </td>
                                    </tr>
                                )}
                            </tbody>
                        </table>
                    </div>
                </div>
            )}

            {/* Payments Table */}
            {activeTab === 'payments' && (
                <div className="bg-white rounded-lg shadow-md overflow-hidden">
                    {/* Search and Filter */}
                    <div className="p-4 border-b border-gray-200">
                        <div className="flex flex-col sm:flex-row gap-4">
                            <div className="flex-1">
                                <input
                                    type="text"
                                    placeholder="Tìm kiếm theo mã giao dịch, khách hàng..."
                                    value={searchTerm}
                                    onChange={(e) => setSearchTerm(e.target.value)}
                                    className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-[#088178]"
                                />
                            </div>
                            <div>
                                <select
                                    value={statusFilter}
                                    onChange={(e) => setStatusFilter(e.target.value)}
                                    className="px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-[#088178]"
                                >
                                    <option value="">Tất cả trạng thái</option>
                                    <option value="SUCCESS">Thành công</option>
                                    <option value="PENDING">Đang xử lý</option>
                                    <option value="FAILED">Thất bại</option>
                                    <option value="REFUNDED">Đã hoàn tiền</option>
                                </select>
                            </div>
                        </div>
                    </div>

                    <div className="overflow-x-auto">
                        <table className="min-w-full divide-y divide-gray-200">
                            <thead className="bg-gray-50">
                                <tr>
                                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                                        Mã giao dịch
                                    </th>
                                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                                        Đơn hàng
                                    </th>
                                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                                        Số tiền
                                    </th>
                                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                                        Phương thức
                                    </th>
                                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                                        Trạng thái
                                    </th>
                                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                                        Thời gian
                                    </th>
                                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                                        Hành động
                                    </th>
                                </tr>
                            </thead>
                            <tbody className="bg-white divide-y divide-gray-200">
                                {payments.length > 0 ? (
                                    payments.map((payment) => (
                                        <tr key={payment.transactionId} className="hover:bg-gray-50">
                                            <td className="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">
                                                #{payment.transactionId}
                                            </td>
                                            <td className="px-6 py-4 whitespace-nowrap">
                                                <div className="text-sm text-gray-900">
                                                    #{payment.order?.orderId || 'N/A'}
                                                </div>
                                                <div className="text-sm text-gray-500">
                                                    {payment.order?.customerFullName || 'Không rõ'}
                                                </div>
                                            </td>
                                            <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                                                {formatCurrency(payment.totalFee)}
                                            </td>
                                            <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                                                {payment.paymentMethod}
                                            </td>
                                            <td className="px-6 py-4 whitespace-nowrap">
                                                {getTransactionStatusBadge(payment.status)}
                                            </td>
                                            <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                                                {formatDateTime(payment.transactionTime)}
                                            </td>
                                            <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                                                {payment.status === 'SUCCESS' && (
                                                    <button
                                                        onClick={() => {
                                                            setSelectedTransaction(payment);
                                                            setShowRefundModal(true);
                                                        }}
                                                        className="bg-red-500 text-white px-3 py-1 rounded text-xs hover:bg-red-600 transition"
                                                    >
                                                        Hoàn tiền
                                                    </button>
                                                )}
                                            </td>
                                        </tr>
                                    ))
                                ) : (
                                    <tr>
                                        <td colSpan={7} className="px-6 py-4 text-center text-gray-500">
                                            {loading ? 'Đang tải...' : 'Không có giao dịch nào'}
                                        </td>
                                    </tr>
                                )}
                            </tbody>
                        </table>
                    </div>
                </div>
            )}

            {/* Order Details Modal */}
            {showOrderDetails && selectedOrderDetails && (
                <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 overflow-y-auto">
                    <div className="bg-white rounded-lg p-6 w-full max-w-4xl max-h-screen overflow-y-auto m-4">
                        <div className="flex justify-between items-center mb-6">
                            <h3 className="text-2xl font-bold text-gray-800">
                                Chi tiết đơn hàng #{selectedOrderDetails.order.orderId}
                            </h3>
                            <button
                                onClick={closeOrderDetails}
                                className="text-gray-500 hover:text-gray-700"
                            >
                                <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
                                </svg>
                            </button>
                        </div>

                        {/* Order Information */}
                        <div className="grid grid-cols-1 md:grid-cols-2 gap-6 mb-6">
                            <div className="bg-gray-50 p-4 rounded-lg">
                                <h4 className="font-bold text-gray-800 mb-3">Thông tin đơn hàng</h4>
                                <div className="space-y-2 text-sm">
                                    <p><strong>Mã đơn hàng:</strong> #{selectedOrderDetails.order.orderId}</p>
                                    <p><strong>Khách hàng:</strong> {selectedOrderDetails.order.customerFullName}</p>
                                    <p><strong>Email:</strong> {selectedOrderDetails.order.customerEmail}</p>
                                    <p><strong>Điện thoại:</strong> {selectedOrderDetails.order.customerPhone}</p>
                                    <p><strong>Địa chỉ giao hàng:</strong> {selectedOrderDetails.order.deliveryAddress}</p>
                                    <p><strong>Tỉnh/Thành phố:</strong> {selectedOrderDetails.order.deliveryProvince}</p>
                                    <p><strong>Ngày tạo:</strong> {formatDateTime(selectedOrderDetails.order.createdAt)}</p>
                                </div>
                            </div>

                            <div className="bg-gray-50 p-4 rounded-lg">
                                <h4 className="font-bold text-gray-800 mb-3">Thông tin thanh toán</h4>
                                <div className="space-y-2 text-sm">
                                    <p><strong>Trạng thái:</strong> {getPaymentStatusBadge(selectedOrderDetails.order.payment)}</p>
                                    <p><strong>Tổng tiền hàng:</strong> {formatCurrency(selectedOrderDetails.order.subtotalAmount)}</p>
                                    <p><strong>Phí giao hàng:</strong> {formatCurrency(selectedOrderDetails.order.shippingFees)}</p>
                                    <p><strong>Tổng cộng:</strong> <span className="font-bold text-lg">{formatCurrency(selectedOrderDetails.order.totalAmount)}</span></p>
                                    {selectedOrderDetails.transaction && (
                                        <>
                                            <p><strong>Mã giao dịch:</strong> #{selectedOrderDetails.transaction.transactionId}</p>
                                            <p><strong>Phương thức:</strong> {selectedOrderDetails.transaction.paymentMethod}</p>
                                            <p><strong>Thời gian thanh toán:</strong> {formatDateTime(selectedOrderDetails.transaction.transactionTime)}</p>
                                        </>
                                    )}
                                </div>
                            </div>
                        </div>

                        {/* Order Items */}
                        <div className="mb-6">
                            <h4 className="font-bold text-gray-800 mb-3">Danh sách sản phẩm</h4>
                            <div className="overflow-x-auto">
                                <table className="min-w-full divide-y divide-gray-200">
                                    <thead className="bg-gray-50">
                                        <tr>
                                            <th className="px-4 py-2 text-left text-xs font-medium text-gray-500 uppercase">Sản phẩm</th>
                                            <th className="px-4 py-2 text-left text-xs font-medium text-gray-500 uppercase">Giá</th>
                                            <th className="px-4 py-2 text-left text-xs font-medium text-gray-500 uppercase">Số lượng</th>
                                            <th className="px-4 py-2 text-left text-xs font-medium text-gray-500 uppercase">Thành tiền</th>
                                        </tr>
                                    </thead>
                                    <tbody className="bg-white divide-y divide-gray-200">
                                        {selectedOrderDetails.orderItems.map((item, index) => (
                                            <tr key={index}>
                                                <td className="px-4 py-2">
                                                    <div className="flex items-center">
                                                        {item.product && (
                                                            <>
                                                                <img
                                                                    src={item.product.imageURL}
                                                                    alt={item.product.title}
                                                                    className="w-12 h-12 object-cover rounded mr-3"
                                                                    onError={(e) => {
                                                                        const target = e.target as HTMLImageElement;
                                                                        target.src = '/default-product.svg';
                                                                    }}
                                                                />
                                                                <div>
                                                                    <p className="text-sm font-medium text-gray-900">{item.product.title}</p>
                                                                    <p className="text-sm text-gray-500">ID: {item.productId}</p>
                                                                </div>
                                                            </>
                                                        )}
                                                        {!item.product && (
                                                            <div>
                                                                <p className="text-sm font-medium text-gray-900">Sản phẩm #{item.productId}</p>
                                                                <p className="text-sm text-gray-500">Không tìm thấy thông tin</p>
                                                            </div>
                                                        )}
                                                    </div>
                                                </td>
                                                <td className="px-4 py-2 text-sm text-gray-900">
                                                    {formatCurrency(item.price)}
                                                </td>
                                                <td className="px-4 py-2 text-sm text-gray-900">
                                                    {item.quantity}
                                                </td>
                                                <td className="px-4 py-2 text-sm font-medium text-gray-900">
                                                    {formatCurrency(item.price * item.quantity)}
                                                </td>
                                            </tr>
                                        ))}
                                    </tbody>
                                </table>
                            </div>
                        </div>

                        {/* Close Button */}
                        <div className="flex justify-end">
                            <button
                                onClick={closeOrderDetails}
                                className="px-4 py-2 bg-gray-500 text-white rounded hover:bg-gray-600 transition"
                            >
                                Đóng
                            </button>
                        </div>
                    </div>
                </div>
            )}

            {/* Refund Modal */}
            {showRefundModal && selectedTransaction && (
                <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
                    <div className="bg-white rounded-lg p-6 w-full max-w-md m-4">
                        <div className="flex justify-between items-center mb-4">
                            <h3 className="text-xl font-bold text-gray-800">Hoàn tiền giao dịch</h3>
                            <button
                                onClick={() => {
                                    setShowRefundModal(false);
                                    setRefundReason('');
                                    setSelectedTransaction(null);
                                }}
                                className="text-gray-500 hover:text-gray-700"
                            >
                                <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
                                </svg>
                            </button>
                        </div>

                        <div className="mb-4">
                            <p className="text-sm text-gray-600 mb-2">
                                <strong>Mã giao dịch:</strong> #{selectedTransaction.transactionId}
                            </p>
                            <p className="text-sm text-gray-600 mb-2">
                                <strong>Số tiền:</strong> {formatCurrency(selectedTransaction.totalFee)}
                            </p>
                            <p className="text-sm text-gray-600 mb-4">
                                <strong>Khách hàng:</strong> {selectedTransaction.order?.customerFullName || 'Không rõ'}
                            </p>

                            <div>
                                <label className="block text-sm font-medium text-gray-700 mb-2">
                                    Lý do hoàn tiền
                                </label>
                                <textarea
                                    value={refundReason}
                                    onChange={(e) => setRefundReason(e.target.value)}
                                    className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-[#088178]"
                                    rows={3}
                                    placeholder="Nhập lý do hoàn tiền..."
                                />
                            </div>
                        </div>

                        <div className="flex justify-end space-x-3">
                            <button
                                onClick={() => {
                                    setShowRefundModal(false);
                                    setRefundReason('');
                                    setSelectedTransaction(null);
                                }}
                                className="px-4 py-2 bg-gray-500 text-white rounded hover:bg-gray-600 transition"
                            >
                                Hủy
                            </button>
                            <button
                                onClick={handleRefund}
                                disabled={refundLoading}
                                className="px-4 py-2 bg-red-500 text-white rounded hover:bg-red-600 transition disabled:opacity-50"
                            >
                                {refundLoading ? 'Đang xử lý...' : 'Xác nhận hoàn tiền'}
                            </button>
                        </div>
                    </div>
                </div>
            )}
        </div>
    );
};

export default AdminOrderPaymentManagement;
