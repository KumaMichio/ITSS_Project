import React, { useState } from "react";
import { useCart } from "../contexts/CartContext";
import type { Product } from "../types";

interface ProductCardProps {
    product: Product;
    stars?: number;
}

const ProductCard: React.FC<ProductCardProps> = ({ product, stars = 5 }) => {
    const { addToCart, isLoading } = useCart();
    const [error, setError] = useState<string>("");
    const [success, setSuccess] = useState<boolean>(false);

    // Add null check for product
    if (!product) {
        return (
            <div className="flex-1 min-w-[250px] max-w-[23%] bg-white rounded-2xl border border-[#cce7d0] shadow p-3 transition hover:shadow-lg flex flex-col items-center">
                <div className="w-full h-72 bg-gray-200 rounded-xl mb-2 flex items-center justify-center">
                    <span className="text-gray-500">No product data</span>
                </div>
            </div>
        );
    } const handleAddToCart = async () => {
        setError(""); // Clear previous error
        try {
            const result = await addToCart(product, 1);
            if (result.success) {
                setSuccess(true);
                setTimeout(() => setSuccess(false), 2000); // Hide success message after 2 seconds
                console.log(`Added ${product.title} to cart`);
            } else {
                setError(result.message || 'Không thể thêm sản phẩm vào giỏ hàng');
                setTimeout(() => setError(""), 5000); // Hide error after 5 seconds
            }
        } catch (error) {
            console.error('Failed to add to cart:', error);
            setError('Lỗi khi thêm sản phẩm vào giỏ hàng');
            setTimeout(() => setError(""), 5000);
        }
    };

    const formatPrice = (price: number) => {
        return new Intl.NumberFormat('vi-VN', {
            style: 'currency',
            currency: 'VND'
        }).format(price);
    }; const getImageUrl = (imageURL: string) => {
        // If imageURL is a full URL, use it as is
        if (imageURL && imageURL.startsWith('http')) {
            return imageURL;
        }
        // Otherwise, assume it's a relative path and prepend with assets
        return imageURL ? `/src/img/products/${imageURL}` : '/default-product.svg';
    }; return (
        <div className="flex-1 min-w-[250px] max-w-[23%] bg-white rounded-2xl border border-[#cce7d0] shadow p-3 transition hover:shadow-lg flex flex-col items-center relative">
            {/* Error Message */}
            {error && (
                <div className="absolute top-2 left-2 right-2 bg-red-100 border border-red-400 text-red-700 px-2 py-1 rounded text-xs z-10">
                    <i className="fas fa-exclamation-triangle mr-1"></i>
                    {error}
                </div>
            )}

            {/* Success Message */}
            {success && (
                <div className="absolute top-2 left-2 right-2 bg-green-100 border border-green-400 text-green-700 px-2 py-1 rounded text-xs z-10">
                    <i className="fas fa-check-circle mr-1"></i>
                    Đã thêm vào giỏ hàng!
                </div>
            )}

            <img
                src={getImageUrl(product.imageURL)}
                alt={product.title}
                className="w-full h-72 object-cover rounded-xl mb-2" onError={(e) => {
                    // Fallback to a default image if the product image fails to load
                    (e.target as HTMLImageElement).src = '/default-product.svg';
                }}
            />
            <div className="text-center py-2">
                <span className="text-xs text-gray-500">{product.category}</span>
                <h5 className="pt-2 text-base font-semibold text-[#1a1a1a] line-clamp-2" title={product.title}>
                    {product.title}
                </h5>
                <div className="flex justify-center gap-1 mt-1">
                    {[...Array(5)].map((_, i) => (
                        <i key={i} className={`fas fa-star text-xs ${i < stars ? 'text-yellow-400' : 'text-gray-300'}`}></i>
                    ))}
                </div>                <h4 className="pt-2 text-lg font-bold text-[#088178]">{formatPrice(product.price)}</h4>
                {product.quantity > 0 ? (
                    product.quantity <= 5 ? (
                        <p className="text-xs text-orange-600 font-semibold">
                            ⚠️ Chỉ còn {product.quantity} sản phẩm
                        </p>
                    ) : (
                        <p className="text-xs text-green-600">Còn {product.quantity} sản phẩm</p>
                    )
                ) : (
                    <p className="text-xs text-red-600 font-semibold">❌ Hết hàng</p>
                )}
            </div>            <button
                onClick={handleAddToCart}
                disabled={isLoading || product.quantity === 0}
                className="mt-2 hover:text-[#066e6a] transition disabled:opacity-50 disabled:cursor-not-allowed"
                title={product.quantity === 0 ? "Sản phẩm đã hết hàng" : `Thêm vào giỏ hàng (còn ${product.quantity} sản phẩm)`}
            >
                {isLoading ? (
                    <i className="fas fa-spinner fa-spin text-xl text-[#088178]"></i>
                ) : (
                    <i className="fa-solid fa-cart-plus cart text-xl text-[#088178]"></i>
                )}
            </button>
        </div>
    );
};

export default ProductCard;
