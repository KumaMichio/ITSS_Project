import React, { createContext, useContext, useState, useEffect, ReactNode } from 'react';
import { cartService } from '../services/cart.service';
import { productService } from '../services/product.service';
import { mockProducts } from '../data/mockData';
import { getCurrentTokenInfo } from '../utils/tokenUtils';
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
    mergeLocalCartToBackend: () => Promise<void>;
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
    const { isAuthenticated, checkTokenValidity } = useAuth();

    const mergeLocalCartToBackend = async () => {
        console.log('Merging local cart to backend');
        try {
            const localCart = cartService.getLocalCart();
            console.log('Local cart to merge:', localCart);

            if (localCart.length === 0) {
                console.log('No local cart items to merge');
                return;
            }

            // Add each local cart item to backend
            for (const item of localCart) {
                try {
                    const orderItem = {
                        productId: item.productId,
                        quantity: item.quantity,
                        price: item.product.price * item.quantity
                    };

                    console.log('Merging item to backend:', orderItem);
                    const response = await cartService.addOrderItem(orderItem);
                    console.log('Merge item response:', response);

                    if (!response.success) {
                        console.warn(`Failed to merge item ${item.productId}:`, response.error);
                    }
                } catch (error) {
                    console.error(`Error merging item ${item.productId}:`, error);
                }
            }

            // Clear local cart after successful merge
            console.log('Clearing local cart after merge');
            cartService.clearLocalCart();

            // Refresh cart to show merged items
            await loadCart();
        } catch (error) {
            console.error('Failed to merge local cart to backend:', error);
        }
    }; useEffect(() => {
        const handleAuthChange = async () => {
            if (isAuthenticated) {
                // User just logged in - check if there's a local cart to merge
                const localCart = cartService.getLocalCart();
                console.log('User authenticated, checking local cart:', localCart);

                if (localCart.length > 0) {
                    console.log('Found local cart items, merging to backend');
                    await mergeLocalCartToBackend();
                } else {
                    console.log('No local cart items, loading from backend');
                    await loadCart();
                }
            } else {
                // User logged out - load from localStorage
                console.log('User not authenticated, loading from localStorage');
                await loadCart();
            }
        };

        handleAuthChange();
    }, [isAuthenticated]);

    // Debug useEffect to track items changes
    useEffect(() => {
        console.log('Cart items state changed:', items);
        console.log('Cart items length:', items.length);
    }, [items]); const loadCart = async () => {
        console.log('CartContext loadCart:', { isAuthenticated });
        setIsLoading(true);
        try {
            if (isAuthenticated) {
                // Check token validity before making backend call
                const tokenInfo = getCurrentTokenInfo();
                console.log('Token check for loadCart:', tokenInfo); if (tokenInfo.hasToken && tokenInfo.isValid) {
                    console.log('Valid token found, trying to load from backend');
                    // Load cart from backend
                    const response = await cartService.getAllOrderItems();
                    console.log('Backend cart response:', response);
                    console.log('Backend cart response.success:', response.success);
                    console.log('Backend cart response.data:', response.data);
                    console.log('Backend cart response.data type:', typeof response.data);
                    console.log('Backend cart response.data length:', response.data?.length);

                    if (response.success && response.data && response.data.length > 0) {
                        // Convert backend order items to cart items format with full product info
                        const cartItemPromises = response.data.map(async (item: any): Promise<CartItem | null> => {
                            try {
                                // Get full product information
                                const productResponse = await productService.getProductById(item.productId);
                                const product = productResponse.success && productResponse.data
                                    ? productResponse.data
                                    : mockProducts.find(p => p.id === item.productId);

                                if (!product) {
                                    console.warn(`Product with ID ${item.productId} not found`);
                                    return null;
                                }

                                return {
                                    productId: item.productId,
                                    quantity: item.quantity,
                                    product: product
                                };
                            } catch (error) {
                                console.error(`Failed to load product ${item.productId}:`, error);
                                // Try to use mock data as fallback
                                const mockProduct = mockProducts.find(p => p.id === item.productId);
                                if (mockProduct) {
                                    return {
                                        productId: item.productId,
                                        quantity: item.quantity,
                                        product: mockProduct
                                    };
                                }
                                return null;
                            }
                        });

                        const cartItemsResults = await Promise.all(cartItemPromises);                        // Filter out null items and set the cart
                        const validCartItems = cartItemsResults.filter((item): item is CartItem => item !== null);
                        console.log('Loaded cart items from backend:', validCartItems);
                        setItems(validCartItems);
                    } else if (response.success && response.data && response.data.length === 0) {
                        // Backend returned empty array
                        console.log('Backend returned empty cart array');
                        setItems([]);
                    } else {
                        // If backend fails, fall back to localStorage
                        console.warn('Backend cart failed, using localStorage:', response.error);

                        // If it's a 401 error, check and clear invalid auth state
                        if (response.error && response.error.includes('401')) {
                            console.log('401 error detected in loadCart, checking token validity');
                            checkTokenValidity(); // This will clear invalid auth state
                        }

                        const localCart = cartService.getLocalCart();
                        console.log('Loaded cart from localStorage:', localCart);
                        setItems(localCart);
                    }
                } else {
                    // Token is invalid or expired, use localStorage immediately
                    console.warn('Token is invalid or expired, using localStorage immediately');
                    const localCart = cartService.getLocalCart();
                    console.log('Loaded cart from localStorage (invalid token):', localCart);
                    setItems(localCart);
                }
            } else {
                console.log('Guest user, loading from localStorage');
                // Load cart from localStorage for guests
                const localCart = cartService.getLocalCart();
                console.log('Loaded cart from localStorage:', localCart);
                setItems(localCart);
            }
        } catch (error) {
            console.error('Failed to load cart, falling back to localStorage:', error);
            // Fall back to localStorage
            const localCart = cartService.getLocalCart();
            console.log('Fallback cart from localStorage:', localCart);
            setItems(localCart);
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
        console.log('CartContext addToCart:', { productId: product.id, quantity, isAuthenticated });
        console.log('Current cart items before adding:', items);

        // Kiểm tra tồn kho trước khi thêm
        const stockCheck = await checkStock(product.id, quantity);
        if (!stockCheck.success) {
            return stockCheck;
        }

        setIsLoading(true);
        try {
            if (isAuthenticated) {
                // Check token validity before making backend call
                const tokenInfo = getCurrentTokenInfo();
                console.log('Token check for addToCart:', tokenInfo);

                // Always try localStorage first if token is invalid
                if (!tokenInfo.hasToken || !tokenInfo.isValid) {
                    console.warn('Token is invalid, adding directly to localStorage');
                    const cartItem: CartItem = {
                        productId: product.id,
                        quantity,
                        product
                    };
                    console.log('Adding to localStorage (invalid token):', cartItem);
                    cartService.addToLocalCart(cartItem);
                    const updatedCart = cartService.getLocalCart();
                    console.log('Updated local cart (invalid token):', updatedCart);
                    setItems(updatedCart);
                    console.log('Cart items after setItems (invalid token):', updatedCart);
                    return { success: true };
                }

                // Token appears valid, try backend but be ready to fallback
                console.log('Token appears valid, trying backend first');
                const orderItem = {
                    productId: product.id,
                    quantity: quantity,
                    price: product.price * quantity
                };

                console.log('Adding order item:', orderItem);
                const response = await cartService.addOrderItem(orderItem);
                console.log('Add response:', response); if (response.success) {
                    console.log('Backend add successful, calling refreshCart');

                    // Also add to localStorage for consistency
                    const cartItem: CartItem = {
                        productId: product.id,
                        quantity,
                        product
                    };
                    console.log('Also adding to localStorage for consistency:', cartItem);
                    cartService.addToLocalCart(cartItem);

                    await refreshCart();
                    // Don't log items here as state might not be updated yet
                    return { success: true };
                } else {
                    // If backend fails (like 401), fall back to localStorage WITHOUT calling refreshCart
                    console.warn('Backend add failed, falling back to localStorage:', response.error);

                    // If it's a 401 error, check and clear invalid auth state
                    if (response.error && response.error.includes('401')) {
                        console.log('401 error detected, checking token validity');
                        checkTokenValidity(); // This will clear invalid auth state
                    }

                    const cartItem: CartItem = {
                        productId: product.id,
                        quantity,
                        product
                    };
                    console.log('Adding to localStorage after backend failure:', cartItem);
                    cartService.addToLocalCart(cartItem);
                    const updatedCart = cartService.getLocalCart();
                    console.log('Updated local cart after backend failure:', updatedCart);
                    setItems(updatedCart);
                    console.log('Cart items after setItems (backend failure):', updatedCart);
                    return { success: true };
                }
            } else {
                console.log('Guest user, adding to local cart');
                // Add to local cart
                const cartItem: CartItem = {
                    productId: product.id,
                    quantity,
                    product
                };
                cartService.addToLocalCart(cartItem);
                const updatedCart = cartService.getLocalCart();
                console.log('Updated local cart for guest:', updatedCart);
                setItems(updatedCart);
                console.log('Cart items after setItems (guest):', updatedCart);
                return { success: true };
            }
        } catch (error) {
            console.error('Failed to add to cart, falling back to localStorage:', error);
            // Always fall back to localStorage on any error
            const cartItem: CartItem = {
                productId: product.id,
                quantity,
                product
            };
            cartService.addToLocalCart(cartItem);
            const updatedCart = cartService.getLocalCart();
            console.log('Updated local cart after error fallback:', updatedCart);
            setItems(updatedCart);
            console.log('Cart items after setItems (error fallback):', updatedCart);
            return { success: true };
        } finally {
            setIsLoading(false);
        }
    }; const updateQuantity = async (productId: number, quantity: number): Promise<{ success: boolean; message?: string }> => {
        console.log('CartContext updateQuantity:', { productId, quantity, isAuthenticated });

        if (quantity <= 0) {
            console.log('Quantity <= 0, removing from cart');
            await removeFromCart(productId);
            return { success: true };
        }

        // Check stock before updating quantity
        const stockCheck = await checkStock(productId, quantity);
        console.log('Stock check result:', stockCheck);
        if (!stockCheck.success) {
            return stockCheck;
        }

        setIsLoading(true);
        try {
            if (isAuthenticated) {
                console.log('Authenticated user, trying to update backend cart');
                // Find the item in backend to get its ID
                const currentItem = items.find(item => item.productId === productId);
                console.log('Current cart item:', currentItem);

                if (currentItem) {
                    // Update order item by ID in backend
                    const orderItem = {
                        productId: productId,
                        quantity: quantity,
                        price: currentItem.product.price * quantity
                    };

                    console.log('Updating order item:', orderItem);
                    const response = await cartService.updateOrderItem(currentItem.productId, orderItem);
                    console.log('Update response:', response);

                    if (response.success) {
                        await refreshCart();
                        return { success: true };
                    } else {
                        // If backend fails (like 401), fall back to localStorage
                        console.warn('Backend update failed, falling back to localStorage:', response.error);
                        const cart = cartService.getLocalCart();
                        const updatedCart = cart.map(item =>
                            item.productId === productId ? { ...item, quantity } : item
                        );
                        cartService.setLocalCart(updatedCart);
                        setItems(updatedCart);
                        return { success: true };
                    }
                } else {
                    console.log('Item not found in current cart items, updating localStorage');
                    // Fall back to localStorage if item not found in backend cart
                    const cart = cartService.getLocalCart();
                    const updatedCart = cart.map(item =>
                        item.productId === productId ? { ...item, quantity } : item
                    );
                    cartService.setLocalCart(updatedCart);
                    setItems(updatedCart);
                    return { success: true };
                }
            } else {
                console.log('Guest user, updating local cart');
                const cart = cartService.getLocalCart();
                const updatedCart = cart.map(item =>
                    item.productId === productId ? { ...item, quantity } : item
                );
                cartService.setLocalCart(updatedCart);
                setItems(updatedCart);
                return { success: true };
            }
        } catch (error) {
            console.error('Failed to update cart, falling back to localStorage:', error);
            // Always fall back to localStorage on any error
            const cart = cartService.getLocalCart();
            const updatedCart = cart.map(item =>
                item.productId === productId ? { ...item, quantity } : item
            );
            cartService.setLocalCart(updatedCart);
            setItems(updatedCart);
            return { success: true };
        } finally {
            setIsLoading(false);
        }
    }; const removeFromCart = async (productId: number) => {
        console.log('CartContext removeFromCart:', { productId, isAuthenticated });

        setIsLoading(true);
        try {
            if (isAuthenticated) {
                console.log('Authenticated user, trying to remove from backend cart');
                // Find the item in backend to get its ID
                const currentItem = items.find(item => item.productId === productId);
                console.log('Current cart item to remove:', currentItem);

                if (currentItem) {
                    // Delete order item by ID in backend
                    console.log('Calling deleteOrderItem with productId:', currentItem.productId);
                    const response = await cartService.deleteOrderItem(currentItem.productId);
                    console.log('Delete response:', response);

                    if (response.success) {
                        await refreshCart();
                    } else {
                        console.warn('Backend delete failed, falling back to localStorage:', response.error);
                        // Fall back to localStorage
                        cartService.removeFromLocalCart(productId);
                        setItems(cartService.getLocalCart());
                    }
                } else {
                    console.log('Item not found in current cart items, removing from localStorage');
                    // Fall back to localStorage if item not found in backend cart
                    cartService.removeFromLocalCart(productId);
                    setItems(cartService.getLocalCart());
                }
            } else {
                console.log('Guest user, removing from local cart');
                cartService.removeFromLocalCart(productId);
                setItems(cartService.getLocalCart());
            }
        } catch (error) {
            console.error('Failed to remove from cart, falling back to localStorage:', error);
            // Always fall back to localStorage on any error
            cartService.removeFromLocalCart(productId);
            setItems(cartService.getLocalCart());
        } finally {
            setIsLoading(false);
        }
    }; const clearCart = async () => {
        console.log('CartContext clearCart:', { isAuthenticated });
        setIsLoading(true);
        try {
            if (isAuthenticated) {
                console.log('Authenticated user, trying to clear backend cart');
                // Clear backend cart
                const response = await cartService.clearCart();
                console.log('Clear cart response:', response);

                if (response.success) {
                    // Always clear local cart too for consistency
                    cartService.clearLocalCart();
                    setItems([]);
                    console.log('✅ Backend and local cart cleared successfully');
                } else {
                    console.warn('Backend clear failed, clearing localStorage:', response.error);
                    // Fall back to localStorage if backend fails
                    cartService.clearLocalCart();
                    setItems([]);
                }
            } else {
                console.log('Guest user, clearing local cart');
                // Clear local cart
                cartService.clearLocalCart();
                setItems([]);
                console.log('✅ Local cart cleared successfully');
            }
        } catch (error) {
            console.error('Failed to clear cart, falling back to localStorage:', error);
            // Always fall back to localStorage on any error
            cartService.clearLocalCart();
            setItems([]);
        } finally {
            setIsLoading(false);
        }
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
    }; const refreshCart = async () => {
        console.log('refreshCart called');
        await loadCart();
        console.log('refreshCart completed');
    };

    const totalItems = items.reduce((sum, item) => sum + item.quantity, 0);
    const totalPrice = items.reduce((sum, item) => sum + (item.product.price * item.quantity), 0);

    const value: CartContextType = {
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
        mergeLocalCartToBackend,
    };

    return (
        <CartContext.Provider value={value}>
            {children}
        </CartContext.Provider>
    );
};
