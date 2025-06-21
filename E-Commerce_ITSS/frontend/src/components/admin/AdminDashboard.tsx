import React, { useState, useEffect } from 'react';
import { userService } from '../../services/user.service';
import { productService } from '../../services/product.service';
import type { User, Product } from '../../types';

const AdminDashboard: React.FC = () => {
    const [stats, setStats] = useState({
        totalUsers: 0,
        totalProducts: 0,
        totalCategories: 0,
        totalOrders: 0,
        loading: true
    });

    const [recentUsers, setRecentUsers] = useState<User[]>([]);
    const [recentProducts, setRecentProducts] = useState<Product[]>([]);

    useEffect(() => {
        fetchDashboardData();
    }, []); const fetchDashboardData = async () => {
        try {
            setStats(prev => ({ ...prev, loading: true }));

            const [usersResponse, productsResponse, categoriesResponse] = await Promise.all([
                userService.getAllUsers(),
                productService.getAllProducts(),
                productService.getAllCategories()
            ]);

            console.log('Dashboard API Responses:', {
                users: usersResponse,
                products: productsResponse,
                categories: categoriesResponse
            });

            // Handle different response structures
            let totalProducts = 0;
            let recentProductsData: Product[] = [];

            if (productsResponse.success) {
                if (Array.isArray(productsResponse.data)) {
                    // Direct array response
                    totalProducts = productsResponse.data.length;
                    recentProductsData = productsResponse.data.slice(0, 5);
                } else if (productsResponse.data.totalElements !== undefined) {
                    // PageResponse structure
                    totalProducts = productsResponse.data.totalElements;
                    recentProductsData = productsResponse.data.content ? productsResponse.data.content.slice(0, 5) : [];
                }
            }

            setStats({
                totalUsers: usersResponse.success ? usersResponse.data.length : 0,
                totalProducts: totalProducts,
                totalCategories: categoriesResponse.success ? categoriesResponse.data.length : 0,
                totalOrders: 0, // TODO: Implement order service
                loading: false
            });

            if (usersResponse.success) {
                setRecentUsers(usersResponse.data.slice(0, 5));
            }

            setRecentProducts(recentProductsData);

        } catch (error) {
            console.error('Error fetching dashboard data:', error);
            setStats(prev => ({ ...prev, loading: false }));
        }
    };

    if (stats.loading) {
        return (
            <div className="flex justify-center items-center h-64">
                <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-[#088178]"></div>
            </div>
        );
    }

    return (
        <div className="p-6">
            <h1 className="text-3xl font-bold text-gray-800 mb-8">Dashboard Thống Kê</h1>

            {/* Stats Cards */}
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-8">
                <div className="bg-white rounded-lg shadow-md p-6">
                    <div className="flex items-center">
                        <div className="p-3 rounded-full bg-blue-100 text-blue-600 mr-4">
                            <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 4.354a4 4 0 110 5.292M15 21H3v-1a6 6 0 0112 0v1zm0 0h6v-1a6 6 0 00-9-5.197m13.5-9a2.5 2.5 0 11-5 0 2.5 2.5 0 015 0z" />
                            </svg>
                        </div>
                        <div>
                            <p className="text-sm font-medium text-gray-600">Tổng người dùng</p>
                            <p className="text-2xl font-bold text-gray-900">{stats.totalUsers}</p>
                        </div>
                    </div>
                </div>

                <div className="bg-white rounded-lg shadow-md p-6">
                    <div className="flex items-center">
                        <div className="p-3 rounded-full bg-green-100 text-green-600 mr-4">
                            <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M20 7l-8-4-8 4m16 0l-8 4m8-4v10l-8 4m0-10L4 7m8 4v10M4 7v10l8 4" />
                            </svg>
                        </div>
                        <div>
                            <p className="text-sm font-medium text-gray-600">Tổng sản phẩm</p>
                            <p className="text-2xl font-bold text-gray-900">{stats.totalProducts}</p>
                        </div>
                    </div>
                </div>

                <div className="bg-white rounded-lg shadow-md p-6">
                    <div className="flex items-center">
                        <div className="p-3 rounded-full bg-yellow-100 text-yellow-600 mr-4">
                            <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 11H5m14 0a2 2 0 012 2v6a2 2 0 01-2 2H5a2 2 0 01-2-2v-6a2 2 0 012-2m14 0V9a2 2 0 00-2-2M5 11V9a2 2 0 012-2m0 0V5a2 2 0 012-2h6a2 2 0 012 2v2M7 7h10" />
                            </svg>
                        </div>
                        <div>
                            <p className="text-sm font-medium text-gray-600">Loại sản phẩm</p>
                            <p className="text-2xl font-bold text-gray-900">{stats.totalCategories}</p>
                        </div>
                    </div>
                </div>

                <div className="bg-white rounded-lg shadow-md p-6">
                    <div className="flex items-center">
                        <div className="p-3 rounded-full bg-purple-100 text-purple-600 mr-4">
                            <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
                            </svg>
                        </div>
                        <div>
                            <p className="text-sm font-medium text-gray-600">Tổng đơn hàng</p>
                            <p className="text-2xl font-bold text-gray-900">{stats.totalOrders}</p>
                        </div>
                    </div>
                </div>
            </div>

            {/* Recent Activity */}
            <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
                {/* Recent Users */}
                <div className="bg-white rounded-lg shadow-md p-6">
                    <h2 className="text-xl font-bold text-gray-800 mb-4">Người dùng mới</h2>
                    <div className="space-y-3">
                        {recentUsers.length > 0 ? (
                            recentUsers.map((user) => (
                                <div key={user.userId} className="flex items-center justify-between p-3 bg-gray-50 rounded-lg">                                    <div>
                                    <p className="font-medium text-gray-900">{user.username}</p>
                                    <p className="text-sm text-gray-600">{user.email}</p>
                                </div>
                                    <span className={`px-2 py-1 text-xs font-medium rounded-full ${user.role === 'admin'
                                        ? 'bg-red-100 text-red-800'
                                        : 'bg-blue-100 text-blue-800'
                                        }`}>
                                        {user.role || 'customer'}
                                    </span>
                                </div>
                            ))
                        ) : (
                            <p className="text-gray-500 text-center py-4">Không có người dùng nào</p>
                        )}
                    </div>
                </div>

                {/* Recent Products */}
                <div className="bg-white rounded-lg shadow-md p-6">
                    <h2 className="text-xl font-bold text-gray-800 mb-4">Sản phẩm mới</h2>
                    <div className="space-y-3">
                        {recentProducts.length > 0 ? (
                            recentProducts.map((product) => (
                                <div key={product.id} className="flex items-center justify-between p-3 bg-gray-50 rounded-lg">
                                    <div className="flex items-center">
                                        <img
                                            src={product.imageURL}
                                            alt={product.title}
                                            className="w-12 h-12 object-cover rounded-lg mr-3"
                                            onError={(e) => {
                                                const target = e.target as HTMLImageElement;
                                                target.src = '/default-product.svg';
                                            }}
                                        />
                                        <div>
                                            <p className="font-medium text-gray-900">{product.title}</p>
                                            <p className="text-sm text-gray-600">{product.category}</p>
                                        </div>
                                    </div>
                                    <div className="text-right">
                                        <p className="font-medium text-gray-900">{product.price.toLocaleString('vi-VN')} VND</p>
                                        <p className="text-sm text-gray-600">Kho: {product.quantity}</p>
                                    </div>
                                </div>
                            ))
                        ) : (
                            <p className="text-gray-500 text-center py-4">Không có sản phẩm nào</p>
                        )}
                    </div>
                </div>
            </div>
        </div>
    );
};

export default AdminDashboard;
