import React, { useState, useEffect } from 'react';
import { userService } from '../../services/user.service';
import type { User } from '../../types';

interface EditUserModalProps {
    user: User;
    onClose: () => void;
    onUpdate: (user: User) => void;
}

const EditUserModal: React.FC<EditUserModalProps> = ({ user, onClose, onUpdate }) => {
    const [formData, setFormData] = useState({
        username: user.username,
        email: user.email,
        role: user.role || 'customer'
    });
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState<string | null>(null);

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        setLoading(true);
        setError(null);

        try {
            const response = await userService.updateUser(user.userId, formData);
            if (response.success) {
                onUpdate(response.data);
                onClose();
            } else {
                setError(response.error || 'Lỗi khi cập nhật người dùng');
            }
        } catch (err) {
            console.error('Error updating user:', err);
            setError('Lỗi kết nối với server');
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
            <div className="bg-white rounded-lg p-6 w-full max-w-md mx-4">
                <div className="flex justify-between items-center mb-4">
                    <h2 className="text-xl font-bold text-gray-800">Sửa người dùng</h2>
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
                    <div>
                        <label className="block text-sm font-medium text-gray-700 mb-1">
                            Tên đăng nhập
                        </label>
                        <input
                            type="text"
                            value={formData.username}
                            onChange={(e) => setFormData({ ...formData, username: e.target.value })}
                            className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-[#088178] focus:border-transparent"
                            required
                        />
                    </div>

                    <div>
                        <label className="block text-sm font-medium text-gray-700 mb-1">
                            Email
                        </label>
                        <input
                            type="email"
                            value={formData.email}
                            onChange={(e) => setFormData({ ...formData, email: e.target.value })}
                            className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-[#088178] focus:border-transparent"
                            required
                        />
                    </div>                    <div>
                        <label className="block text-sm font-medium text-gray-700 mb-1">
                            Vai trò
                        </label>
                        <select
                            value={formData.role}
                            onChange={(e) => setFormData({ ...formData, role: e.target.value })}
                            className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-[#088178] focus:border-transparent"
                        >
                            <option value="customer">Khách hàng</option>
                            <option value="seller">Người bán</option>
                            <option value="admin">Quản trị viên</option>
                        </select>
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

const AdminUserManagement: React.FC = () => {
    const [users, setUsers] = useState<User[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);
    const [searchTerm, setSearchTerm] = useState('');
    const [filterRole, setFilterRole] = useState('all');
    const [editingUser, setEditingUser] = useState<User | null>(null);
    const [deletingUserId, setDeletingUserId] = useState<number | null>(null);

    useEffect(() => {
        fetchUsers();
    }, []);

    const fetchUsers = async () => {
        try {
            setLoading(true);
            setError(null);
            const response = await userService.getAllUsers();

            if (response.success) {
                setUsers(response.data);
            } else {
                setError(response.error || 'Lỗi khi tải danh sách người dùng');
            }
        } catch (err) {
            console.error('Error fetching users:', err);
            setError('Lỗi kết nối với server');
        } finally {
            setLoading(false);
        }
    };

    const handleDeleteUser = async (userId: number) => {
        if (!confirm('Bạn có chắc chắn muốn xóa người dùng này?')) {
            return;
        }

        setDeletingUserId(userId);
        try {
            const response = await userService.deleteUser(userId);
            if (response.success) {
                setUsers(users.filter(user => user.userId !== userId));
            } else {
                alert(response.error || 'Lỗi khi xóa người dùng');
            }
        } catch (err) {
            console.error('Error deleting user:', err);
            alert('Lỗi kết nối với server');
        } finally {
            setDeletingUserId(null);
        }
    };

    const handleChangeRole = async (userId: number, newRole: string) => {
        if (!confirm(`Bạn có chắc chắn muốn thay đổi vai trò người dùng thành "${newRole}"?`)) {
            return;
        }

        try {
            const response = await userService.changeUserRole(userId, newRole);
            if (response.success) {
                setUsers(users.map(user =>
                    user.userId === userId
                        ? { ...user, role: newRole }
                        : user
                ));
            } else {
                alert(response.error || 'Lỗi khi thay đổi vai trò');
            }
        } catch (err) {
            console.error('Error changing user role:', err);
            alert('Lỗi kết nối với server');
        }
    };

    const handleUpdateUser = (updatedUser: User) => {
        setUsers(users.map(user =>
            user.userId === updatedUser.userId ? updatedUser : user
        ));
    }; const filteredUsers = users.filter(user => {
        const matchesSearch = user.username.toLowerCase().includes(searchTerm.toLowerCase()) ||
            user.email.toLowerCase().includes(searchTerm.toLowerCase());

        const matchesRole = filterRole === 'all' || user.role === filterRole;

        return matchesSearch && matchesRole;
    });

    const getRoleColor = (role?: string) => {
        switch (role) {
            case 'admin':
                return 'bg-red-100 text-red-800';
            case 'seller':
                return 'bg-yellow-100 text-yellow-800';
            case 'customer':
            default:
                return 'bg-blue-100 text-blue-800';
        }
    };

    const getRoleDisplay = (role?: string) => {
        switch (role) {
            case 'admin':
                return 'Quản trị viên';
            case 'seller':
                return 'Người bán';
            case 'customer':
            default:
                return 'Khách hàng';
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
                    onClick={fetchUsers}
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
                <h1 className="text-3xl font-bold text-gray-800">Quản Lý Người Dùng</h1>
                <button
                    onClick={fetchUsers}
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
                            Tìm kiếm người dùng
                        </label>
                        <input
                            type="text"
                            placeholder="Tìm theo tên, email..."
                            value={searchTerm}
                            onChange={(e) => setSearchTerm(e.target.value)}
                            className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-[#088178] focus:border-transparent"
                        />
                    </div>
                    <div className="md:w-48">
                        <label className="block text-sm font-medium text-gray-700 mb-2">
                            Lọc theo vai trò
                        </label>
                        <select
                            value={filterRole}
                            onChange={(e) => setFilterRole(e.target.value)}
                            className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-[#088178] focus:border-transparent"
                        >
                            <option value="all">Tất cả</option>
                            <option value="customer">Khách hàng</option>
                            <option value="seller">Người bán</option>
                            <option value="admin">Quản trị viên</option>
                        </select>
                    </div>
                </div>
            </div>

            {/* Users Management Table */}
            <div className="bg-white rounded-lg shadow-md overflow-hidden">
                <div className="px-6 py-4 bg-gray-50 border-b">
                    <h2 className="text-lg font-semibold text-gray-800">
                        Tổng cộng: {filteredUsers.length} người dùng
                        {searchTerm && ` (Lọc từ ${users.length} người dùng)`}
                    </h2>
                </div>

                {filteredUsers.length === 0 ? (
                    <div className="text-center py-12">
                        <div className="text-gray-400 mb-4">
                            <svg className="w-16 h-16 mx-auto" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 4.354a4 4 0 110 5.292M15 21H3v-1a6 6 0 0112 0v1zm0 0h6v-1a6 6 0 00-9-5.197m13.5-9a2.5 2.5 0 11-5 0 2.5 2.5 0 015 0z" />
                            </svg>
                        </div>
                        <p className="text-gray-500 text-lg">
                            {searchTerm ? 'Không tìm thấy người dùng phù hợp' : 'Không có người dùng nào'}
                        </p>
                    </div>
                ) : (
                    <div className="overflow-x-auto">
                        <table className="w-full">
                            <thead className="bg-gray-50">
                                <tr>
                                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                                        Thông tin người dùng
                                    </th>
                                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                                        Vai trò
                                    </th>
                                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                                        Thao tác
                                    </th>
                                </tr>
                            </thead>
                            <tbody className="bg-white divide-y divide-gray-200">
                                {filteredUsers.map((user) => (
                                    <tr key={user.userId} className="hover:bg-gray-50">
                                        <td className="px-6 py-4">                                            <div>
                                            <div className="text-sm font-medium text-gray-900">
                                                {user.username} (#{user.userId})
                                            </div>
                                            <div className="text-sm text-gray-500">{user.email}</div>
                                        </div>
                                        </td>
                                        <td className="px-6 py-4 whitespace-nowrap">
                                            <div className="flex flex-col space-y-2">
                                                <span className={`px-2 inline-flex text-xs leading-5 font-semibold rounded-full ${getRoleColor(user.role)}`}>
                                                    {getRoleDisplay(user.role)}
                                                </span>
                                                <select
                                                    value={user.role || 'customer'}
                                                    onChange={(e) => handleChangeRole(user.userId, e.target.value)}
                                                    className="text-xs border border-gray-300 rounded px-2 py-1 focus:outline-none focus:ring-1 focus:ring-[#088178]"
                                                >
                                                    <option value="customer">Khách hàng</option>
                                                    <option value="seller">Người bán</option>
                                                    <option value="admin">Quản trị viên</option>
                                                </select>
                                            </div>
                                        </td>
                                        <td className="px-6 py-4 whitespace-nowrap text-sm font-medium">
                                            <div className="flex space-x-2">
                                                <button
                                                    onClick={() => setEditingUser(user)}
                                                    className="text-indigo-600 hover:text-indigo-900 px-3 py-1 border border-indigo-300 rounded-lg hover:bg-indigo-50 transition"
                                                >
                                                    Sửa
                                                </button>
                                                <button
                                                    onClick={() => handleDeleteUser(user.userId)}
                                                    disabled={deletingUserId === user.userId}
                                                    className="text-red-600 hover:text-red-900 px-3 py-1 border border-red-300 rounded-lg hover:bg-red-50 transition disabled:opacity-50"
                                                >
                                                    {deletingUserId === user.userId ? 'Đang xóa...' : 'Xóa'}
                                                </button>
                                            </div>
                                        </td>
                                    </tr>
                                ))}
                            </tbody>
                        </table>
                    </div>
                )}
            </div>

            {/* Edit User Modal */}
            {editingUser && (
                <EditUserModal
                    user={editingUser}
                    onClose={() => setEditingUser(null)}
                    onUpdate={handleUpdateUser}
                />
            )}
        </div>
    );
};

export default AdminUserManagement;
