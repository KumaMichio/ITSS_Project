import React, { useState } from 'react';
import { useAuth } from '../../contexts/AuthContext';
import { Navigate } from 'react-router-dom';
import Header from '../../components/Header';
import AdminSidebar from '../../components/AdminSidebar';
import AdminDashboard from '../../components/admin/AdminDashboard';
import AdminProductManagement from '../../components/admin/AdminProductManagement';
import AdminAddProduct from '../../components/admin/AdminAddProduct';
import AdminUserManagement from '../../components/admin/AdminUserManagement';
import AdminAddUser from '../../components/admin/AdminAddUser';

const AdminPanel: React.FC = () => {
    const { user, isAuthenticated } = useAuth();
    const [activeSection, setActiveSection] = useState('dashboard');

    // Check if user is authenticated and has admin role
    if (!isAuthenticated || !user) {
        return <Navigate to="/login" replace />;
    }

    if (user.role !== 'admin') {
        return <Navigate to="/" replace />;
    } const renderContent = () => {
        switch (activeSection) {
            case 'dashboard':
                return <AdminDashboard />;
            case 'products':
                return <AdminProductManagement />;
            case 'add-product':
                return <AdminAddProduct />;
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
