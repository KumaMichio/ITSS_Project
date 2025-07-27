import React, { useState } from 'react';
import { useAuth } from '../../contexts/AuthContext';
import { Navigate } from 'react-router-dom';
import Header from '../../components/Header';
import AdminSidebar from '../../components/AdminSidebar';
import AdminDashboard from '../../components/admin/AdminDashboard';
import AdminProductManagement from '../../components/admin/AdminProductManagement';
import AdminAddProduct from '../../components/admin/AdminAddProduct';
import AdminOrderPaymentManagement from '../../components/admin/AdminOrderPaymentManagement';
import AdminUserManagement from '../../components/admin/AdminUserManagement';
import AdminAddUser from '../../components/admin/AdminAddUser';

const AdminPanel: React.FC = () => {
    const { user, isAuthenticated, isLoading } = useAuth();
    const [activeSection, setActiveSection] = useState('dashboard');

    // Debug logging
    console.log('AdminPanel render:', {
        isLoading,
        isAuthenticated,
        user: user ? { username: user.username, role: user.role } : null
    });

    // Show loading while AuthContext is still checking authentication
    if (isLoading) {
        console.log('AdminPanel: Still loading, showing spinner');
        return (
            <div className="flex justify-center items-center min-h-screen">
                <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-[#088178]"></div>
            </div>
        );
    }

    // Check if user is authenticated and has admin role
    if (!isAuthenticated || !user) {
        console.log('AdminPanel: Not authenticated or no user, redirecting to login');
        return <Navigate to="/login" replace />;
    }

    if (user.role !== 'admin') {
        console.log('AdminPanel: User is not admin, redirecting to home');
        return <Navigate to="/" replace />;
    } const renderContent = () => {
        switch (activeSection) {
            case 'dashboard':
                return <AdminDashboard />;
            case 'products':
                return <AdminProductManagement />;
            case 'add-product':
                return <AdminAddProduct />;
            case 'payments':
                return <AdminOrderPaymentManagement />;
            case 'user-management':
                return <AdminUserManagement />;
            case 'add-user':
                return <AdminAddUser />;
            default:
                return <AdminDashboard />;
        }
    }; return (
        <>
            <Header />
            <div className="flex min-h-screen bg-gray-100">
                <AdminSidebar
                    activeSection={activeSection}
                    onSectionChange={setActiveSection}
                />
                <div className="flex-1 overflow-hidden">
                    <div className="h-full overflow-y-auto">
                        {renderContent()}
                    </div>
                </div>
            </div>
        </>
    );
};

export default AdminPanel;
