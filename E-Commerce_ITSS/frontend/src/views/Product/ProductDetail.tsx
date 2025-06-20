import { useState, useEffect } from "react";
import { useParams, useNavigate } from "react-router-dom";
import Header from "../../components/Header";
import Loading from "../../components/Loading";
import { useCart } from "../../contexts/CartContext";
import { productService } from "../../services/product.service";
import type { Product } from "../../types";

const ProductDetail = () => {
    const { id } = useParams<{ id: string }>();
    const navigate = useNavigate();
    const { addToCart, isLoading: cartLoading } = useCart();

    const [product, setProduct] = useState<Product | null>(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string>("");
    const [quantity, setQuantity] = useState(1);
    const [addToCartError, setAddToCartError] = useState<string>("");
    const [addToCartSuccess, setAddToCartSuccess] = useState(false);

    useEffect(() => {
        if (id) {
            loadProduct(parseInt(id));
        }
    }, [id]);

    const loadProduct = async (productId: number) => {
        setLoading(true);
        setError("");
        try {
            const response = await productService.getProductById(productId);
            if (response.success && response.data) {
                setProduct(response.data);
            } else {
                setError(response.error || "Không thể tải thông tin sản phẩm");
            }
        } catch (error) {
            console.error('Error loading product:', error);
            setError("Lỗi khi tải thông tin sản phẩm");
        } finally {
            setLoading(false);
        }
    };

    const formatPrice = (price: number) => {
        return new Intl.NumberFormat('vi-VN', {
            style: 'currency',
            currency: 'VND'
        }).format(price);
    };

    const getImageUrl = (imageURL: string) => {
        if (imageURL && imageURL.startsWith('http')) {
            return imageURL;
        }
        return imageURL ? `/src/img/products/${imageURL}` : '/default-product.svg';
    };

    const handleQuantityChange = (newQuantity: number) => {
        if (newQuantity >= 1 && newQuantity <= (product?.quantity || 0)) {
            setQuantity(newQuantity);
        }
    };

    const handleAddToCart = async () => {
        if (!product) return;

        setAddToCartError("");
        setAddToCartSuccess(false);

        try {
            const result = await addToCart(product, quantity);
            if (result.success) {
                setAddToCartSuccess(true);
                setTimeout(() => setAddToCartSuccess(false), 3000);
            } else {
                setAddToCartError(result.message || 'Không thể thêm sản phẩm vào giỏ hàng');
                setTimeout(() => setAddToCartError(""), 5000);
            }
        } catch (error) {
            console.error('Failed to add to cart:', error);
            setAddToCartError('Lỗi khi thêm sản phẩm vào giỏ hàng');
            setTimeout(() => setAddToCartError(""), 5000);
        }
    };

    if (loading) {
        return (
            <>
                <Header />
                <Loading message="Đang tải thông tin sản phẩm..." />
            </>
        );
    }

    if (error || !product) {
        return (
            <>
                <Header />
                <div className="flex justify-center items-center min-h-[400px]">
                    <div className="text-center">
                        <p className="text-red-500 mb-4">{error || "Không tìm thấy sản phẩm"}</p>
                        <button
                            onClick={() => navigate('/shop')}
                            className="bg-[#088178] text-white px-4 py-2 rounded hover:bg-[#066e6a] transition"
                        >
                            Quay lại cửa hàng
                        </button>
                    </div>
                </div>
            </>
        );
    }

    return (
        <>
            <Header />
            <section className="px-20 py-10">
                <div className="grid grid-cols-1 lg:grid-cols-2 gap-10">
                    {/* Product Image */}
                    <div className="flex justify-center">
                        <img
                            src={getImageUrl(product.imageURL)}
                            alt={product.title}
                            className="w-full max-w-md h-auto object-cover rounded-xl shadow-lg"
                            onError={(e) => {
                                (e.target as HTMLImageElement).src = '/default-product.svg';
                            }}
                        />
                    </div>

                    {/* Product Info */}
                    <div className="space-y-6">
                        <div>
                            <span className="text-sm text-gray-500 uppercase">{product.category}</span>
                            <h1 className="text-3xl font-bold text-gray-800 mt-2">{product.title}</h1>
                        </div>

                        <div className="flex items-center gap-2">
                            <div className="flex gap-1">
                                {[...Array(5)].map((_, i) => (
                                    <i key={i} className={`fas fa-star text-sm ${i < 4 ? 'text-yellow-400' : 'text-gray-300'}`}></i>
                                ))}
                            </div>
                            <span className="text-sm text-gray-500">(4.0/5)</span>
                        </div>

                        <div className="text-3xl font-bold text-[#088178]">
                            {formatPrice(product.price)}
                        </div>

                        {/* Stock Status */}
                        <div>
                            {product.quantity > 0 ? (
                                product.quantity <= 5 ? (
                                    <p className="text-orange-600 font-semibold">
                                        ⚠️ Chỉ còn {product.quantity} sản phẩm
                                    </p>
                                ) : (
                                    <p className="text-green-600">✅ Còn {product.quantity} sản phẩm</p>
                                )
                            ) : (
                                <p className="text-red-600 font-semibold">❌ Hết hàng</p>
                            )}
                        </div>

                        {/* Quantity Selector */}
                        {product.quantity > 0 && (
                            <div className="space-y-4">
                                <div className="flex items-center gap-4">
                                    <label className="font-semibold">Số lượng:</label>
                                    <div className="flex items-center gap-2">
                                        <button
                                            onClick={() => handleQuantityChange(quantity - 1)}
                                            className="w-10 h-10 bg-gray-200 rounded-full flex items-center justify-center hover:bg-gray-300 transition disabled:opacity-50"
                                            disabled={quantity <= 1}
                                        >
                                            <i className="fas fa-minus"></i>
                                        </button>
                                        <span className="w-16 text-center font-semibold text-lg">{quantity}</span>
                                        <button
                                            onClick={() => handleQuantityChange(quantity + 1)}
                                            className="w-10 h-10 bg-gray-200 rounded-full flex items-center justify-center hover:bg-gray-300 transition disabled:opacity-50"
                                            disabled={quantity >= product.quantity}
                                        >
                                            <i className="fas fa-plus"></i>
                                        </button>
                                    </div>
                                </div>

                                {/* Add to Cart Button */}
                                <button
                                    onClick={handleAddToCart}
                                    disabled={cartLoading}
                                    className="w-full bg-[#088178] text-white py-3 px-6 rounded-lg hover:bg-[#066e6a] transition text-lg font-semibold disabled:bg-gray-400 disabled:cursor-not-allowed"
                                >
                                    {cartLoading ? (
                                        <>
                                            <i className="fas fa-spinner fa-spin mr-2"></i>
                                            Đang thêm...
                                        </>
                                    ) : (
                                        <>
                                            <i className="fas fa-cart-plus mr-2"></i>
                                            Thêm vào giỏ hàng
                                        </>
                                    )}
                                </button>
                            </div>
                        )}

                        {/* Error/Success Messages */}
                        {addToCartError && (
                            <div className="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded flex items-center">
                                <i className="fas fa-exclamation-triangle mr-2"></i>
                                {addToCartError}
                            </div>
                        )}

                        {addToCartSuccess && (
                            <div className="bg-green-100 border border-green-400 text-green-700 px-4 py-3 rounded flex items-center">
                                <i className="fas fa-check-circle mr-2"></i>
                                Đã thêm sản phẩm vào giỏ hàng!
                            </div>
                        )}

                        {/* Product Details */}
                        <div className="space-y-3 border-t pt-6">
                            <h3 className="text-xl font-semibold">Thông tin sản phẩm</h3>
                            {product.dimension && (
                                <div className="flex justify-between">
                                    <span className="text-gray-600">Kích thước:</span>
                                    <span className="font-medium">{product.dimension} cm</span>
                                </div>
                            )}
                            {product.weight && (
                                <div className="flex justify-between">
                                    <span className="text-gray-600">Trọng lượng:</span>
                                    <span className="font-medium">{product.weight} kg</span>
                                </div>
                            )}
                            <div className="flex justify-between">
                                <span className="text-gray-600">Danh mục:</span>
                                <span className="font-medium capitalize">{product.category}</span>
                            </div>
                        </div>

                        {/* Action Buttons */}
                        <div className="flex gap-4 pt-4">
                            <button
                                onClick={() => navigate('/shop')}
                                className="flex-1 bg-gray-500 text-white py-3 px-6 rounded-lg hover:bg-gray-600 transition"
                            >
                                <i className="fas fa-arrow-left mr-2"></i>
                                Tiếp tục mua sắm
                            </button>
                            <button
                                onClick={() => navigate('/cart')}
                                className="flex-1 bg-[#088178] text-white py-3 px-6 rounded-lg hover:bg-[#066e6a] transition"
                            >
                                <i className="fas fa-shopping-cart mr-2"></i>
                                Xem giỏ hàng
                            </button>
                        </div>
                    </div>
                </div>
            </section>
        </>
    );
};

export default ProductDetail;
