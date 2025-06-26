import Header from "../../components/Header";
import FeatureBox from "../../components/FeatureBox";
import ProductCard from "../../components/ProductCard";
import Loading from "../../components/Loading";
import { useRandomProducts, useCategories } from "../../hooks/useProducts";
import { useAuth } from "../../contexts/AuthContext";
import { useState } from "react";
import { Navigate } from "react-router-dom";

// Import images
import hero4 from "../../img/hero4.png";
import f1Feature from "../../img/features/f1.png";
import f2Feature from "../../img/features/f2.png";
import f3Feature from "../../img/features/f3.png";
import f4Feature from "../../img/features/f4.png";

const categoryImages: { [key: string]: string } = {
    'LP': f1Feature,
    'DVD': f2Feature,
    'CD': f3Feature,
    'BOOK': f4Feature,
    'Book': f4Feature,
    'Music': f3Feature,
    'Movies': f2Feature,
    'default': f1Feature
};

const Home = () => {
    const { user, isAuthenticated } = useAuth();
    const [currentPage, setCurrentPage] = useState(1);
    const productsPerPage = 20;

    // If user is admin, redirect to admin panel
    if (isAuthenticated && user?.role === 'admin') {
        return <Navigate to="/admin" replace />;
    }

    // Fetch 20 random products for current page
    const { products, loading: productsLoading, error: productsError, refetch } = useRandomProducts(productsPerPage);
    const { categories, loading: categoriesLoading, error: categoriesError } = useCategories();

    // Create features array from categories
    const features = categories.map(category => ({
        img: categoryImages[category] || categoryImages['default'],
        label: category.toUpperCase(),
        type: category
    }));

    const handlePageChange = (pageNumber: number) => {
        setCurrentPage(pageNumber);
        // Refetch new random products for the new page
        refetch();
    };

    const handleRefreshProducts = () => {
        refetch();
    };

    return (
        <>
            <Header />

            {/* Hero Section */}
            <section
                className="bg-cover bg-center flex flex-col justify-center px-20 py-10 min-h-[400px]"
                style={{ backgroundImage: `url(${hero4})` }}
            >
                <div className="text-black">
                    <h1 className="text-5xl font-bold mb-4">Chào mừng đến với cửa hàng</h1>
                    <p className="text-xl">Khám phá những sản phẩm tuyệt vời</p>
                </div>
            </section>

            {/* Categories Section */}
            <section className="px-20 py-10">
                <h2 className="text-3xl font-bold text-center mb-8">Danh mục sản phẩm</h2>
                {categoriesLoading ? (
                    <Loading message="Đang tải danh mục..." />
                ) : categoriesError ? (
                    <div className="text-center text-red-500">
                        <p>Lỗi tải danh mục: {categoriesError}</p>
                    </div>
                ) : (
                    <div className="flex flex-wrap items-center justify-center gap-4">
                        {features.map((feature, index) => (
                            <FeatureBox
                                key={index}
                                img={feature.img}
                                label={feature.label}
                                type={feature.type}
                            />
                        ))}
                    </div>
                )}
            </section>

            {/* Products Section */}
            <section className="text-center px-20 pb-10">
                <h2 className="text-3xl font-bold mb-2">Sản phẩm nổi bật</h2>
                <p className="text-xs text-gray-500 mb-8">20 sản phẩm ngẫu nhiên mỗi trang</p>

                {productsLoading ? (
                    <Loading message="Đang tải sản phẩm..." />
                ) : productsError ? (
                    <div className="flex justify-center items-center min-h-[400px]">
                        <div className="text-center">
                            <p className="text-red-500 mb-4">Lỗi tải sản phẩm: {productsError}</p>
                            <button
                                onClick={() => window.location.reload()}
                                className="bg-[#088178] text-white px-4 py-2 rounded hover:bg-[#066e6a] transition"
                            >
                                Thử lại
                            </button>
                        </div>
                    </div>
                ) : (
                    <>                        <div className="flex flex-wrap justify-between gap-5 border border-[#cce7d0] rounded-2xl p-4 shadow mb-8">
                        {products.length > 0 ? (
                            products.map((product) => (
                                <ProductCard
                                    key={product.id}
                                    product={product}
                                />
                            ))
                        ) : (
                            <div className="w-full text-center py-20">
                                <p className="text-gray-500">Không có sản phẩm nào</p>
                            </div>
                        )}
                    </div>

                        {/* Pagination and Refresh */}
                        <div className="flex justify-center gap-4 items-center">
                            <button
                                onClick={() => handlePageChange(Math.max(1, currentPage - 1))}
                                disabled={currentPage === 1}
                                className="px-4 py-2 bg-gray-300 text-gray-700 rounded disabled:opacity-50 disabled:cursor-not-allowed hover:bg-gray-400 transition"
                            >
                                Trước
                            </button>

                            <div className="flex items-center gap-2">
                                <span className="text-sm text-gray-600">Trang {currentPage}</span>
                                <button
                                    onClick={handleRefreshProducts}
                                    className="px-3 py-1 bg-[#088178] text-white rounded text-sm hover:bg-[#066e6a] transition"
                                    title="Tải sản phẩm mới"
                                >
                                    <i className="fas fa-refresh mr-1"></i>
                                    Làm mới
                                </button>
                            </div>

                            <button
                                onClick={() => handlePageChange(currentPage + 1)}
                                className="px-4 py-2 bg-gray-300 text-gray-700 rounded hover:bg-gray-400 transition"
                            >
                                Sau
                            </button>
                        </div>
                    </>
                )}
            </section>
        </>
    );
};

export default Home;
