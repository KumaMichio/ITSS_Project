import React from 'react';

interface AdminSidebarProps {
    activeSection: string;
    onSectionChange: (section: string) => void;
}

const AdminSidebar: React.FC<AdminSidebarProps> = ({ activeSection, onSectionChange }) => {
    const menuItems = [
        { id: 'dashboard', label: 'Dashboard thống kê', icon: '📊' },
        { id: 'products', label: 'Quản lý sản phẩm', icon: '📦' },
        { id: 'add-product', label: 'Thêm sản phẩm', icon: '➕' },
        { id: 'payments', label: 'Quản lý đơn hàng & thanh toán', icon: '💳' },
        { id: 'user-management', label: 'Quản lý người dùng', icon: '⚙️' },
        { id: 'add-user', label: 'Thêm người dùng', icon: '👤' },
    ];

    return (
        <div className="w-64 bg-white shadow-lg min-h-screen">
            <div className="p-6">
                <h2 className="text-xl font-bold text-gray-800 mb-6">ADMIN PANEL</h2>
            </div>

            <nav className="mt-6">
                {menuItems.map((item) => (
                    <button
                        key={item.id}
                        onClick={() => onSectionChange(item.id)}
                        className={`w-full flex items-center px-6 py-3 text-left hover:bg-gray-100 transition-colors ${activeSection === item.id
                            ? 'bg-[#088178] text-white hover:bg-[#066e6a]'
                            : 'text-gray-700'
                            }`}
                    >
                        <span className="mr-3 text-lg">{item.icon}</span>
                        <span className="font-medium">{item.label}</span>
                    </button>))}
            </nav>
        </div>
    );
};

export default AdminSidebar;
