// Debug utility for authentication issues
export const debugAuth = {
    clearAllStorageData: () => {
        localStorage.removeItem('authToken');
        localStorage.removeItem('user');
        console.log('All auth data cleared from localStorage');
    },

    checkStorageData: () => {
        const token = localStorage.getItem('authToken');
        const user = localStorage.getItem('user');

        console.log('Current localStorage data:');
        console.log('authToken:', token);
        console.log('user:', user);

        if (user === 'undefined' || user === 'null') {
            console.warn('Corrupted user data detected in localStorage');
            return false;
        }

        return true;
    }
};

// Auto-clear corrupted data on module load
if (typeof window !== 'undefined') {
    const user = localStorage.getItem('user');
    if (user === 'undefined' || user === 'null') {
        console.warn('Clearing corrupted localStorage data...');
        debugAuth.clearAllStorageData();
    }
}
