import React, { useState } from 'react';
import { userService } from '../../services/user.service';

const AdminAddUser: React.FC = () => {
    const [formData, setFormData] = useState({
        username: '',
        email: '',
        password: '',
        role: 'customer'
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
            const response = await userService.createUser(formData);
            if (response.success) {
                setSuccess('Người dùng đã được thêm thành công!');
                // Reset form
                setFormData({
                    username: '',
                    email: '',
                    password: '',
                    role: 'customer'
                });
            } else {
                setError(response.error || 'Lỗi khi thêm người dùng');
            }
        } catch (err) {
            console.error('Error creating user:', err);
            setError('Lỗi kết nối với server');
        } finally {
            setLoading(false);
        }
    };

    const handleReset = () => {
        setFormData({
            username: '',
            email: '',
            password: '',
            role: 'customer'
        });
        setError(null);
        setSuccess(null);
    };

    return (
        <div className="p-6">
            <div className="flex justify-between items-center mb-6">
                <h1 className="text-3xl font-bold text-gray-800">Thêm Người Dùng Mới</h1>
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

            <div className="bg-white rounded-lg shadow-md p-6 max-w-2xl">
                <form onSubmit={handleSubmit} className="space-y-4">
                    <div>
                        <label className="block text-sm font-medium text-gray-700 mb-1">
                            Tên đăng nhập *
                        </label>
                        <input
                            type="text"
                            value={formData.username}
                            onChange={(e) => setFormData({ ...formData, username: e.target.value })}
                            className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-[#088178] focus:border-transparent"
                            required
                            minLength={3}
                            placeholder="Nhập tên đăng nhập"
                        />
                    </div>

                    <div>
                        <label className="block text-sm font-medium text-gray-700 mb-1">
                            Email *
                        </label>
                        <input
                            type="email"
                            value={formData.email}
                            onChange={(e) => setFormData({ ...formData, email: e.target.value })}
                            className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-[#088178] focus:border-transparent"
                            required
                            placeholder="Nhập địa chỉ email"
                        />
                    </div>

                    <div>
                        <label className="block text-sm font-medium text-gray-700 mb-1">
                            Mật khẩu *
                        </label>
                        <input
                            type="password"
                            value={formData.password}
                            onChange={(e) => setFormData({ ...formData, password: e.target.value })}
                            className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-[#088178] focus:border-transparent"
                            required
                            minLength={6}
                            placeholder="Nhập mật khẩu (tối thiểu 6 ký tự)"
                        />
                    </div>

                    <div>
                        <label className="block text-sm font-medium text-gray-700 mb-1">
                            Vai trò *
                        </label>
                        <select
                            value={formData.role}
                            onChange={(e) => setFormData({ ...formData, role: e.target.value })}
                            className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-[#088178] focus:border-transparent"
                            required
                        >
                            <option value="customer">Customer (Khách hàng)</option>
                            <option value="seller">Seller (Người bán)</option>
                            <option value="admin">Admin (Quản trị viên)</option>
                        </select>
                        <p className="text-sm text-gray-500 mt-1">
                            Chọn vai trò phù hợp cho người dùng
                        </p>
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
                            {loading ? 'Đang thêm...' : 'Thêm người dùng'}
                        </button>
                    </div>
                </form>
            </div>

            <div className="mt-6 bg-blue-50 border border-blue-200 rounded-lg p-4">
                <h3 className="text-sm font-medium text-blue-800 mb-2">Lưu ý:</h3>
                <ul className="text-sm text-blue-700 space-y-1">
                    <li>• Tên đăng nhập phải có ít nhất 3 ký tự và là duy nhất</li>
                    <li>• Email phải có định dạng hợp lệ và chưa được sử dụng</li>
                    <li>• Mật khẩu phải có ít nhất 6 ký tự</li>
                    <li>• Vai trò quyết định quyền truy cập của người dùng trong hệ thống</li>
                </ul>
            </div>
        </div>
    );
};

export default AdminAddUser;
