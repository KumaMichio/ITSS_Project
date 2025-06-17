import { useState } from "react";
import Header from "../../components/Header";
import FeatureBox from "../../components/FeatureBox";
import ProductCard from "../../components/ProductCard";
import Loading from "../../components/Loading";
import { useProducts, useProductsByCategory } from "../../hooks/useProducts";

// Import images
import f2Feature from "../../img/features/f2.png";
import f3Feature from "../../img/features/f3.png";
import f4Feature from "../../img/features/f4.png";

const features = [
    { type: "Books", label: "BOOK", img: f4Feature },
    { type: "Music", label: "CD/LP", img: f3Feature },
    { type: "Movies", label: "DVD", img: f2Feature },
];

const Shop = () => {
    const [filter, setFilter] = useState<string | null>(null);

    // Use different hooks based on filter
    const { products: allProducts, loading: loadingAll, error: errorAll } = useProducts();
    const { products: filteredProducts, loading: loadingFiltered, error: errorFiltered } = useProductsByCategory(filter || '');

    // Determine which data to use
    const products = filter ? filteredProducts : allProducts;
    const loading = filter ? loadingFiltered : loadingAll;
    const error = filter ? errorFiltered : errorAll;

    const handleFilterChange = (categoryType: string) => {
        setFilter(filter === categoryType ? null : categoryType);
    };

    if (error) {
        return (
            <>
                <Header />
                <div className="flex justify-center items-center min-h-[400px]">
                    <div className="text-center">
                        <p className="text-red-500 mb-4">Lỗi tải sản phẩm: {error}</p>
                        <button
                            onClick={() => window.location.reload()}
                            className="bg-[#088178] text-white px-4 py-2 rounded hover:bg-[#066e6a] transition"
                        >
                            Thử lại
                        </button>
                    </div>
                </div>
            </>
        );
    }

    return (
        <>
            <Header />
            <section className="flex flex-wrap items-center justify-between px-20 py-10 gap-4">
                {features.map((feature, index) => (
                    <FeatureBox
                        key={index}
                        img={feature.img}
                        label={feature.label}
                        type={feature.type}
                        isActive={filter === feature.type}
                        onClick={() => handleFilterChange(feature.type)}
                    />
                ))}
            </section>

            <section className="text-center px-20">
                <h2 className="text-3xl font-bold mb-2">
                    {filter ? `Shop ${filter}` : 'Shop All'}
                </h2>
                <p className="text-xs text-gray-500 mb-4">Collection</p>                {loading ? (
                    <Loading message="Đang tải sản phẩm..." />
                ) : (
                    <div className="flex flex-wrap justify-between gap-5 border border-[#cce7d0] rounded-2xl p-4 shadow mb-4">
                        {products.length > 0 ? (
                            products.map((product) => (
                                <ProductCard
                                    key={product.id}
                                    product={product}
                                />
                            ))
                        ) : (
                            <div className="w-full text-center py-20">
                                <p className="text-gray-500">
                                    {filter ? `Không có sản phẩm nào trong danh mục ${filter}` : 'Không có sản phẩm nào'}
                                </p>
                            </div>
                        )}
                    </div>
                )}
            </section>
        </>
    );
};

export default Shop;
