import React, { useState } from 'react';
import { productService } from '../../services/product.service';

const AdminAddProduct: React.FC = () => {
    const [formData, setFormData] = useState({
        title: '',
        price: 0,
        category: '',
        imageURL: '',
        quantity: 0,
        dimension: 0,
        weight: 0,
        entryDate: new Date().toISOString().split('T')[0], // Default to today's date
        author: '',
        genre: '',
        language: '',
        publisher: ''
    });
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState<string | null>(null);
    const [success, setSuccess] = useState<string | null>(null);

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        setLoading(true);
        setError(null);
        setSuccess(null);

        try {
            const response = await productService.createProduct(formData); if (response.success) {
                setSuccess('Sản phẩm đã được thêm thành công!');
                // Reset form
                setFormData({
                    title: '',
                    price: 0,
                    category: '',
                    imageURL: '',
                    quantity: 0,
                    dimension: 0,
                    weight: 0,
                    entryDate: new Date().toISOString().split('T')[0],
                    author: '',
                    genre: '',
                    language: '',
                    publisher: ''
                });
            } else {
                setError(response.error || 'Lỗi khi thêm sản phẩm');
            }
        } catch (err) {
            console.error('Error creating product:', err);
            setError('Lỗi kết nối với server');
        } finally {
            setLoading(false);
        }
    }; const handleReset = () => {
        setFormData({
            title: '',
            price: 0,
            category: '',
            imageURL: '',
            quantity: 0,
            dimension: 0,
            weight: 0,
            entryDate: new Date().toISOString().split('T')[0],
            author: '',
            genre: '',
            language: '',
            publisher: ''
        });
        setError(null);
        setSuccess(null);
    };

    return (
        <div className="p-6">
            <div className="flex justify-between items-center mb-6">
                <h1 className="text-3xl font-bold text-gray-800">Thêm Sản Phẩm Mới</h1>
            </div>

            {error && (
                <div className="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded mb-4">
                    <p className="font-bold">Lỗi</p>
                    <p>{error}</p>
                </div>
            )}

            {success && (
                <div className="bg-green-100 border border-green-400 text-green-700 px-4 py-3 rounded mb-4">
                    <p className="font-bold">Thành công</p>
                    <p>{success}</p>
                </div>
            )}

            <div className="bg-white rounded-lg shadow-md p-6">
                <form onSubmit={handleSubmit} className="space-y-4">
                    <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                        <div>
                            <label className="block text-sm font-medium text-gray-700 mb-1">
                                Tên sản phẩm *
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
                                Giá (VND) *
                            </label>
                            <input
                                type="number"
                                step="1000"
                                value={formData.price}
                                onChange={(e) => setFormData({ ...formData, price: parseFloat(e.target.value) })}
                                className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-[#088178] focus:border-transparent"
                                required
                                min="0"
                            />
                        </div>

                        <div>
                            <label className="block text-sm font-medium text-gray-700 mb-1">
                                Danh mục *
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
                                Số lượng *
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
                                URL hình ảnh
                            </label>
                            <input
                                type="url"
                                value={formData.imageURL}
                                onChange={(e) => setFormData({ ...formData, imageURL: e.target.value })}
                                className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-[#088178] focus:border-transparent"
                                placeholder="https://example.com/image.jpg"
                            />
                        </div>

                        <div>
                            <label className="block text-sm font-medium text-gray-700 mb-1">
                                Tác giả
                            </label>
                            <input
                                type="text"
                                value={formData.author}
                                onChange={(e) => setFormData({ ...formData, author: e.target.value })}
                                className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-[#088178] focus:border-transparent"
                            />
                        </div>

                        <div>
                            <label className="block text-sm font-medium text-gray-700 mb-1">
                                Thể loại
                            </label>
                            <input
                                type="text"
                                value={formData.genre}
                                onChange={(e) => setFormData({ ...formData, genre: e.target.value })}
                                className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-[#088178] focus:border-transparent"
                            />
                        </div>

                        <div>
                            <label className="block text-sm font-medium text-gray-700 mb-1">
                                Ngôn ngữ
                            </label>
                            <input
                                type="text"
                                value={formData.language}
                                onChange={(e) => setFormData({ ...formData, language: e.target.value })}
                                className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-[#088178] focus:border-transparent"
                            />
                        </div>

                        <div>
                            <label className="block text-sm font-medium text-gray-700 mb-1">
                                Nhà xuất bản
                            </label>
                            <input
                                type="text"
                                value={formData.publisher}
                                onChange={(e) => setFormData({ ...formData, publisher: e.target.value })}
                                className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-[#088178] focus:border-transparent"
                            />
                        </div>

                        <div>
                            <label className="block text-sm font-medium text-gray-700 mb-1">
                                Kích thước (cm)
                            </label>
                            <input
                                type="number"
                                step="0.1"
                                value={formData.dimension}
                                onChange={(e) => setFormData({ ...formData, dimension: parseFloat(e.target.value) })}
                                className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-[#088178] focus:border-transparent"
                                min="0"
                            />
                        </div>

                        <div>
                            <label className="block text-sm font-medium text-gray-700 mb-1">
                                Cân nặng (kg)
                            </label>
                            <input
                                type="number"
                                step="0.1"
                                value={formData.weight}
                                onChange={(e) => setFormData({ ...formData, weight: parseFloat(e.target.value) })}
                                className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-[#088178] focus:border-transparent"
                                min="0"
                            />
                        </div>
                    </div>

                    <div className="flex justify-end space-x-4 pt-4">
                        <button
                            type="button"
                            onClick={handleReset}
                            className="px-6 py-2 bg-gray-500 text-white rounded-lg hover:bg-gray-600 transition"
                            disabled={loading}
                        >
                            Đặt lại
                        </button>
                        <button
                            type="submit"
                            disabled={loading}
                            className="px-6 py-2 bg-[#088178] text-white rounded-lg hover:bg-[#066e6a] transition disabled:opacity-50"
                        >
                            {loading ? 'Đang thêm...' : 'Thêm sản phẩm'}
                        </button>
                    </div>
                </form>
            </div>
        </div>
    );
};

export default AdminAddProduct;
