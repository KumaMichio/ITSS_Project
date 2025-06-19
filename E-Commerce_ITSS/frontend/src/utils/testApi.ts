import { API_BASE_URL } from '../config/api';

export const testBackendConnection = async () => {
    try {
        const response = await fetch(`${API_BASE_URL}/api/products`);
        const data = await response.json();

        if (response.ok) {
            console.log('✅ Backend connection successful!');
            console.log('Sample data:', data);
            return { success: true, data };
        } else {
            console.log('❌ Backend responded with error:', response.status);
            return { success: false, error: `HTTP ${response.status}` };
        }
    } catch (error) {
        console.log('❌ Failed to connect to backend:', error);
        return { success: false, error: error instanceof Error ? error.message : 'Unknown error' };
    }
};

export const testStockValidation = async () => {
    console.log('🔄 Testing stock validation...');

    try {
        // First test simple connection
        const testResponse = await fetch(`${API_BASE_URL}/api/products`);
        console.log('Backend connection test:', testResponse.status);

        if (!testResponse.ok) {
            console.log('❌ Backend not accessible');
            return { success: false, error: `Backend not accessible: ${testResponse.status}` };
        }        // Test với một sản phẩm có số lượng lớn (sẽ thất bại nếu stock validation hoạt động)
        // TODO: Re-enable when backend supports stock validation
        console.log('Stock validation test skipped - backend does not support checkStock endpoint');

        return { success: true, result: 'Stock validation test skipped' };

        /*
        const result = await cartService.checkStock([
            {
                productId: 1, // Giả sử sản phẩm ID 1 tồn tại
                quantity: 999999, // Số lượng rất lớn để test
                price: 0
            }
        ]);

        console.log('Stock validation result:', result);

        if (result.success && result.data) {
            if (result.data.isValid) {
                console.log('✅ Stock validation: Product has enough stock');
            } else {
                console.log('❌ Stock validation: Not enough stock');
                console.log('Errors:', result.data.errors);
            }
            return { success: true, result: result.data };
        } else {
            console.log('❌ Stock validation request failed');
            return { success: false, error: result.error };
        }
        */
    } catch (error) {
        console.log('❌ Stock validation test failed:', error);
        return { success: false, error: error instanceof Error ? error.message : 'Unknown error' };
    }
};

// Call this function to test
// testBackendConnection();
// testStockValidation();
