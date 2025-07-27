// Quick admin login helper for development
// This should only be used for debugging/testing purposes

export const quickAdminLogin = async () => {
    const loginData = {
        username: 'admin',
        password: 'admin123'
    };

    try {
        const response = await fetch('http://localhost:8080/api/auth/login', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(loginData)
        });

        if (response.ok) {
            const authData = await response.json();
            if (authData.token && authData.user) {
                localStorage.setItem('authToken', authData.token);
                localStorage.setItem('user', JSON.stringify(authData.user));
                console.log('✅ Admin login successful');
                console.log('User:', authData.user);
                console.log('Role:', authData.user.role);

                // Refresh the page to update auth state
                window.location.reload();
                return true;
            }
        } else {
            console.error('❌ Login failed:', response.status, response.statusText);
            return false;
        }
    } catch (error) {
        console.error('❌ Login error:', error);
        return false;
    }
};

// Auto-execute if running in development and no current user
if (import.meta.env.DEV) {
    const currentUser = localStorage.getItem('user');
    if (!currentUser || currentUser === 'null' || currentUser === 'undefined') {
        console.log('🚀 No user found, attempting admin login...');
        // Add delay to allow DOM to load
        setTimeout(quickAdminLogin, 1000);
    } else {
        try {
            const userData = JSON.parse(currentUser);
            console.log('Current user role:', userData.role);
            if (userData.role !== 'admin') {
                console.log('⚠️ Current user is not admin. Use quickAdminLogin() to switch to admin.');
            }
        } catch (e) {
            console.error('Error parsing user data');
        }
    }
}

// Export for manual use
(window as any).quickAdminLogin = quickAdminLogin;
