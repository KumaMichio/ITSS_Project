import React, { createContext, useContext, useState, useEffect, ReactNode } from 'react';
import { cartService } from '../services/cart.service';
import { productService } from '../services/product.service';
import { mockProducts } from '../data/mockData';
import type { CartItem, Product } from '../types';
import { useAuth } from './AuthContext';

interface CartContextType {
    items: CartItem[];
    totalItems: number;
    totalPrice: number;
    isLoading: boolean;
    addToCart: (product: Product, quantity?: number) => Promise<{ success: boolean; message?: string }>;
    updateQuantity: (productId: number, quantity: number) => Promise<{ success: boolean; message?: string }>;
    removeFromCart: (productId: number) => Promise<void>;
    clearCart: () => void;
    refreshCart: () => Promise<void>;
    checkStock: (productId: number, quantity: number) => Promise<{ success: boolean; message?: string }>;
    validateCartStock: () => Promise<{ success: boolean; message?: string; invalidItems?: string[] }>;
}

const CartContext = createContext<CartContextType | undefined>(undefined);

export const useCart = () => {
    const context = useContext(CartContext);
    if (context === undefined) {
        throw new Error('useCart must be used within a CartProvider');
    }
    return context;
};

interface CartProviderProps {
    children: ReactNode;
}

