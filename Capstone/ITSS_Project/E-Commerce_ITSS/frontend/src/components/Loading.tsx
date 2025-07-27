import type { FC } from 'react';

interface LoadingProps {
    message?: string;
    size?: 'sm' | 'md' | 'lg';
}

const Loading: FC<LoadingProps> = ({ message = 'Đang tải...', size = 'md' }) => {
    const sizeClasses = {
        sm: 'text-2xl',
        md: 'text-4xl',
        lg: 'text-6xl'
    };

    return (
        <div className="flex justify-center items-center min-h-[400px]">
            <div className="text-center">
                <i className={`fas fa-spinner fa-spin ${sizeClasses[size]} text-[#088178] mb-4`}></i>
                <p className="text-gray-600">{message}</p>
            </div>
        </div>
    );
};

export default Loading;
