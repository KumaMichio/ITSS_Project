import { useState, useEffect } from "react";
import Header from "../../components/Header";
import Loading from "../../components/Loading";
import Toast from "../../components/Toast";
import { useNavigate } from "react-router-dom";
import { useCart } from "../../contexts/CartContext";
import { paymentService } from "../../services/payment.service";
import { guestOrderService } from "../../services/guest-order.service";

// Import images
import vnpayQR from "../../img/pay/a886a84657dce482bdcd.jpg";

const Checkout = () => {
    const navigate = useNavigate();
    const { items, totalPrice, isLoading } = useCart();
    const [showModal, setShowModal] = useState(false);
    const [shippingMethod, setShippingMethod] = useState<'cod' | 'rush'>('cod');
    const [productValidation, setProductValidation] = useState<{
        isValid: boolean;
        errors: string[];
        isChecking: boolean;
    }>({
        isValid: true,
        errors: [],
        isChecking: false
    }); const [orderForm, setOrderForm] = useState({
        fullname: '',
        email: '',
        phone: '',
        address: '',
        province: ''
    });
    const [shippingOptions, setShippingOptions] = useState<{
        regular?: { regularShippingFee: number };
        express?: { expressShippingFee: number; expressAvailable: boolean };
    }>({});
    const [isCalculatingShipping, setIsCalculatingShipping] = useState(false);
    const [vnpayLoading, setVnpayLoading] = useState(false);
    const [toast, setToast] = useState<{
        message: string;
        type: 'success' | 'error' | 'warning' | 'info';
    } | null>(null);

    useEffect(() => {
        if (!isLoading && items.length === 0) {
            navigate('/shop');
        }
    }, [isLoading, items, navigate]);    // Kiểm tra tính hợp lệ của sản phẩm trước khi thanh toán
    const validateProducts = async () => {
        setProductValidation(prev => ({ ...prev, isChecking: true }));

        try {
            const cartItems = items.map(item => ({
                productId: item.product.id,
                quantity: item.quantity
            }));

            const response = await guestOrderService.validateCartForCheckout(cartItems);

            if (response.success && response.data) {
                setProductValidation({
                    isValid: response.data.isValid,
                    errors: response.data.errors || [],
                    isChecking: false
                });
                return response.data.isValid;
            } else {
                // Fallback validation when backend is offline
                console.log('Backend validation not available, using local validation');
                setProductValidation({
                    isValid: true,
                    errors: [],
                    isChecking: false
                });
                return true;
            }
        } catch (error) {
            console.error('Stock validation error:', error);
            // Fallback validation
            setProductValidation({
                isValid: true,
                errors: [],
                isChecking: false
            });
            return true;
        }
    }; const handleFormChange = (field: string, value: string) => {
        setOrderForm(prev => ({ ...prev, [field]: value }));

        // Calculate shipping when address/province changes
        if ((field === 'address' || field === 'province') && value.trim()) {
            calculateShippingOptions();
        }
    };

    // Calculate shipping options based on address
    const calculateShippingOptions = async () => {
        if (!orderForm.address.trim() || !orderForm.province.trim() || items.length === 0) {
            return;
        }

        setIsCalculatingShipping(true);
        try {
            const cartItems = items.map(item => ({
                productId: item.product.id,
                quantity: item.quantity
            }));

            const options = await guestOrderService.calculateShippingOptions(
                cartItems,
                orderForm.province,
                orderForm.address
            );

            if (options.regular.success && options.express.success) {
                setShippingOptions({
                    regular: options.regular.data,
                    express: options.express.data
                });
            } else {
                // Fallback shipping calculation
                const baseShipping = orderForm.province.toLowerCase().includes('hà nội') ||
                    orderForm.province.toLowerCase().includes('hồ chí minh') ? 22000 : 30000;

                setShippingOptions({
                    regular: { regularShippingFee: baseShipping },
                    express: {
                        expressShippingFee: baseShipping + (items.length * 10000),
                        expressAvailable: orderForm.province.toLowerCase().includes('hà nội')
                    }
                });
            }
        } catch (error) {
            console.error('Shipping calculation error:', error);
            // Fallback calculation
            const baseShipping = 30000;
            setShippingOptions({
                regular: { regularShippingFee: baseShipping },
                express: {
                    expressShippingFee: baseShipping + 20000,
                    expressAvailable: false
                }
            });
        } finally {
            setIsCalculatingShipping(false);
        }
    }; const handleConfirmPayment = async () => {
        // Validate form
        if (!orderForm.fullname.trim() || !orderForm.email.trim() ||
            !orderForm.phone.trim() || !orderForm.address.trim() || !orderForm.province.trim()) {
            setToast({
                message: 'Vui lòng điền đầy đủ thông tin đặt hàng.',
                type: 'warning'
            });
            return;
        }

        // Validate products
        const isValidProducts = await validateProducts();
        if (!isValidProducts) {
            return;
        }

        try {            // Create guest order
            const cartItems = items.map(item => ({
                productId: item.product.id,
                quantity: item.quantity
            }));

            const deliveryInfo = {
                name: orderForm.fullname,
                email: orderForm.email,
                phone: orderForm.phone,
                address: orderForm.address,
                province: orderForm.province
            };

            const orderRequest = {
                productIds: cartItems.map(item => item.productId),
                quantities: cartItems.map(item => item.quantity),
                deliveryInfo,
                express: shippingMethod === 'rush'
            };

            const response = await guestOrderService.createGuestOrder(orderRequest);

            if (response.success && response.data) {
                setToast({
                    message: `Đặt hàng thành công! Mã đơn hàng: ${response.data.orderId}`,
                    type: 'success'
                });

                // Clear cart and redirect after successful order
                setTimeout(() => {
                    navigate('/shop');
                }, 2000);
            } else {
                throw new Error(response.error || 'Không thể tạo đơn hàng');
            }
        } catch (error) {
            console.error('Order creation error:', error);
            setToast({
                message: `Không thể tạo đơn hàng: ${error instanceof Error ? error.message : 'Lỗi không xác định'}`,
                type: 'error'
            });
        }

        setShowModal(false);
    }; const handleVNPayPayment = async () => {
        // Validate form first
        if (!orderForm.fullname.trim() || !orderForm.email.trim() ||
            !orderForm.phone.trim() || !orderForm.address.trim() || !orderForm.province.trim()) {
            setToast({
                message: 'Vui lòng điền đầy đủ thông tin đặt hàng.',
                type: 'warning'
            });
            return;
        }

        // Validate products
        const isValidProducts = await validateProducts();
        if (!isValidProducts) {
            return;
        } setVnpayLoading(true);
        try {
            console.log('Starting VNPay payment process...');

            // Check backend health first
            const backendHealthy = await paymentService.checkBackendHealth();
            console.log('Backend health status:', backendHealthy);

            let orderId = Date.now(); // Fallback order ID

            // Only try backend order creation if backend is healthy
            if (backendHealthy) {
                try {
                    const cartItems = items.map(item => ({
                        productId: item.product.id,
                        quantity: item.quantity
                    }));

                    const deliveryInfo = {
                        name: orderForm.fullname,
                        email: orderForm.email,
                        phone: orderForm.phone,
                        address: orderForm.address,
                        province: orderForm.province
                    };

                    const orderRequest = {
                        productIds: cartItems.map(item => item.productId),
                        quantities: cartItems.map(item => item.quantity),
                        deliveryInfo,
                        express: shippingMethod === 'rush'
                    };

                    console.log('Creating guest order...');
                    const orderResponse = await guestOrderService.createGuestOrder(orderRequest);

                    if (orderResponse.success && orderResponse.data) {
                        orderId = orderResponse.data.orderId;
                        console.log('Order created successfully:', orderId);
                    } else {
                        console.log('Failed to create order via backend, using fallback order ID');
                    }
                } catch (orderError) {
                    console.error('Order creation failed:', orderError);
                    console.log('Using fallback order ID:', orderId);
                }
            } else {
                console.log('Backend is offline, using fallback order ID:', orderId);
                setToast({
                    message: 'Backend offline - Sử dụng chế độ demo',
                    type: 'warning'
                });
            }            // Create VNPay payment regardless of order creation status
            setToast({
                message: 'Đang tạo thanh toán VNPay...',
                type: 'info'
            });

            // Calculate total amount including shipping
            const shippingFee = shippingMethod === 'rush'
                ? (shippingOptions.express?.expressShippingFee || 30000)
                : (shippingOptions.regular?.regularShippingFee || 15000);
            const totalAmount = totalPrice + shippingFee;

            console.log('Payment details:', { orderId, totalAmount, shippingFee, totalPrice });

            const paymentResponse = await paymentService.createVNPayPaymentForOrderWithHealth(orderId, totalAmount, backendHealthy);

            if (paymentResponse.success && paymentResponse.data) {
                // Check if this is a fallback response
                const isFallback = paymentResponse.message && paymentResponse.message.includes('fallback');

                if (isFallback) {
                    setToast({
                        message: 'Sử dụng URL thanh toán demo. Chuyển hướng...',
                        type: 'warning'
                    });
                } else {
                    setToast({
                        message: 'Đang chuyển hướng đến VNPay...',
                        type: 'success'
                    });
                }

                // Store order info in localStorage for recovery
                localStorage.setItem('pendingOrder', JSON.stringify({
                    orderId,
                    items: items,
                    deliveryInfo: orderForm,
                    timestamp: Date.now(),
                    isFallback: isFallback || !backendHealthy
                }));                // Extract payment URL from response
                console.log('Payment response for URL extraction:', paymentResponse);
                console.log('Payment response.data:', paymentResponse.data);
                console.log('Payment response.data as any:', (paymentResponse.data as any)?.data);

                let paymentUrl = null;
                // Check nested data structure first (backend response format)
                if (paymentResponse.data && (paymentResponse.data as any).data && (paymentResponse.data as any).data.paymentUrl) {
                    paymentUrl = (paymentResponse.data as any).data.paymentUrl;
                } else if (paymentResponse.data && paymentResponse.data.paymentUrl) {
                    // Fallback for direct response structure
                    paymentUrl = paymentResponse.data.paymentUrl;
                }

                console.log('Extracted payment URL:', paymentUrl);

                if (paymentUrl) {
                    setTimeout(() => {
                        paymentService.redirectToVNPay(paymentUrl);
                    }, 1500);
                } else {
                    throw new Error('Không nhận được URL thanh toán');
                }
            } else {
                throw new Error(paymentResponse.error || 'Không thể tạo thanh toán VNPay');
            }
        } catch (error) {
            console.error('VNPay payment error:', error);

            let errorMessage = 'Có lỗi xảy ra khi tạo thanh toán VNPay.';
            let showFallbackOptions = false;

            if (error instanceof TypeError && error.message.includes('Failed to fetch')) {
                errorMessage = 'Không thể kết nối đến server thanh toán. Đang thử phương thức dự phòng...';
                showFallbackOptions = true;
            } else if (error instanceof Error) {
                if (error.message.includes('CORS') || error.message.includes('Network')) {
                    errorMessage = 'Lỗi kết nối mạng. Đang thử phương thức dự phòng...';
                    showFallbackOptions = true;
                } else {
                    errorMessage = `Lỗi: ${error.message}`;
                }
            }

            if (showFallbackOptions) {
                // Try fallback payment URL generation
                try {
                    console.log('Trying fallback payment method...');
                    const fallbackResponse = await paymentService.createVNPayPayment(); if (fallbackResponse.success && fallbackResponse.data && fallbackResponse.data.paymentUrl) {
                        setToast({
                            message: 'Đang sử dụng phương thức thanh toán dự phòng...',
                            type: 'warning'
                        });

                        setTimeout(() => {
                            paymentService.redirectToVNPay(fallbackResponse.data!.paymentUrl);
                        }, 2000);
                        return;
                    }
                } catch (fallbackError) {
                    console.error('Fallback payment also failed:', fallbackError);
                }

                // Show QR code option as last resort
                setToast({
                    message: 'Lỗi kết nối server. Bạn có thể thanh toán bằng QR code bên dưới.',
                    type: 'warning'
                });
            } else {
                setToast({
                    message: errorMessage,
                    type: 'error'
                });
            }
        } finally {
            setVnpayLoading(false);
        }
    }; const getShippingFee = () => {
        if (shippingMethod === 'rush') {
            return shippingOptions.express?.expressShippingFee || 30000;
        } else {
            return shippingOptions.regular?.regularShippingFee || 22000;
        }
    };

    const shippingFee = getShippingFee();
    const finalAmount = totalPrice + shippingFee;
    const formatPrice = (price: number) =>
        new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(price);

    if (isLoading) {
        return (
            <>
                <Header />
                <Loading message="Đang tải giỏ hàng..." />
            </>
        );
    }

    return (
        <>
            <Header />
            <div className="max-w-6xl mx-auto p-8 mt-10">
                <h2 className="text-3xl font-bold text-center mb-10">
                    Payment Details
                </h2>

                <div className="grid grid-cols-1 lg:grid-cols-2 gap-8">
                    {/* Left Column - Form */}
                    <div className="bg-white p-8 rounded-lg shadow-lg">
                        {/* Hiển thị lỗi validation sản phẩm */}
                        {!productValidation.isValid && (
                            <div className="mb-6 p-4 bg-red-50 border border-red-200 rounded-lg">
                                <h3 className="font-bold text-red-800 mb-2">Có vấn đề với đơn hàng:</h3>
                                <ul className="list-disc list-inside space-y-1">
                                    {productValidation.errors.map((error, index) => (
                                        <li key={index} className="text-red-700 text-sm">{error}</li>
                                    ))}
                                </ul>
                                <button
                                    onClick={() => navigate('/cart')}
                                    className="mt-3 text-sm bg-red-600 text-white px-4 py-2 rounded hover:bg-red-700"
                                >
                                    Quay lại giỏ hàng
                                </button>
                            </div>
                        )}

                        <form>
                            <div className="mb-5">
                                <label htmlFor="fullname" className="block mb-2 font-bold">
                                    Full Name
                                </label>
                                <input
                                    type="text"
                                    id="fullname"
                                    required
                                    value={orderForm.fullname}
                                    onChange={(e) => handleFormChange('fullname', e.target.value)}
                                    className="w-full p-3 border border-gray-300 rounded"
                                />
                            </div>                            <div className="mb-5">
                                <label htmlFor="email" className="block mb-2 font-bold">
                                    Email Address
                                </label>
                                <input
                                    type="email"
                                    id="email"
                                    required
                                    value={orderForm.email}
                                    onChange={(e) => handleFormChange('email', e.target.value)}
                                    className="w-full p-3 border border-gray-300 rounded"
                                />
                            </div>
                            <div className="mb-5">
                                <label htmlFor="phone" className="block mb-2 font-bold">
                                    Phone Number
                                </label>
                                <input
                                    type="tel"
                                    id="phone"
                                    required
                                    value={orderForm.phone}
                                    onChange={(e) => handleFormChange('phone', e.target.value)}
                                    className="w-full p-3 border border-gray-300 rounded"
                                    placeholder="Nhập số điện thoại"
                                />
                            </div>
                            <div className="mb-5">
                                <label htmlFor="province" className="block mb-2 font-bold">
                                    Province/City
                                </label>
                                <select
                                    id="province"
                                    required
                                    value={orderForm.province}
                                    onChange={(e) => handleFormChange('province', e.target.value)}
                                    className="w-full p-3 border border-gray-300 rounded"
                                >
                                    <option value="">Chọn tỉnh/thành phố</option>
                                    <option value="Hà Nội">Hà Nội</option>
                                    <option value="Hồ Chí Minh">Hồ Chí Minh</option>
                                    <option value="Đà Nẵng">Đà Nẵng</option>
                                    <option value="Cần Thơ">Cần Thơ</option>
                                    <option value="An Giang">An Giang</option>
                                    <option value="Bà Rịa - Vũng Tàu">Bà Rịa - Vũng Tàu</option>
                                    <option value="Bắc Giang">Bắc Giang</option>
                                    <option value="Bắc Kạn">Bắc Kạn</option>
                                    <option value="Bạc Liêu">Bạc Liêu</option>
                                    <option value="Bắc Ninh">Bắc Ninh</option>
                                    <option value="Bến Tre">Bến Tre</option>
                                    <option value="Bình Định">Bình Định</option>
                                    <option value="Bình Dương">Bình Dương</option>
                                    <option value="Bình Phước">Bình Phước</option>
                                    <option value="Bình Thuận">Bình Thuận</option>
                                    <option value="Cà Mau">Cà Mau</option>
                                    <option value="Cao Bằng">Cao Bằng</option>
                                    <option value="Đắk Lắk">Đắk Lắk</option>
                                    <option value="Đắk Nông">Đắk Nông</option>
                                    <option value="Điện Biên">Điện Biên</option>
                                    <option value="Đồng Nai">Đồng Nai</option>
                                    <option value="Đồng Tháp">Đồng Tháp</option>
                                    <option value="Gia Lai">Gia Lai</option>
                                    <option value="Hà Giang">Hà Giang</option>
                                    <option value="Hà Nam">Hà Nam</option>
                                    <option value="Hà Tĩnh">Hà Tĩnh</option>
                                    <option value="Hải Dương">Hải Dương</option>
                                    <option value="Hải Phòng">Hải Phòng</option>
                                    <option value="Hậu Giang">Hậu Giang</option>
                                    <option value="Hòa Bình">Hòa Bình</option>
                                    <option value="Hưng Yên">Hưng Yên</option>
                                    <option value="Khánh Hòa">Khánh Hòa</option>
                                    <option value="Kiên Giang">Kiên Giang</option>
                                    <option value="Kon Tum">Kon Tum</option>
                                    <option value="Lai Châu">Lai Châu</option>
                                    <option value="Lâm Đồng">Lâm Đồng</option>
                                    <option value="Lạng Sơn">Lạng Sơn</option>
                                    <option value="Lào Cai">Lào Cai</option>
                                    <option value="Long An">Long An</option>
                                    <option value="Nam Định">Nam Định</option>
                                    <option value="Nghệ An">Nghệ An</option>
                                    <option value="Ninh Bình">Ninh Bình</option>
                                    <option value="Ninh Thuận">Ninh Thuận</option>
                                    <option value="Phú Thọ">Phú Thọ</option>
                                    <option value="Phú Yên">Phú Yên</option>
                                    <option value="Quảng Bình">Quảng Bình</option>
                                    <option value="Quảng Nam">Quảng Nam</option>
                                    <option value="Quảng Ngãi">Quảng Ngãi</option>
                                    <option value="Quảng Ninh">Quảng Ninh</option>
                                    <option value="Quảng Trị">Quảng Trị</option>
                                    <option value="Sóc Trăng">Sóc Trăng</option>
                                    <option value="Sơn La">Sơn La</option>
                                    <option value="Tây Ninh">Tây Ninh</option>
                                    <option value="Thái Bình">Thái Bình</option>
                                    <option value="Thái Nguyên">Thái Nguyên</option>
                                    <option value="Thanh Hóa">Thanh Hóa</option>
                                    <option value="Thừa Thiên Huế">Thừa Thiên Huế</option>
                                    <option value="Tiền Giang">Tiền Giang</option>
                                    <option value="Trà Vinh">Trà Vinh</option>
                                    <option value="Tuyên Quang">Tuyên Quang</option>
                                    <option value="Vĩnh Long">Vĩnh Long</option>
                                    <option value="Vĩnh Phúc">Vĩnh Phúc</option>
                                    <option value="Yên Bái">Yên Bái</option>
                                </select>
                            </div>
                            <div className="mb-5">
                                <label htmlFor="address" className="block mb-2 font-bold">
                                    Address
                                </label>
                                <input
                                    type="text"
                                    id="address"
                                    required
                                    value={orderForm.address}
                                    onChange={(e) => handleFormChange('address', e.target.value)}
                                    className="w-full p-3 border border-gray-300 rounded"
                                />
                            </div>                            <div className="mb-6">
                                <h3 className="font-bold mb-3">Shipping Options</h3>
                                {isCalculatingShipping && (
                                    <div className="text-blue-600 text-sm mb-2">Đang tính phí vận chuyển...</div>
                                )}
                                <div className="space-y-2">
                                    <label className="flex items-center gap-2 p-3 border rounded cursor-pointer hover:bg-gray-50">
                                        <input
                                            type="radio"
                                            name="shipping"
                                            value="cod"
                                            checked={shippingMethod === 'cod'}
                                            onChange={() => setShippingMethod('cod')}
                                            className="accent-[#2aa59b]"
                                        />
                                        <div className="flex-1">
                                            <div className="font-medium">Giao hàng tiêu chuẩn</div>
                                            <div className="text-sm text-gray-600">
                                                Phí vận chuyển: {formatPrice(shippingOptions.regular?.regularShippingFee || 22000)}
                                            </div>
                                            <div className="text-xs text-gray-500">Giao hàng trong 3-5 ngày</div>
                                        </div>
                                    </label>                                    <label className={`flex items-center gap-2 p-3 border rounded cursor-pointer hover:bg-gray-50 ${(!shippingOptions.express?.expressAvailable && orderForm.province) ? 'opacity-50 cursor-not-allowed' : ''
                                        }`}>
                                        <input
                                            type="radio"
                                            name="shipping"
                                            value="rush"
                                            checked={shippingMethod === 'rush'}
                                            onChange={() => setShippingMethod('rush')}
                                            disabled={!shippingOptions.express?.expressAvailable && !!orderForm.province}
                                            className="accent-[#2aa59b]"
                                        />
                                        <div className="flex-1">
                                            <div className="font-medium">Giao hàng nhanh 2h ⚡</div>
                                            <div className="text-sm text-gray-600">
                                                Phí vận chuyển: {formatPrice(shippingOptions.express?.expressShippingFee || 30000)}
                                            </div>
                                            <div className="text-xs text-gray-500">
                                                {(shippingOptions.express?.expressAvailable === false && orderForm.province) ?
                                                    'Không khả dụng cho địa chỉ này' :
                                                    'Chỉ áp dụng tại nội thành Hà Nội'}
                                            </div>
                                        </div>
                                    </label>
                                </div>
                            </div>
                            <div className="border-t border-gray-200 pt-6">
                                <p className="text-base mb-2">
                                    Tổng giá sản phẩm:{' '}
                                    <strong className="text-lg">{formatPrice(totalPrice)}</strong>
                                </p>
                                <p className="text-base mb-2">
                                    Phí vận chuyển:{' '}
                                    <strong className="text-lg">{formatPrice(shippingFee)}</strong>
                                </p>
                                <p className="text-base mb-4">
                                    Tổng thanh toán:{' '}
                                    <strong className="text-lg text-[#2aa59b]">{formatPrice(finalAmount)}</strong>
                                </p>
                            </div>
                        </form>
                    </div>

                    {/* Right Column - QR Code and Payment Options */}
                    <div className="bg-white p-8 rounded-lg shadow-lg">
                        <div className="text-center">
                            <h3 className="text-xl font-bold mb-4">
                                Phương thức thanh toán
                            </h3>

                            {/* QR Code Section */}
                            <div className="mb-8">
                                <h4 className="text-lg font-semibold mb-3 text-[#1c7ed6]">
                                    Quét mã QR để thanh toán
                                </h4>
                                <div className="bg-gray-50 p-4 rounded-lg">
                                    <img
                                        src={vnpayQR}
                                        alt="VNPay QR"
                                        className="mx-auto max-w-xs rounded-lg shadow-md"
                                    />
                                    <p className="text-[#1c7ed6] mt-3 font-medium">Scan to Pay with VNPay</p>
                                    <p className="text-sm text-gray-600 mt-1">
                                        Mở ứng dụng VNPay và quét mã QR để thanh toán
                                    </p>
                                </div>
                            </div>                            {/* Payment Buttons */}
                            <div className="space-y-4">
                                <button
                                    onClick={handleVNPayPayment}
                                    disabled={vnpayLoading || productValidation.isChecking || !productValidation.isValid}
                                    className="w-full bg-[#1c7ed6] text-white py-4 text-lg font-semibold rounded-lg hover:bg-[#1565c0] transition disabled:bg-gray-400 disabled:cursor-not-allowed flex items-center justify-center gap-2"
                                >
                                    {vnpayLoading ? (
                                        <>
                                            <i className="fas fa-spinner fa-spin"></i>
                                            Đang chuyển hướng...
                                        </>
                                    ) : (
                                        <>
                                            <i className="fas fa-credit-card"></i>
                                            Thanh toán qua VNPay
                                        </>
                                    )}
                                </button>

                                <button
                                    onClick={handleConfirmPayment}
                                    disabled={productValidation.isChecking || !productValidation.isValid}
                                    className="w-full bg-[#2aa59b] text-white py-4 text-lg font-semibold rounded-lg hover:bg-[#21857d] transition disabled:bg-gray-400 disabled:cursor-not-allowed flex items-center justify-center gap-2"
                                >
                                    {productValidation.isChecking ? (
                                        <>
                                            <i className="fas fa-spinner fa-spin"></i>
                                            Đang kiểm tra...
                                        </>
                                    ) : (
                                        <>
                                            <i className="fas fa-qrcode"></i>
                                            Hiển thị QR Code
                                        </>
                                    )}
                                </button>

                                <div className="mt-4 p-3 bg-yellow-50 border border-yellow-200 rounded-lg">
                                    <p className="text-sm text-yellow-800">
                                        <i className="fas fa-info-circle mr-1"></i>
                                        Bạn có thể thanh toán bằng cách quét QR code hoặc chuyển hướng đến trang VNPay
                                    </p>
                                </div>

                                {/* Debug info when backend is offline */}
                                <div className="mt-4 p-3 bg-blue-50 border border-blue-200 rounded-lg">
                                    <p className="text-sm text-blue-800">
                                        <i className="fas fa-info-circle mr-1"></i>
                                        Lưu ý: Nếu backend không chạy, hệ thống sẽ chuyển đến trang VNPay demo để test
                                    </p>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

                {/* Modal QR Code (Keep existing modal for backward compatibility) */}
                {showModal && (
                    <div className="fixed inset-0 bg-black bg-opacity-60 flex items-center justify-center z-50">
                        <div className="bg-white p-8 rounded-lg max-w-md w-full text-center relative">
                            <span
                                className="absolute right-4 top-3 text-2xl cursor-pointer"
                                onClick={() => setShowModal(false)}
                            >
                                &times;
                            </span>
                            <h3 className="text-lg font-bold mb-2">
                                Quét mã qua ứng dụng Ví VNPay
                            </h3>
                            <p className="mb-2">
                                <a
                                    href="#"
                                    className="text-blue-600 hover:underline"
                                >
                                    <i className="fa fa-info-circle"></i> Hướng dẫn
                                    thanh toán
                                </a>
                            </p>
                            <img
                                src={vnpayQR}
                                alt="VNPay QR"
                                className="mx-auto max-w-xs rounded"
                            />
                            <p className="text-[#1c7ed6] mt-2">Scan to Pay</p>
                            <button
                                onClick={() => setShowModal(false)}
                                className="mt-4 px-6 py-2 bg-gray-200 rounded hover:bg-gray-300"
                            >
                                Hủy thanh toán
                            </button>
                        </div>
                    </div>)}
            </div>

            {/* Toast Notification */}
            {toast && (
                <Toast
                    message={toast.message}
                    type={toast.type}
                    onClose={() => setToast(null)}
                />
            )}
        </>
    );
};

export default Checkout;