export const CartProvider: React.FC<CartProviderProps> = ({ children }) => {
    const [items, setItems] = useState<CartItem[]>([]);
    const [isLoading, setIsLoading] = useState(false);
    const { isAuthenticated } = useAuth();

    useEffect(() => {
        loadCart();
    }, [isAuthenticated]); const loadCart = async () => {
        setIsLoading(true);
        try {
            if (isAuthenticated) {
                // Load cart from backend
                const response = await cartService.getAllOrderItems();
                if (response.success && response.data) {
                    // Convert backend order items to cart items format
                    const cartItems: CartItem[] = response.data.map((item: any) => ({
                        productId: item.productId,
                        quantity: item.quantity,
                        product: {
                            id: item.productId,
                            price: item.price / item.quantity, // Calculate unit price
                            // Add other product properties as needed
                        } as Product
                    }));
                    setItems(cartItems);
                }
            } else {
                // Load cart from localStorage
                const localCart = cartService.getLocalCart();
                setItems(localCart);
            }
        } catch (error) {
            console.error('Failed to load cart:', error);
        } finally {
            setIsLoading(false);
        }
    }; const checkStock = async (productId: number, quantity: number): Promise<{ success: boolean; message?: string }> => {
        try {
            // Get current product information to check available stock
            const response = await productService.getProductById(productId);
            if (response.success && response.data) {
                const product = response.data;
                const availableStock = product.quantity;

                // Check if requested quantity exceeds available stock
                if (quantity > availableStock) {
                    return {
                        success: false,
                        message: `Số lượng yêu cầu (${quantity}) vượt quá số lượng có sẵn (${availableStock}) của sản phẩm "${product.title}"`
                    };
                }

                return { success: true };
            } else {
                // Fallback to mock data if backend response failed
                console.warn('Backend response failed, falling back to mock data for stock check');
                const mockProduct = mockProducts.find(p => p.id === productId);
                if (mockProduct) {
                    const availableStock = mockProduct.quantity;

                    if (quantity > availableStock) {
                        return {
                            success: false,
                            message: `Số lượng yêu cầu (${quantity}) vượt quá số lượng có sẵn (${availableStock}) của sản phẩm "${mockProduct.title}" (sử dụng dữ liệu offline)`
                        };
                    }

                    return { success: true };
                } else {
                    return { success: false, message: 'Không tìm thấy thông tin sản phẩm' };
                }
            }
        } catch (error) {
            console.error('Failed to check stock from backend, trying mock data:', error);

            // Fallback to mock data when backend is completely unavailable
            const mockProduct = mockProducts.find(p => p.id === productId);
            if (mockProduct) {
                const availableStock = mockProduct.quantity;

                if (quantity > availableStock) {
                    return {
                        success: false,
                        message: `Số lượng yêu cầu (${quantity}) vượt quá số lượng có sẵn (${availableStock}) của sản phẩm "${mockProduct.title}" (sử dụng dữ liệu offline)`
                    };
                }

                return { success: true };
            } else {
                console.warn('Product not found in mock data, allowing add to cart');
                return { success: true }; // Allow add to cart if product not found in mock data
            }
        }
    }; const addToCart = async (product: Product, quantity: number = 1): Promise<{ success: boolean; message?: string }> => {
        // Kiểm tra tồn kho trước khi thêm
        const stockCheck = await checkStock(product.id, quantity);
        if (!stockCheck.success) {
            return stockCheck;
        }

        setIsLoading(true);
        try {
            if (isAuthenticated) {
                // Add to backend cart as OrderItem
                const orderItem = {
                    productId: product.id,
                    quantity: quantity,
                    price: product.price * quantity
                };

                const response = await cartService.addOrderItem(orderItem);
                if (response.success) {
                    await refreshCart();
                    return { success: true };
                } else {
                    return { success: false, message: response.error || 'Không thể thêm sản phẩm vào giỏ hàng' };
                }
            } else {
                // Add to local cart
                const cartItem: CartItem = {
                    productId: product.id,
                    quantity,
                    product
                };
                cartService.addToLocalCart(cartItem);
                setItems(cartService.getLocalCart());
                return { success: true };
            }
        } catch (error) {
            console.error('Failed to add to cart:', error);
            return { success: false, message: 'Lỗi khi thêm sản phẩm vào giỏ hàng' };
        } finally {
            setIsLoading(false);
        }
    }; const updateQuantity = async (productId: number, quantity: number): Promise<{ success: boolean; message?: string }> => {
        if (quantity <= 0) {
            await removeFromCart(productId);
            return { success: true };
        }        // Check stock before updating quantity
        const stockCheck = await checkStock(productId, quantity);
        if (!stockCheck.success) {
            return stockCheck;
        }

        setIsLoading(true);
        try {
            if (isAuthenticated) {
                // Find the item in backend to get its ID
                const currentItem = items.find(item => item.productId === productId);
                if (currentItem) {
                    // Update order item by ID in backend
                    const orderItem = {
                        productId: productId,
                        quantity: quantity,
                        price: currentItem.product.price * quantity
                    };

                    // We need to find the orderItem ID to update it
                    // For now, we'll assume the first match or implement a better lookup
                    const response = await cartService.updateOrderItem(currentItem.productId, orderItem);
                    if (response.success) {
                        await refreshCart();
                        return { success: true };
                    } else {
                        return { success: false, message: response.error || 'Không thể cập nhật số lượng' };
                    }
                } else {
                    return { success: false, message: 'Không tìm thấy sản phẩm trong giỏ hàng' };
                }
            } else {
                const cart = cartService.getLocalCart();
                const updatedCart = cart.map(item =>
                    item.productId === productId ? { ...item, quantity } : item
                );
                cartService.setLocalCart(updatedCart);
                setItems(updatedCart);
                return { success: true };
            }
        } catch (error) {
            console.error('Failed to update cart:', error);
            return { success: false, message: 'Lỗi khi cập nhật số lượng' };
        } finally {
            setIsLoading(false);
        }
    }; const removeFromCart = async (productId: number) => {
        setIsLoading(true);
        try {
            if (isAuthenticated) {
                // Find the item in backend to get its ID
                const currentItem = items.find(item => item.productId === productId);
                if (currentItem) {
                    // Delete order item by ID in backend
                    const response = await cartService.deleteOrderItem(currentItem.productId);
                    if (response.success) {
                        await refreshCart();
                    }
                }
            } else {
                cartService.removeFromLocalCart(productId);
                setItems(cartService.getLocalCart());
            }
        } catch (error) {
            console.error('Failed to remove from cart:', error);
        } finally {
            setIsLoading(false);
        }
    }; const clearCart = () => {
        if (isAuthenticated) {
            // Clear backend cart - would need API endpoint
            console.log('Clear backend cart');
        } else {
            cartService.clearLocalCart();
        }
        setItems([]);
    };

    const validateCartStock = async (): Promise<{ success: boolean; message?: string; invalidItems?: string[] }> => {
        try {
            const invalidItems: string[] = [];

            for (const item of items) {
                const stockCheck = await checkStock(item.productId, item.quantity);
                if (!stockCheck.success) {
                    invalidItems.push(`${item.product.title}: ${stockCheck.message}`);
                }
            }

            if (invalidItems.length > 0) {
                return {
                    success: false,
                    message: 'Một số sản phẩm trong giỏ hàng không đủ số lượng',
                    invalidItems
                };
            }

            return { success: true };
        } catch (error) {
            console.error('Failed to validate cart stock:', error);
            return {
                success: false,
                message: 'Lỗi khi kiểm tra số lượng sản phẩm trong giỏ hàng'
            };
        }
    };

    const refreshCart = async () => {
        await loadCart();
    };

    const totalItems = items.reduce((sum, item) => sum + item.quantity, 0);
    const totalPrice = items.reduce((sum, item) => sum + (item.product.price * item.quantity), 0); const value: CartContextType = {
        items,
        totalItems,
        totalPrice,
        isLoading,
        addToCart,
        updateQuantity,
        removeFromCart,
        clearCart,
        refreshCart,
        checkStock,
        validateCartStock,
    };

    return (
        <CartContext.Provider value={value}>
            {children}
        </CartContext.Provider>
    );
};
