import React, { useEffect } from 'react';

interface ToastProps {
    message: string;
    type: 'success' | 'error' | 'warning' | 'info';
    onClose: () => void;
    duration?: number;
}

const Toast: React.FC<ToastProps> = ({ message, type, onClose, duration = 5000 }) => {
    useEffect(() => {
        const timer = setTimeout(() => {
            onClose();
        }, duration);

        return () => clearTimeout(timer);
    }, [onClose, duration]);

    const getToastStyles = () => {
        switch (type) {
            case 'success':
                return 'bg-green-100 border-green-400 text-green-700';
            case 'error':
                return 'bg-red-100 border-red-400 text-red-700';
            case 'warning':
                return 'bg-yellow-100 border-yellow-400 text-yellow-700';
            case 'info':
                return 'bg-blue-100 border-blue-400 text-blue-700';
            default:
                return 'bg-gray-100 border-gray-400 text-gray-700';
        }
    };

    const getIcon = () => {
        switch (type) {
            case 'success':
                return 'fas fa-check-circle';
            case 'error':
                return 'fas fa-exclamation-triangle';
            case 'warning':
                return 'fas fa-exclamation-triangle';
            case 'info':
                return 'fas fa-info-circle';
            default:
                return 'fas fa-info-circle';
        }
    };

    return (
        <div className={`fixed top-4 right-4 z-50 p-4 border rounded-lg shadow-lg transition-all duration-300 ${getToastStyles()}`}>
            <div className="flex items-start justify-between">
                <div className="flex items-start">
                    <i className={`${getIcon()} mr-2 mt-1`}></i>
                    <span className="text-sm">{message}</span>
                </div>
                <button
                    onClick={onClose}
                    className="ml-4 text-lg hover:opacity-70 transition-opacity"
                >
                    &times;
                </button>
            </div>
        </div>
    );
};

export default Toast;
