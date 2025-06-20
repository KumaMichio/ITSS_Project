import { apiService } from './api.service';
import { API_ENDPOINTS } from '../config/api';
import type { Product, PageResponse, PageRequest } from '../types';

export class ProductService {
    async getAllProducts(pageRequest?: PageRequest) {
        const params = pageRequest
            ? `?page=${pageRequest.page}&size=${pageRequest.size}${pageRequest.sort ? `&sort=${pageRequest.sort}` : ''}`
            : '';

        return apiService.get<PageResponse<Product>>(`${API_ENDPOINTS.PRODUCTS.BASE}${params}`);
    }

    async getProductById(id: number) {
        return apiService.get<Product>(API_ENDPOINTS.PRODUCTS.BY_ID(id));
    } async searchProducts(title: string) {
        return apiService.get<Product[]>(API_ENDPOINTS.PRODUCTS.SEARCH(title));
    }

    async getProductsByCategory(category: string) {
        return apiService.get<Product[]>(API_ENDPOINTS.PRODUCTS.BY_CATEGORY(category));
    }

    async getAllCategories() {
        return apiService.get<string[]>(API_ENDPOINTS.PRODUCTS.CATEGORIES);
    }

    async getRandomProducts(limit: number = 20) {
        return apiService.get<Product[]>(`${API_ENDPOINTS.PRODUCTS.RANDOM}?limit=${limit}`);
    }

    async createProduct(product: Omit<Product, 'id'>) {
        return apiService.post<Product>(`${API_ENDPOINTS.PRODUCTS.BASE}/add-product`, product);
    }

    async updateProduct(id: number, product: Partial<Product>) {
        return apiService.put<Product>(`${API_ENDPOINTS.PRODUCTS.BASE}/modify/${id}`, product);
    }

    async deleteProduct(id: number) {
        return apiService.delete<void>(`${API_ENDPOINTS.PRODUCTS.BASE}/delete/${id}`);
    }

    // Book specific methods
    async getAllBooks() {
        return apiService.get<any[]>(API_ENDPOINTS.PRODUCTS.BOOKS.BASE);
    }

    async getBookById(id: number) {
        return apiService.get<any>(API_ENDPOINTS.PRODUCTS.BOOKS.BY_ID(id));
    }

    async searchBooks(title: string) {
        return apiService.get<any[]>(API_ENDPOINTS.PRODUCTS.BOOKS.SEARCH(title));
    }

    async createBook(book: any) {
        return apiService.post<any>(API_ENDPOINTS.PRODUCTS.BOOKS.ADD, book);
    }

    async updateBook(id: number, book: any) {
        return apiService.put<any>(API_ENDPOINTS.PRODUCTS.BOOKS.UPDATE(id), book);
    }

    async deleteBook(id: number) {
        return apiService.delete<void>(API_ENDPOINTS.PRODUCTS.BOOKS.DELETE(id));
    }

    async deleteAllBooks() {
        return apiService.delete<void>(API_ENDPOINTS.PRODUCTS.BOOKS.DELETE_ALL);
    }

    // DVD specific methods
    async getAllDVDs() {
        return apiService.get<any[]>(API_ENDPOINTS.PRODUCTS.DVDS.BASE);
    }

    async getDVDById(id: number) {
        return apiService.get<any>(API_ENDPOINTS.PRODUCTS.DVDS.BY_ID(id));
    }

    async searchDVDs(title: string) {
        return apiService.get<any[]>(API_ENDPOINTS.PRODUCTS.DVDS.SEARCH(title));
    }

    async createDVD(dvd: any) {
        return apiService.post<any>(API_ENDPOINTS.PRODUCTS.DVDS.ADD, dvd);
    }

    async updateDVD(id: number, dvd: any) {
        return apiService.put<any>(API_ENDPOINTS.PRODUCTS.DVDS.UPDATE(id), dvd);
    }

    async deleteDVD(id: number) {
        return apiService.delete<void>(API_ENDPOINTS.PRODUCTS.DVDS.DELETE(id));
    }

    // CDLP specific methods
    async getAllCDLPs() {
        return apiService.get<any[]>(API_ENDPOINTS.PRODUCTS.CDLPS.BASE);
    }

    async getCDLPById(id: number) {
        return apiService.get<any>(API_ENDPOINTS.PRODUCTS.CDLPS.BY_ID(id));
    }

    async searchCDLPs(title: string) {
        return apiService.get<any[]>(API_ENDPOINTS.PRODUCTS.CDLPS.SEARCH(title));
    }

    async createCDLP(cdlp: any) {
        return apiService.post<any>(API_ENDPOINTS.PRODUCTS.CDLPS.ADD, cdlp);
    }

    async updateCDLP(id: number, cdlp: any) {
        return apiService.put<any>(API_ENDPOINTS.PRODUCTS.CDLPS.UPDATE(id), cdlp);
    }

    async deleteCDLP(id: number) {
        return apiService.delete<void>(API_ENDPOINTS.PRODUCTS.CDLPS.DELETE(id));
    }

    async deleteAllCDLPs() {
        return apiService.delete<void>(API_ENDPOINTS.PRODUCTS.CDLPS.DELETE_ALL);
    }
}

export const productService = new ProductService();
