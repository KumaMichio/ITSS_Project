import React, { Component, ErrorInfo, ReactNode } from 'react';

interface Props {
    children: ReactNode;
    fallback?: ReactNode;
}

interface State {
    hasError: boolean;
    error?: Error;
}

class ErrorBoundary extends Component<Props, State> {
    public state: State = {
        hasError: false
    };

    public static getDerivedStateFromError(error: Error): State {
        return { hasError: true, error };
    }

    public componentDidCatch(error: Error, errorInfo: ErrorInfo) {
        console.error('ErrorBoundary caught an error:', error, errorInfo);
    }

    public render() {
        if (this.state.hasError) {
            if (this.props.fallback) {
                return this.props.fallback;
            }

            return (
                <div className="min-h-screen bg-gray-50 flex flex-col justify-center py-12 sm:px-6 lg:px-8">
                    <div className="sm:mx-auto sm:w-full sm:max-w-md">
                        <div className="bg-white py-8 px-4 shadow sm:rounded-lg sm:px-10">
                            <div className="text-center">
                                <i className="fas fa-exclamation-triangle text-6xl text-red-500 mb-4"></i>
                                <h2 className="text-2xl font-bold text-gray-900 mb-4">
                                    Oops! Something went wrong
                                </h2>
                                <p className="text-gray-600 mb-6">
                                    We're sorry, but something unexpected happened. Please try refreshing the page.
                                </p>
                                <div className="space-y-3">
                                    <button
                                        onClick={() => window.location.reload()}
                                        className="w-full flex justify-center py-2 px-4 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-[#088178] hover:bg-[#066e6a] focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-[#088178]"
                                    >
                                        Refresh Page
                                    </button>
                                    <button
                                        onClick={() => this.setState({ hasError: false })}
                                        className="w-full flex justify-center py-2 px-4 border border-gray-300 rounded-md shadow-sm text-sm font-medium text-gray-700 bg-white hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-[#088178]"
                                    >
                                        Try Again
                                    </button>
                                </div>
                                {process.env.NODE_ENV === 'development' && this.state.error && (
                                    <details className="mt-4 text-left">
                                        <summary className="cursor-pointer text-sm text-gray-500">
                                            Error Details (Development)
                                        </summary>
                                        <pre className="mt-2 text-xs text-red-600 bg-red-50 p-2 rounded overflow-auto">
                                            {this.state.error.toString()}
                                        </pre>
                                    </details>
                                )}
                            </div>
                        </div>
                    </div>
                </div>
            );
        }

        return this.props.children;
    }
}

export default ErrorBoundary;
