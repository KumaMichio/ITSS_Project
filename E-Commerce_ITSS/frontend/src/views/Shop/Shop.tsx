import { useState, useEffect } from "react";
import { useSearchParams } from "react-router-dom";
import Header from "../../components/Header";
import FeatureBox from "../../components/FeatureBox";
import ProductCard from "../../components/ProductCard";
import Loading from "../../components/Loading";
import { useProducts, useProductsByCategory } from "../../hooks/useProducts";
import { productService } from "../../services/product.service";
import type { Product } from "../../types";

// Import images
import f2Feature from "../../img/features/f2.png";
import f3Feature from "../../img/features/f3.png";
import f4Feature from "../../img/features/f4.png";

const features = [
    { type: "Book", label: "BOOK", img: f4Feature },
    { type: "CD", label: "CD", img: f3Feature },
    { type: "DVD", label: "DVD", img: f2Feature },
    { type: "CDLP", label: "CDLP", img: f3Feature },
];

const Shop = () => {
    const [filter, setFilter] = useState<string | null>(null);
    const [searchParams] = useSearchParams();
    const [searchResults, setSearchResults] = useState<Product[]>([]);
    const [searchLoading, setSearchLoading] = useState(false);
    const [searchError, setSearchError] = useState<string>("");
    const searchQuery = searchParams.get('search');

    // Use different hooks based on filter
    const { products: allProducts, loading: loadingAll, error: errorAll } = useProducts();
    const { products: filteredProducts, loading: loadingFiltered, error: errorFiltered } = useProductsByCategory(filter || '');

    // Search for products when searchQuery changes
    useEffect(() => {
        if (searchQuery) {
            searchProducts(searchQuery);
        } else {
            setSearchResults([]);
            setSearchError("");
        }
    }, [searchQuery]);

    const searchProducts = async (query: string) => {
        setSearchLoading(true);
        setSearchError("");
        try {
            const response = await productService.searchProducts(query);
            if (response.success && response.data) {
                setSearchResults(response.data);
            } else {
                setSearchError(response.error || "Không thể tìm kiếm sản phẩm");
                setSearchResults([]);
            }
        } catch (error) {
            console.error('Search error:', error);
            setSearchError("Lỗi khi tìm kiếm sản phẩm");
            setSearchResults([]);
        } finally {
            setSearchLoading(false);
        }
    };    // Determine which data to use
    let products, loading, error, title;

    console.log('Shop Debug:', {
        searchQuery,
        filter,
        allProducts: allProducts?.length,
        filteredProducts: filteredProducts?.length,
        searchResults: searchResults?.length
    });

    if (searchQuery) {
        products = searchResults;
        loading = searchLoading;
        error = searchError;
        title = `Kết quả tìm kiếm: "${searchQuery}"`;
    } else if (filter) {
        products = filteredProducts;
        loading = loadingFiltered;
        error = errorFiltered;
        title = `Shop ${filter}`;
    } else {
        products = allProducts;
        loading = loadingAll;
        error = errorAll;
        title = 'Shop All';
    } const handleFilterChange = (categoryType: string) => {
        console.log('Filter change requested:', categoryType);
        console.log('Current filter:', filter);

        if (searchQuery) {
            // Clear search when filtering by category
            window.location.href = '/shop';
            return;
        }

        const newFilter = filter === categoryType ? null : categoryType;
        console.log('Setting new filter:', newFilter);
        setFilter(newFilter);
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
            <Header />            <section className="flex flex-wrap items-center justify-between px-20 py-10 gap-4">
                {!searchQuery && features.map((feature, index) => (
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
                    {title}
                </h2>
                <p className="text-xs text-gray-500 mb-4">
                    {searchQuery ? `${products.length} sản phẩm được tìm thấy` : 'Collection'}
                </p>{loading ? (
                    <Loading message="Đang tải sản phẩm..." />
                ) : (
                    <div className="flex flex-wrap justify-between gap-5 border border-[#cce7d0] rounded-2xl p-4 shadow mb-4">
                        {products.length > 0 ? (
                            products.map((product) => (
                                <ProductCard
                                    key={product.id}
                                    product={product}
                                />
                            ))) : (
                            <div className="w-full text-center py-20">
                                <p className="text-gray-500">
                                    {searchQuery
                                        ? `Không tìm thấy sản phẩm nào cho "${searchQuery}"`
                                        : filter
                                            ? `Không có sản phẩm nào trong danh mục ${filter}`
                                            : 'Không có sản phẩm nào'
                                    }
                                </p>
                                {searchQuery && (
                                    <button
                                        onClick={() => window.location.href = '/shop'}
                                        className="mt-4 bg-[#088178] text-white px-4 py-2 rounded hover:bg-[#066e6a] transition"
                                    >
                                        Xem tất cả sản phẩm
                                    </button>
                                )}
                            </div>
                        )}
                    </div>
                )}
            </section>
        </>
    );
};

export default Shop;
