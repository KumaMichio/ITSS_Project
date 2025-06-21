import React, { useState, useEffect } from 'react';
import { productService } from '../../services/product.service';
import type { Product, PageResponse } from '../../types';

interface EditProductModalProps {
    product: Product;
    onClose: () => void;
    onUpdate: (product: Product) => void;
}

const EditProductModal: React.FC<EditProductModalProps> = ({ product, onClose, onUpdate }) => {
    const [formData, setFormData] = useState({
        title: product.title,
        price: product.price,
        category: product.category,
        imageURL: product.imageURL,
        quantity: product.quantity,
        dimension: product.dimension,
        weight: product.weight
    });
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState<string | null>(null);

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        setLoading(true);
        setError(null);

        try {
            const response = await productService.updateProduct(product.id, formData);
            if (response.success) {
                onUpdate(response.data);
                onClose();
            } else {
                setError(response.error || 'Lỗi khi cập nhật sản phẩm');
            }
        } catch (err) {
            console.error('Error updating product:', err);
            setError('Lỗi kết nối với server');
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
            <div className="bg-white rounded-lg p-6 w-full max-w-2xl mx-4 max-h-[90vh] overflow-y-auto">
                <div className="flex justify-between items-center mb-4">
                    <h2 className="text-xl font-bold text-gray-800">Sửa sản phẩm</h2>
                    <button
                        onClick={onClose}
                        className="text-gray-400 hover:text-gray-600"
                    >
                        <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
                        </svg>
                    </button>
                </div>

                {error && (
                    <div className="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded mb-4">
                        {error}
                    </div>
                )}

                <form onSubmit={handleSubmit} className="space-y-4">
                    <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                        <div>
                            <label className="block text-sm font-medium text-gray-700 mb-1">
                                Tên sản phẩm
                            </label>
                            <input
                                type="text"
                                value={formData.title}
                                onChange={(e) => setFormData({ ...formData, title: e.target.value })}
                                className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-[#088178] focus:border-transparent"
                                required
                            />
                        </div>

                        <div>
                            <label className="block text-sm font-medium text-gray-700 mb-1">
                                Giá (VND)
                            </label>
                            <input
                                type="number"
                                step="0.01"
                                value={formData.price}
                                onChange={(e) => setFormData({ ...formData, price: parseFloat(e.target.value) })}
                                className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-[#088178] focus:border-transparent"
                                required
                                min="0"
                            />
                        </div>

                        <div>
                            <label className="block text-sm font-medium text-gray-700 mb-1">
                                Danh mục
                            </label>
                            <select
                                value={formData.category}
                                onChange={(e) => setFormData({ ...formData, category: e.target.value })}
                                className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-[#088178] focus:border-transparent"
                                required
                            >
                                <option value="">Chọn danh mục</option>
                                <option value="Book">Book</option>
                                <option value="DVD">DVD</option>
                                <option value="CD">CD</option>
                                <option value="CDLP">CDLP</option>
                            </select>
                        </div>

                        <div>
                            <label className="block text-sm font-medium text-gray-700 mb-1">
                                Số lượng
                            </label>
                            <input
                                type="number"
                                value={formData.quantity}
                                onChange={(e) => setFormData({ ...formData, quantity: parseInt(e.target.value) })}
                                className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-[#088178] focus:border-transparent"
                                required
                                min="0"
                            />
                        </div>

                        <div>
                            <label className="block text-sm font-medium text-gray-700 mb-1">
                                Kích thước
                            </label>
                            <input
                                type="number"
                                step="0.01"
                                value={formData.dimension}
                                onChange={(e) => setFormData({ ...formData, dimension: parseFloat(e.target.value) })}
                                className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-[#088178] focus:border-transparent"
                                min="0"
                            />
                        </div>

                        <div>
                            <label className="block text-sm font-medium text-gray-700 mb-1">
                                Trọng lượng
                            </label>
                            <input
                                type="number"
                                step="0.01"
                                value={formData.weight}
                                onChange={(e) => setFormData({ ...formData, weight: parseFloat(e.target.value) })}
                                className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-[#088178] focus:border-transparent"
                                min="0"
                            />
                        </div>
                    </div>

                    <div>
                        <label className="block text-sm font-medium text-gray-700 mb-1">
                            URL hình ảnh
                        </label>
                        <input
                            type="url"
                            value={formData.imageURL}
                            onChange={(e) => setFormData({ ...formData, imageURL: e.target.value })}
                            className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-[#088178] focus:border-transparent"
                            required
                        />
                    </div>

                    <div className="flex justify-end space-x-3 mt-6">
                        <button
                            type="button"
                            onClick={onClose}
                            className="px-4 py-2 text-gray-600 bg-gray-200 rounded-lg hover:bg-gray-300 transition"
                        >
                            Hủy
                        </button>
                        <button
                            type="submit"
                            disabled={loading}
                            className="px-4 py-2 bg-[#088178] text-white rounded-lg hover:bg-[#066e6a] transition disabled:opacity-50"
                        >
                            {loading ? 'Đang lưu...' : 'Lưu'}
                        </button>
                    </div>
                </form>
            </div>
        </div>
    );
};

const AdminProductManagement: React.FC = () => {
    const [products, setProducts] = useState<Product[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);
    const [searchTerm, setSearchTerm] = useState('');
    const [filterCategory, setFilterCategory] = useState('all');
    const [editingProduct, setEditingProduct] = useState<Product | null>(null);
    const [deletingProductId, setDeletingProductId] = useState<number | null>(null);
    const [currentPage, setCurrentPage] = useState(0);
    const [totalPages, setTotalPages] = useState(0);

    useEffect(() => {
        fetchProducts();
    }, [currentPage]); const fetchProducts = async () => {
        try {
            setLoading(true);
            setError(null);
            const response = await productService.getAllProducts({
                page: currentPage,
                size: 20,
                sort: 'id,desc'
            });

            console.log('API Response:', response);

            if (response.success) {
                // Check if response.data is an array (direct products) or PageResponse structure
                if (Array.isArray(response.data)) {
                    // Direct array response
                    console.log('Direct array response:', response.data);
                    setProducts(response.data);
                    setTotalPages(1); // Assume single page for direct array
                } else {
                    // PageResponse structure
                    const pageResponse = response.data as PageResponse<Product>;
                    console.log('PageResponse structure:', pageResponse);
                    setProducts(pageResponse.content);
                    setTotalPages(pageResponse.totalPages);
                }
            } else {
                setError(response.error || 'Lỗi khi tải sản phẩm');
            }
        } catch (err) {
            console.error('Error fetching products:', err);
            setError('Lỗi kết nối với server');
        } finally {
            setLoading(false);
        }
    };

    const handleDeleteProduct = async (productId: number) => {
        if (!confirm('Bạn có chắc chắn muốn xóa sản phẩm này?')) {
            return;
        }

        setDeletingProductId(productId);
        try {
            const response = await productService.deleteProduct(productId);

            if (response.success) {
                setProducts((products || []).filter(product => product.id !== productId));
            } else {
                alert(response.error || 'Lỗi khi xóa sản phẩm');
            }
        } catch (err) {
            console.error('Error deleting product:', err);
            alert('Lỗi kết nối với server');
        } finally {
            setDeletingProductId(null);
        }
    };

    const handleUpdateProduct = (updatedProduct: Product) => {
        setProducts((products || []).map(product =>
            product.id === updatedProduct.id ? updatedProduct : product
        ));
    };

    const filteredProducts = (products || []).filter(product => {
        const matchesSearch = product.title.toLowerCase().includes(searchTerm.toLowerCase()) ||
            product.category.toLowerCase().includes(searchTerm.toLowerCase());

        const matchesCategory = filterCategory === 'all' || product.category === filterCategory;

        return matchesSearch && matchesCategory;
    });

    // Debug logging
    console.log('AdminProductManagement Debug:', {
        products: products,
        productsLength: (products || []).length,
        filteredProducts: filteredProducts,
        filteredProductsLength: filteredProducts.length,
        loading: loading,
        error: error,
        searchTerm: searchTerm,
        filterCategory: filterCategory
    });

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
                    onClick={fetchProducts}
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
                <h1 className="text-3xl font-bold text-gray-800">Quản Lý Sản Phẩm</h1>
                <button
                    onClick={fetchProducts}
                    className="bg-[#088178] text-white px-4 py-2 rounded hover:bg-[#066e6a] transition flex items-center"
                >
                    <svg className="w-4 h-4 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15" />
                    </svg>
                    Làm mới
                </button>
            </div>

            {/* Search and Filter */}
            <div className="bg-white rounded-lg shadow-md p-4 mb-6">
                <div className="flex flex-col md:flex-row gap-4">
                    <div className="flex-1">
                        <label className="block text-sm font-medium text-gray-700 mb-2">
                            Tìm kiếm sản phẩm
                        </label>
                        <input
                            type="text"
                            placeholder="Tìm theo tên, danh mục..."
                            value={searchTerm}
                            onChange={(e) => setSearchTerm(e.target.value)}
                            className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-[#088178] focus:border-transparent"
                        />
                    </div>
                    <div className="md:w-48">
                        <label className="block text-sm font-medium text-gray-700 mb-2">
                            Lọc theo danh mục
                        </label>
                        <select
                            value={filterCategory}
                            onChange={(e) => setFilterCategory(e.target.value)}
                            className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-[#088178] focus:border-transparent"
                        >
                            <option value="all">Tất cả</option>
                            <option value="Book">Book</option>
                            <option value="DVD">DVD</option>
                            <option value="CD">CD</option>
                            <option value="CDLP">CDLP</option>
                        </select>
                    </div>
                </div>
            </div>            {/* Products Table */}
            <div className="bg-white rounded-lg shadow-md overflow-hidden">
                <div className="px-6 py-4 bg-gray-50 border-b">
                    <h2 className="text-lg font-semibold text-gray-800">
                        Tổng cộng: {filteredProducts.length} sản phẩm
                        {searchTerm && ` (Lọc từ ${(products || []).length} sản phẩm)`}
                    </h2>
                </div>

                {filteredProducts.length === 0 ? (
                    <div className="text-center py-12">
                        <div className="text-gray-400 mb-4">
                            <svg className="w-16 h-16 mx-auto" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M20 7l-8-4-8 4m16 0l-8 4m8-4v10l-8 4m0-10L4 7m8 4v10M4 7v10l8 4" />
                            </svg>
                        </div>
                        <p className="text-gray-500 text-lg">
                            {searchTerm ? 'Không tìm thấy sản phẩm phù hợp' : 'Không có sản phẩm nào'}
                        </p>
                    </div>
                ) : (
                    <div className="overflow-x-auto">
                        <table className="w-full">
                            <thead className="bg-gray-50">
                                <tr>
                                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                                        Sản phẩm
                                    </th>
                                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                                        Giá & Kho
                                    </th>
                                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                                        Danh mục
                                    </th>
                                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                                        Thao tác
                                    </th>
                                </tr>
                            </thead>
                            <tbody className="bg-white divide-y divide-gray-200">
                                {filteredProducts.map((product) => (
                                    <tr key={product.id} className="hover:bg-gray-50">
                                        <td className="px-6 py-4">
                                            <div className="flex items-center">
                                                <img
                                                    src={product.imageURL}
                                                    alt={product.title}
                                                    className="w-16 h-16 object-cover rounded-lg mr-4"
                                                    onError={(e) => {
                                                        const target = e.target as HTMLImageElement;
                                                        target.src = '/default-product.svg';
                                                    }}
                                                />
                                                <div>
                                                    <div className="text-sm font-medium text-gray-900">
                                                        {product.title}
                                                    </div>
                                                    <div className="text-sm text-gray-500">
                                                        ID: #{product.id}
                                                    </div>
                                                </div>
                                            </div>
                                        </td>
                                        <td className="px-6 py-4 whitespace-nowrap">
                                            <div className="text-sm text-gray-900">{product.price.toLocaleString('vi-VN')} VND</div>
                                            <div className="text-sm text-gray-500">Kho: {product.quantity}</div>
                                        </td>
                                        <td className="px-6 py-4 whitespace-nowrap">
                                            <span className="px-2 inline-flex text-xs leading-5 font-semibold rounded-full bg-blue-100 text-blue-800">
                                                {product.category}
                                            </span>
                                        </td>
                                        <td className="px-6 py-4 whitespace-nowrap text-sm font-medium">
                                            <div className="flex space-x-2">
                                                <button
                                                    onClick={() => setEditingProduct(product)}
                                                    className="text-indigo-600 hover:text-indigo-900 px-3 py-1 border border-indigo-300 rounded-lg hover:bg-indigo-50 transition"
                                                >
                                                    Sửa
                                                </button>
                                                <button
                                                    onClick={() => handleDeleteProduct(product.id)}
                                                    disabled={deletingProductId === product.id}
                                                    className="text-red-600 hover:text-red-900 px-3 py-1 border border-red-300 rounded-lg hover:bg-red-50 transition disabled:opacity-50"
                                                >
                                                    {deletingProductId === product.id ? 'Đang xóa...' : 'Xóa'}
                                                </button>
                                            </div>
                                        </td>
                                    </tr>
                                ))}
                            </tbody>
                        </table>
                    </div>
                )}

                {/* Pagination */}
                {totalPages > 1 && (
                    <div className="px-6 py-4 bg-gray-50 border-t">
                        <div className="flex justify-between items-center">
                            <div className="text-sm text-gray-700">
                                Trang {currentPage + 1} / {totalPages}
                            </div>
                            <div className="flex space-x-2">
                                <button
                                    onClick={() => setCurrentPage(Math.max(0, currentPage - 1))}
                                    disabled={currentPage === 0}
                                    className="px-3 py-1 border border-gray-300 rounded text-sm disabled:opacity-50 disabled:cursor-not-allowed hover:bg-gray-100"
                                >
                                    Trước
                                </button>
                                <button
                                    onClick={() => setCurrentPage(Math.min(totalPages - 1, currentPage + 1))}
                                    disabled={currentPage >= totalPages - 1}
                                    className="px-3 py-1 border border-gray-300 rounded text-sm disabled:opacity-50 disabled:cursor-not-allowed hover:bg-gray-100"
                                >
                                    Sau
                                </button>
                            </div>
                        </div>
                    </div>
                )}
            </div>

            {/* Edit Product Modal */}
            {editingProduct && (
                <EditProductModal
                    product={editingProduct}
                    onClose={() => setEditingProduct(null)}
                    onUpdate={handleUpdateProduct}
                />
            )}
        </div>
    );
};

export default AdminProductManagement;
