import Header from "../../components/Header";
import FeatureBox from "../../components/FeatureBox";
import ProductCard from "../../components/ProductCard";
import Loading from "../../components/Loading";
import { useProducts } from "../../hooks/useProducts";

// Import images
import hero4 from "../../img/hero4.png";
import f1Feature from "../../img/features/f1.png";
import f2Feature from "../../img/features/f2.png";
import f3Feature from "../../img/features/f3.png";
import f4Feature from "../../img/features/f4.png";

const features = [
    { img: f1Feature, label: "LP", type: "LP" },
    { img: f2Feature, label: "DVD", type: "DVD" },
    { img: f3Feature, label: "CD", type: "CD" },
    { img: f4Feature, label: "BOOK", type: "BOOK" },
];

const Home = () => {
    const { products, loading, error } = useProducts();

    // Get featured products (first 5) or show loading/error state
    const featuredProducts = products.slice(0, 5);

    return (
        <>
            <Header />
            <section className="bg-cover bg-center flex flex-col justify-center px-20 py-10" style={{ backgroundImage: `url(${hero4})` }}></section>
            <section className="flex flex-wrap items-center justify-between px-20 py-10 gap-4">
                {features.map((feature, index) => (
                    <FeatureBox
                        key={index}
                        img={feature.img}
                        label={feature.label}
                        type={feature.type}
                    />
                ))}
            </section>
            <section className="text-center px-20">
                <h2 className="text-3xl font-bold mb-2">What's hot</h2>
                <p className="text-xs text-gray-500 mb-4">Collection</p>
                {loading ? (
                    <Loading message="Đang tải sản phẩm..." />
                ) : error ? (
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
                ) : (
                    <div className="flex flex-wrap justify-between gap-5 border border-[#cce7d0] rounded-2xl p-4 shadow mb-4">
                        {featuredProducts.length > 0 ? (
                            featuredProducts.map((product) => (
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
                )}
            </section>
        </>
    );
};

export default Home;
