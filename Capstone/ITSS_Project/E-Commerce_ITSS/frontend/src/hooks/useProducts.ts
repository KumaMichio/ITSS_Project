import { useState, useEffect } from 'react';
import { productService } from '../services/product.service';
import mockProducts from '../data/mockData';
import type { Product, PageRequest, PageResponse } from '../types';

export const useProducts = (pageRequest?: PageRequest) => {
    const [products, setProducts] = useState<Product[]>([]);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState<string | null>(null);
    const [pagination, setPagination] = useState<PageResponse<Product> | null>(null);

    const fetchProducts = async () => {
        setLoading(true);
        setError(null);

        try {
            const response = await productService.getAllProducts(pageRequest);
            if (response.success && response.data) {
                if (pageRequest) {
                    // Paginated response
                    const pageData = response.data as PageResponse<Product>;
                    setProducts(pageData.content);
                    setPagination(pageData);
                } else {
                    // Simple array response - check if it's an array or paginated response
                    const data = response.data;
                    if (Array.isArray(data)) {
                        setProducts(data);
                    } else {
                        // If backend returns paginated response even without pageRequest
                        const pageData = data as PageResponse<Product>;
                        setProducts(pageData.content || []);
                        setPagination(pageData);
                    }
                }
            } else {
                // Fallback to mock data if backend is not available
                console.warn('Backend not available, using mock data');
                setProducts(mockProducts);
                setError(null); // Clear error since we have fallback data
            }
        } catch (err) {
            console.warn('Failed to fetch from backend, using mock data:', err);
            setProducts(mockProducts);
            setError(null); // Clear error since we have fallback data
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchProducts();
    }, [pageRequest?.page, pageRequest?.size, pageRequest?.sort]);

    return {
        products,
        loading,
        error,
        pagination,
        refetch: fetchProducts
    };
};

export const useProduct = (id: number) => {
    const [product, setProduct] = useState<Product | null>(null);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState<string | null>(null);

    const fetchProduct = async () => {
        if (!id) return;

        setLoading(true);
        setError(null);

        try {
            const response = await productService.getProductById(id);

            if (response.success && response.data) {
                setProduct(response.data);
            } else {
                setError(response.error || 'Failed to fetch product');
            }
        } catch (err) {
            setError(err instanceof Error ? err.message : 'Unknown error');
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchProduct();
    }, [id]);

    return {
        product,
        loading,
        error,
        refetch: fetchProduct
    };
};

export const useProductsByCategory = (category: string) => {
    const [products, setProducts] = useState<Product[]>([]);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState<string | null>(null);

    const fetchProducts = async () => {
        if (!category) {
            console.log('useProductsByCategory: No category provided, clearing products');
            setProducts([]);
            return;
        }

        console.log('useProductsByCategory: Fetching products for category:', category);
        setLoading(true);
        setError(null);

        try {
            const response = await productService.getProductsByCategory(category);
            console.log('useProductsByCategory API Response:', response);

            if (response.success && response.data) {
                console.log('useProductsByCategory: Setting products:', response.data.length, 'items');
                setProducts(response.data);
            } else {
                // Fallback to filtered mock data
                console.warn('Backend not available, using filtered mock data');
                const filteredMockProducts = mockProducts.filter(p => p.category === category);
                console.log('useProductsByCategory: Mock products filtered:', filteredMockProducts.length, 'items');
                setProducts(filteredMockProducts);
                setError(null);
            }
        } catch (err) {
            console.warn('Failed to fetch from backend, using filtered mock data:', err);
            const filteredMockProducts = mockProducts.filter(p => p.category === category);
            console.log('useProductsByCategory: Mock products filtered (error):', filteredMockProducts.length, 'items');
            setProducts(filteredMockProducts);
            setError(null);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchProducts();
    }, [category]);

    return {
        products,
        loading,
        error,
        refetch: fetchProducts
    };
};

export const useProductSearch = (query: string) => {
    const [products, setProducts] = useState<Product[]>([]);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState<string | null>(null);

    const searchProducts = async () => {
        if (!query.trim()) {
            setProducts([]);
            return;
        }

        setLoading(true);
        setError(null);

        try {
            const response = await productService.searchProducts(query);

            if (response.success && response.data) {
                setProducts(response.data);
            } else {
                setError(response.error || 'Failed to search products');
            }
        } catch (err) {
            setError(err instanceof Error ? err.message : 'Unknown error');
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        const debounceTimer = setTimeout(() => {
            searchProducts();
        }, 300); // Debounce search by 300ms

        return () => clearTimeout(debounceTimer);
    }, [query]); return {
        products,
        loading,
        error,
        search: searchProducts
    };
};

// Hook to get all categories
export const useCategories = () => {
    const [categories, setCategories] = useState<string[]>([]);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState<string | null>(null);

    const fetchCategories = async () => {
        setLoading(true);
        setError(null);

        try {
            const response = await productService.getAllCategories();

            if (response.success && response.data) {
                setCategories(response.data);
            } else {
                // Fallback to mock categories
                const mockCategories = Array.from(new Set(mockProducts.map(p => p.category)));
                setCategories(mockCategories);
                setError(null);
            }
        } catch (err) {
            console.warn('Failed to fetch categories, using mock data:', err);
            const mockCategories = Array.from(new Set(mockProducts.map(p => p.category)));
            setCategories(mockCategories);
            setError(null);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchCategories();
    }, []);

    return {
        categories,
        loading,
        error,
        refetch: fetchCategories
    };
};

// Hook to get random products
export const useRandomProducts = (limit: number = 20) => {
    const [products, setProducts] = useState<Product[]>([]);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState<string | null>(null);

    const fetchRandomProducts = async () => {
        setLoading(true);
        setError(null);

        try {
            const response = await productService.getRandomProducts(limit);

            if (response.success && response.data) {
                setProducts(response.data);
            } else {
                // Fallback to shuffled mock data
                const shuffled = [...mockProducts].sort(() => 0.5 - Math.random());
                setProducts(shuffled.slice(0, limit));
                setError(null);
            }
        } catch (err) {
            console.warn('Failed to fetch random products, using mock data:', err);
            const shuffled = [...mockProducts].sort(() => 0.5 - Math.random());
            setProducts(shuffled.slice(0, limit));
            setError(null);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchRandomProducts();
    }, [limit]);

    return {
        products,
        loading,
        error,
        refetch: fetchRandomProducts
    };
};
