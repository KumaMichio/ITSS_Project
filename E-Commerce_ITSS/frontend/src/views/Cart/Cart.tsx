import { useNavigate } from "react-router-dom";
import { useState } from "react";
import Header from "../../components/Header";
import Loading from "../../components/Loading";
import { useCart } from "../../contexts/CartContext";

export default function Cart() {
    const navigate = useNavigate();
    const { items, totalPrice, updateQuantity, removeFromCart, isLoading, validateCartStock } = useCart();
    const [errorMessage, setErrorMessage] = useState<string>("");
    const [checkingStock, setCheckingStock] = useState<boolean>(false);

    const formatPrice = (price: number) => {
        return new Intl.NumberFormat('vi-VN', {
            style: 'currency',
            currency: 'VND'
        }).format(price);
    };

    const getImageUrl = (imageURL: string | null) => {
        if (imageURL && imageURL.startsWith('http')) {
            return imageURL;
        }
        return imageURL ? `/src/img/products/${imageURL}` : '/default-product.svg';
    }; const handleQuantityChange = async (productId: number, newQuantity: number) => {
        if (newQuantity >= 0) {
            setErrorMessage(""); // Clear previous error
            const result = await updateQuantity(productId, newQuantity);
            if (!result.success && result.message) {
                setErrorMessage(result.message);
                // Auto-hide error after 5 seconds
                setTimeout(() => setErrorMessage(""), 5000);
            }
        }
    }; const handleRemoveItem = async (productId: number) => {
        await removeFromCart(productId);
    };

    const handleCheckout = async () => {
        setCheckingStock(true);
        setErrorMessage("");

        try {
            const validation = await validateCartStock();

            if (validation.success) {
                navigate('/checkout');
            } else {
                // Display detailed error messages for invalid items
                if (validation.invalidItems && validation.invalidItems.length > 0) {
                    const errorText = validation.invalidItems.join('\n');
                    setErrorMessage(`${validation.message}:\n${errorText}`);
                } else {
                    setErrorMessage(validation.message || 'Có lỗi xảy ra khi kiểm tra số lượng sản phẩm');
                }

                // Auto-hide error after 10 seconds for stock validation errors
                setTimeout(() => setErrorMessage(""), 10000);
            }
        } catch (error) {
            console.error('Error during checkout validation:', error);
            setErrorMessage('Có lỗi xảy ra khi kiểm tra số lượng sản phẩm');
            setTimeout(() => setErrorMessage(""), 5000);
        } finally {
            setCheckingStock(false);
        }
    };

    if (isLoading) {
        return (
            <>
                <Header />
                <Loading message="Đang tải giỏ hàng..." />
            </>
        );
    }

    return (<>
        <Header />
        <section className="px-20 py-10 bg-white font-sans min-h-screen">            {/* Error Message */}
            {errorMessage && (
                <div className="mb-4 p-4 bg-red-100 border border-red-400 text-red-700 rounded-lg flex items-start justify-between">
                    <div className="flex items-start">
                        <i className="fas fa-exclamation-triangle mr-2 mt-1"></i>
                        <span className="whitespace-pre-line">{errorMessage}</span>
                    </div>
                    <button
                        onClick={() => setErrorMessage("")}
                        className="text-red-500 hover:text-red-700 ml-2 flex-shrink-0"
                    >
                        <i className="fas fa-times"></i>
                    </button>
                </div>
            )}

            {items.length === 0 ? (
                <div className="text-center py-20">
                    <i className="fas fa-shopping-cart text-6xl text-gray-300 mb-4"></i>
                    <h2 className="text-2xl font-bold text-gray-700 mb-4">Giỏ hàng của bạn đang trống</h2>
                    <p className="text-gray-500 mb-6">Hãy thêm một số sản phẩm vào giỏ hàng để tiếp tục mua sắm</p>
                    <button
                        onClick={() => navigate('/shop')}
                        className="bg-[#088178] text-white px-6 py-3 rounded-lg hover:bg-[#066e6a] transition"
                    >
                        Tiếp tục mua sắm
                    </button>
                </div>
            ) : (
                <div className="space-y-8">
                    <div className="overflow-x-auto">
                        <table className="w-full border-collapse">
                            <thead>
                                <tr className="border-b-2 border-gray-200">
                                    <th className="font-bold uppercase text-xs py-4 text-center">Xóa</th>
                                    <th className="font-bold uppercase text-xs py-4 text-center">Hình ảnh</th>
                                    <th className="font-bold uppercase text-xs py-4 text-center">Sản phẩm</th>
                                    <th className="font-bold uppercase text-xs py-4 text-center">Giá</th>
                                    <th className="font-bold uppercase text-xs py-4 text-center">Số lượng</th>
                                    <th className="font-bold uppercase text-xs py-4 text-center">Tổng cộng</th>
                                </tr>
                            </thead>
                            <tbody>
                                {items.map((item) => (
                                    <tr key={item.productId} className="border-b border-gray-100 hover:bg-gray-50">
                                        <td className="text-center py-4">
                                            <button
                                                onClick={() => handleRemoveItem(item.productId)}
                                                className="text-red-500 hover:text-red-700 transition text-xl"
                                                title="Xóa sản phẩm"
                                            >
                                                <i className="far fa-times-circle"></i>
                                            </button>
                                        </td>
                                        <td className="text-center py-4">
                                            <img
                                                src={getImageUrl(item.product.imageURL)}
                                                alt={item.product.title}
                                                className="w-20 h-20 object-cover rounded mx-auto shadow-sm"
                                                onError={(e) => {
                                                    (e.target as HTMLImageElement).src = '/default-product.svg';
                                                }}
                                            />
                                        </td>                                        <td className="text-center py-4">
                                            <div>
                                                <h4 className="font-semibold text-gray-800 mb-1">{item.product.title}</h4>
                                                <p className="text-sm text-gray-500 capitalize">{item.product.category}</p>
                                                {/* Stock Warning */}
                                                {item.product.quantity <= 5 && (
                                                    <p className="text-xs text-orange-600 font-semibold mt-1">
                                                        ⚠️ Chỉ còn {item.product.quantity} sản phẩm
                                                    </p>
                                                )}
                                                {item.quantity > item.product.quantity && (
                                                    <p className="text-xs text-red-600 font-semibold mt-1">
                                                        ❌ Không đủ hàng (có {item.product.quantity})
                                                    </p>
                                                )}
                                            </div>
                                        </td>
                                        <td className="text-center py-4 font-semibold text-gray-700">
                                            {formatPrice(item.product.price)}
                                        </td>
                                        <td className="text-center py-4">
                                            <div className="flex items-center justify-center space-x-2">                                                    <button
                                                onClick={() => handleQuantityChange(item.productId, item.quantity - 1)}
                                                className="w-8 h-8 bg-gray-200 rounded-full flex items-center justify-center hover:bg-gray-300 transition disabled:opacity-50 disabled:cursor-not-allowed"
                                                disabled={item.quantity <= 1 || isLoading}
                                                title="Giảm số lượng"
                                            >
                                                <i className="fas fa-minus text-xs"></i>
                                            </button>
                                                <span className="w-12 text-center font-semibold text-gray-800">{item.quantity}</span>                                                    <button
                                                    onClick={() => handleQuantityChange(item.productId, item.quantity + 1)}
                                                    className="w-8 h-8 bg-gray-200 rounded-full flex items-center justify-center hover:bg-gray-300 transition disabled:opacity-50 disabled:cursor-not-allowed"
                                                    disabled={isLoading || item.quantity >= item.product.quantity}
                                                    title={item.quantity >= item.product.quantity ? `Không đủ hàng (tối đa ${item.product.quantity})` : "Tăng số lượng"}
                                                >
                                                    <i className="fas fa-plus text-xs"></i>
                                                </button>
                                            </div>
                                        </td>
                                        <td className="text-center py-4 font-bold text-[#088178]">
                                            {formatPrice(item.product.price * item.quantity)}
                                        </td>
                                    </tr>
                                ))}
                            </tbody>
                        </table>
                    </div>

                    <div className="flex flex-col lg:flex-row justify-between items-center space-y-6 lg:space-y-0">
                        <button
                            onClick={() => navigate('/shop')}
                            className="bg-gray-500 text-white px-8 py-3 rounded-lg hover:bg-gray-600 transition font-medium"
                        >
                            <i className="fas fa-arrow-left mr-2"></i>
                            Tiếp tục mua sắm
                        </button>

                        <div className="bg-gray-50 p-6 rounded-lg shadow-sm border">
                            <div className="space-y-3 mb-4">
                                <div className="flex justify-between items-center text-gray-600">
                                    <span>Tổng tiền hàng:</span>
                                    <span className="font-semibold">{formatPrice(totalPrice)}</span>
                                </div>
                                <div className="flex justify-between items-center text-gray-600">
                                    <span>Phí vận chuyển:</span>
                                    <span className="font-semibold">Miễn phí</span>
                                </div>
                                <hr className="border-gray-300" />
                                <div className="flex justify-between items-center text-xl font-bold text-[#088178]">
                                    <span>Tổng cộng:</span>
                                    <span>{formatPrice(totalPrice)}</span>
                                </div>
                            </div>                            <button
                                onClick={handleCheckout}
                                disabled={checkingStock || isLoading}
                                className="w-full bg-[#088178] text-white px-8 py-3 rounded-lg hover:bg-[#066e6a] transition text-lg font-semibold disabled:bg-gray-400 disabled:cursor-not-allowed"
                            >
                                {checkingStock ? (
                                    <>
                                        <i className="fas fa-spinner fa-spin mr-2"></i>
                                        Đang kiểm tra...
                                    </>
                                ) : (
                                    <>
                                        <i className="fas fa-credit-card mr-2"></i>
                                        Thanh toán
                                    </>
                                )}
                            </button>
                        </div>
                    </div>
                </div>
            )}
        </section>
    </>
    );
}

