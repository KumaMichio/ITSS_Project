import React, { useState, useEffect } from 'react';
import { productService } from '../../services/product.service';

const AdminCategories: React.FC = () => {
    const [categories, setCategories] = useState<string[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);

    useEffect(() => {
        fetchCategories();
    }, []);

    const fetchCategories = async () => {
        try {
            setLoading(true);
            setError(null);
            const response = await productService.getAllCategories();

            if (response.success) {
                setCategories(response.data);
            } else {
                setError(response.error || 'Lỗi khi tải danh mục');
            }
        } catch (err) {
            console.error('Error fetching categories:', err);
            setError('Lỗi kết nối với server');
        } finally {
            setLoading(false);
        }
    };

    if (loading) {
        return (
            <div className="flex justify-center items-center h-64">
                <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-[#088178]"></div>
            </div>
        );
    }

    if (error) {
        return (
            <div className="p-6">
                <div className="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded mb-4">
                    <p className="font-bold">Lỗi</p>
                    <p>{error}</p>
                </div>
                <button
                    onClick={fetchCategories}
                    className="bg-[#088178] text-white px-4 py-2 rounded hover:bg-[#066e6a] transition"
                >
                    Thử lại
                </button>
            </div>
        );
    }

    return (
        <div className="p-6">
            <div className="flex justify-between items-center mb-6">
                <h1 className="text-3xl font-bold text-gray-800">Danh Sách Loại Sản Phẩm</h1>
                <button
                    onClick={fetchCategories}
                    className="bg-[#088178] text-white px-4 py-2 rounded hover:bg-[#066e6a] transition flex items-center"
                >
                    <svg className="w-4 h-4 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15" />
                    </svg>
                    Làm mới
                </button>
            </div>

            <div className="bg-white rounded-lg shadow-md p-6">
                <div className="mb-4">
                    <h2 className="text-xl font-semibold text-gray-800 mb-2">
                        Tổng cộng: {categories.length} loại sản phẩm
                    </h2>
                    <p className="text-gray-600">
                        Hiển thị tất cả các loại sản phẩm có sẵn trong hệ thống
                    </p>
                </div>

                {categories.length === 0 ? (
                    <div className="text-center py-8">
                        <div className="text-gray-400 mb-4">
                            <svg className="w-16 h-16 mx-auto" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 11H5m14 0a2 2 0 012 2v6a2 2 0 01-2 2H5a2 2 0 01-2-2v-6a2 2 0 012-2m14 0V9a2 2 0 00-2-2M5 11V9a2 2 0 012-2m0 0V5a2 2 0 012-2h6a2 2 0 012 2v2M7 7h10" />
                            </svg>
                        </div>
                        <p className="text-gray-500 text-lg">Không có loại sản phẩm nào</p>
                    </div>
                ) : (
                    <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-4">
                        {categories.map((category, index) => (
                            <div
                                key={index}
                                className="bg-gradient-to-br from-[#088178] to-[#066e6a] text-white rounded-lg p-6 hover:scale-105 transition-transform duration-200 shadow-lg"
                            >
                                <div className="flex items-center justify-between">
                                    <div>
                                        <h3 className="text-lg font-bold mb-1">
                                            {category.toUpperCase()}
                                        </h3>
                                        <p className="text-sm opacity-90">
                                            Loại sản phẩm
                                        </p>
                                    </div>
                                    <div className="text-2xl opacity-70">
                                        {category.toLowerCase() === 'book' ? '📚' :
                                            category.toLowerCase() === 'dvd' ? '💿' :
                                                category.toLowerCase() === 'cd' ? '🎵' :
                                                    category.toLowerCase() === 'cdlp' ? '🎵' :
                                                        category.toLowerCase() === 'lp' ? '🎵' :
                                                            '📦'}
                                    </div>
                                </div>
                            </div>
                        ))}
                    </div>
                )}
            </div>

            <div className="mt-6 bg-blue-50 border border-blue-200 rounded-lg p-4">
                <h3 className="text-lg font-semibold text-blue-800 mb-2">Thông tin</h3>
                <div className="text-blue-700 text-sm space-y-1">
                    <p>• Danh mục sản phẩm được tự động tạo dựa trên các sản phẩm có trong hệ thống</p>
                    <p>• Mỗi loại sản phẩm có thể chứa nhiều sản phẩm con</p>
                    <p>• Để thêm loại sản phẩm mới, hãy thêm sản phẩm thuộc loại đó</p>
                </div>
            </div>
        </div>
    );
};

export default AdminCategories;
