const axios = require('axios');
const { PRODUCT_SERVICE_URL } = process.env;

class ProductService {
    constructor() {
        this.baseURL = PRODUCT_SERVICE_URL || 'http://localhost:8081/api/v1/products';
        this.axiosInstance = axios.create({
            baseURL: this.baseURL,
            timeout: 15000, // Increased timeout for better reliability
            headers: {
                'Content-Type': 'application/json',
                'User-Agent': 'BFF-Service/1.0.0'
            }
        });

        // Add request interceptor for logging
        this.axiosInstance.interceptors.request.use(
            (config) => {
                console.log(`[ProductService] Making ${config.method.toUpperCase()} request to: ${config.url}`);
                if (config.params) {
                    console.log(`[ProductService] Request params:`, config.params);
                }
                if (config.data) {
                    console.log(`[ProductService] Request data:`, config.data);
                }
                return config;
            },
            (error) => {
                console.error('[ProductService] Request error:', error.message);
                return Promise.reject(error);
            }
        );

        // Add response interceptor for error handling
        this.axiosInstance.interceptors.response.use(
            (response) => {
                console.log(`[ProductService] Response received with status: ${response.status}`);
                return response;
            },
            (error) => {
                console.error('[ProductService] Response error:', error.message);
                if (error.response) {
                    console.error('[ProductService] Error status:', error.response.status);
                    console.error('[ProductService] Error data:', error.response.data);
                }
                return Promise.reject(error);
            }
        );
    }

    /**
     * Get all approved products (for customers)
     */
    async getAllApprovedProducts() {
        try {
            const response = await this.axiosInstance.get('/approved');
            return response.data;
        } catch (error) {
            this.handleError(error, 'Error fetching approved products');
            throw error;
        }
    }

    /**
     * Get all products (for admin/data steward)
     */
    async getAllProducts() {
        try {
            const response = await this.axiosInstance.get('');
            return response.data;
        } catch (error) {
            this.handleError(error, 'Error fetching all products');
            throw error;
        }
    }

    /**
     * Get product by ID
     */
    async getProductById(id) {
        try {
            const response = await this.axiosInstance.get(`/${id}`);
            return response.data;
        } catch (error) {
            this.handleError(error, `Error fetching product with ID: ${id}`);
            throw error;
        }
    }

    /**
     * Create a new product
     */
    async createProduct(productData) {
        try {
            const response = await this.axiosInstance.post('', productData);
            return response.data;
        } catch (error) {
            this.handleError(error, 'Error creating product');
            throw error;
        }
    }

    /**
     * Get products by status (PENDING, APPROVED, REJECTED)
     */
    async getProductsByStatus(status) {
        try {
            const response = await this.axiosInstance.get(`/status/${status}`);
            return response.data;
        } catch (error) {
            this.handleError(error, `Error fetching products with status: ${status}`);
            throw error;
        }
    }

    /**
     * Review a product (approve/reject) - Updated to match new backend API
     */
    async reviewProduct(productId, status, reviewData) {
        try {
            const response = await this.axiosInstance.put(`/${productId}/review`, reviewData, {
                params: { status: status }
            });
            return response.data;
        } catch (error) {
            this.handleError(error, `Error reviewing product with ID: ${productId}`);
            throw error;
        }
    }

    /**
     * Search products by name, category, brand, or SKU
     */
    async searchProducts(searchValue) {
        try {
            const response = await this.axiosInstance.get('', {
                params: { search: searchValue }
            });
            return response.data;
        } catch (error) {
            this.handleError(error, `Error searching products with value: ${searchValue}`);
            throw error;
        }
    }

    /**
     * Get products by category name
     */
    async getProductsByCategoryName(categoryName) {
        try {
            const response = await this.axiosInstance.get(`/categories/${categoryName}`);
            return response.data;
        } catch (error) {
            this.handleError(error, `Error fetching products by category: ${categoryName}`);
            throw error;
        }
    }

    /**
     * Update a product
     */
    async updateProduct(productId, productData) {
        try {
            const response = await this.axiosInstance.put(`/${productId}`, productData);
            return response.data;
        } catch (error) {
            this.handleError(error, `Error updating product with ID: ${productId}`);
            throw error;
        }
    }

    /**
     * Delete a product
     */
    async deleteProduct(productId) {
        try {
            const response = await this.axiosInstance.delete(`/${productId}`);
            return response.data;
        } catch (error) {
            this.handleError(error, `Error deleting product with ID: ${productId}`);
            throw error;
        }
    }

    /**
     * Health check for product service
     */
    async healthCheck() {
        try {
            const response = await axios.get(`${this.baseURL.replace('/api/v1/products', '')}/health`);
            return response.data;
        } catch (error) {
            this.handleError(error, 'Product service health check failed');
            throw error;
        }
    }

    /**
     * Handle and log errors consistently
     */
    handleError(error, context) {
        console.error(`[ProductService] ${context}:`, {
            message: error.message,
            status: error.response?.status,
            data: error.response?.data
        });
    }
}

module.exports = new ProductService();
