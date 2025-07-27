import React, { useState, useEffect } from 'react';
import { useAuth } from '../contexts/AuthContext';
import { useCart } from '../contexts/CartContext';
import { getCurrentTokenInfo } from '../utils/tokenUtils';
import { cartService } from '../services/cart.service';
import { runCartTests } from '../utils/testCart';

const DebugPanel: React.FC = () => {
    const [isVisible, setIsVisible] = useState(false);
    const [tokenInfo, setTokenInfo] = useState<any>(null);
    const [cartInfo, setCartInfo] = useState<any>(null);
    const { user, isAuthenticated } = useAuth();
    const { items, totalItems, totalPrice, clearCart } = useCart();

    useEffect(() => {
        if (isVisible) {
            updateDebugInfo();
            const interval = setInterval(updateDebugInfo, 5000); // Update every 5 seconds
            return () => clearInterval(interval);
        }
    }, [isVisible, isAuthenticated, items]);

    const updateDebugInfo = () => {
        const token = getCurrentTokenInfo();
        const localCart = cartService.getLocalCart();

        setTokenInfo(token);
        setCartInfo({
            backendCartItems: items.length,
            localStorageCartItems: localCart.length,
            totalItems,
            totalPrice,
            localCart: localCart.map(item => ({
                productId: item.productId,
                quantity: item.quantity,
                title: item.product.title
            }))
        });
    };

    if (!isVisible) {
        return (
            <button
                onClick={() => setIsVisible(true)}
                className="fixed bottom-4 right-4 bg-blue-500 text-white px-3 py-1 rounded text-sm hover:bg-blue-600 z-50"
            >
                Debug
            </button>
        );
    }

    return (
        <div className="fixed bottom-4 right-4 bg-white border border-gray-300 rounded shadow-lg p-4 max-w-md z-50 max-h-96 overflow-y-auto">
            <div className="flex justify-between items-center mb-3">
                <h3 className="font-bold text-sm">Debug Info</h3>
                <button
                    onClick={() => setIsVisible(false)}
                    className="text-gray-500 hover:text-gray-700"
                >
                    ✕
                </button>
            </div>

            {/* Authentication Info */}
            <div className="mb-3">
                <h4 className="font-semibold text-xs text-blue-600 mb-1">Auth Status</h4>
                <div className="text-xs space-y-1">
                    <div>Authenticated: {isAuthenticated ? 'OK' : 'NO'}</div>
                    <div>User: {user?.username || 'None'}</div>
                    <div>Role: {user?.role || 'None'}</div>
                </div>
            </div>

            {/* Token Info */}
            <div className="mb-3">
                <h4 className="font-semibold text-xs text-green-600 mb-1">Token Info</h4>
                <div className="text-xs space-y-1">
                    <div>Has Token: {tokenInfo?.hasToken ? 'OK' : 'NO'}</div>
                    <div>Valid: {tokenInfo?.isValid ? 'OK' : 'NO'}</div>
                    {tokenInfo?.timeLeft && (
                        <div>Time Left: {Math.floor(tokenInfo.timeLeft / 60)}m {Math.floor(tokenInfo.timeLeft % 60)}s</div>
                    )}
                    {tokenInfo?.decoded && (
                        <div>Username: {tokenInfo.decoded.username || tokenInfo.decoded.sub}</div>
                    )}
                </div>
            </div>

            {/* Cart Info */}
            <div className="mb-3">
                <h4 className="font-semibold text-xs text-purple-600 mb-1">Cart Info</h4>
                <div className="text-xs space-y-1">
                    <div>Backend Items: {cartInfo?.backendCartItems || 0}</div>
                    <div>LocalStorage Items: {cartInfo?.localStorageCartItems || 0}</div>
                    <div>Total Items: {totalItems}</div>
                    <div>Total Price: {totalPrice.toLocaleString('vi-VN')} VND</div>
                </div>
            </div>

            {/* Auth Debug Section */}
            <div className="mb-3">
                <h4 className="font-semibold text-xs text-red-600 mb-1">Debug Actions</h4>
                <div className="text-xs">
                    {isAuthenticated && tokenInfo && !tokenInfo.isValid && (
                        <div className="bg-red-100 border border-red-300 text-red-700 px-2 py-1 rounded mb-2">
                            Auth state mismatch detected! User authenticated but token invalid.
                        </div>
                    )}
                </div>
            </div>

            {/* LocalStorage Cart Details */}
            {cartInfo?.localCart && cartInfo.localCart.length > 0 && (
                <div className="mb-3">
                    <h4 className="font-semibold text-xs text-orange-600 mb-1">LocalStorage Cart</h4>
                    <div className="text-xs space-y-1 max-h-20 overflow-y-auto">
                        {cartInfo.localCart.map((item: any, index: number) => (
                            <div key={index} className="flex justify-between">
                                <span className="truncate">{item.title}</span>
                                <span>x{item.quantity}</span>
                            </div>
                        ))}
                    </div>
                </div>
            )}
            {/* Actions */}
            <div className="border-t pt-2 mt-2">
                <div className="flex flex-wrap gap-2">
                    <button
                        onClick={() => updateDebugInfo()}
                        className="text-xs bg-gray-200 px-2 py-1 rounded hover:bg-gray-300"
                    >
                        Refresh
                    </button>                    <button
                        onClick={() => runCartTests()}
                        className="text-xs bg-blue-200 px-2 py-1 rounded hover:bg-blue-300"
                    >
                        Test Cart
                    </button>
                    <button
                        onClick={async () => {
                            if (confirm('Bạn có chắc chắn muốn xóa toàn bộ giỏ hàng?')) {
                                try {
                                    await clearCart();
                                    updateDebugInfo();
                                    console.log('Cart cleared successfully');
                                } catch (error) {
                                    console.error('Error clearing cart:', error);
                                }
                            }
                        }}
                        className="text-xs bg-orange-200 px-2 py-1 rounded hover:bg-orange-300"
                    >
                        Clear Cart
                    </button>
                    <button
                        onClick={() => {
                            // Force check token validity and clear if invalid
                            const tokenInfo = getCurrentTokenInfo();
                            console.log('Manual token check:', tokenInfo);
                            if (!tokenInfo.isValid && isAuthenticated) {
                                console.log('Clearing invalid auth state');
                                localStorage.removeItem('authToken');
                                localStorage.removeItem('user');
                                window.location.reload();
                            } else {
                                console.log('Auth state appears correct');
                            }
                        }}
                        className="text-xs bg-yellow-200 px-2 py-1 rounded hover:bg-yellow-300"
                    >
                        Fix Auth
                    </button>
                    <button
                        onClick={() => {
                            localStorage.removeItem('authToken');
                            localStorage.removeItem('cart');
                            window.location.reload();
                        }}
                        className="text-xs bg-red-200 px-2 py-1 rounded hover:bg-red-300"
                    >
                        Clear All
                    </button>
                </div>
            </div>
        </div>
    );
};

export default DebugPanel;
