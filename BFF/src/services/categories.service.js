const axios = require('axios');
const { PRODUCT_SERVICE_URL } = process.env;

class CategoryService {
    constructor() {
        // Categories API is part of the product service
        this.baseURL = PRODUCT_SERVICE_URL?.replace('/products', '/categories') || 'http://localhost:8080/api/v1/categories';
        this.axiosInstance = axios.create({
            baseURL: this.baseURL,
            timeout: 15000,
            headers: {
                'Content-Type': 'application/json',
                'User-Agent': 'BFF-Service/1.0.0'
            }
        });

        // Add request interceptor for logging
        this.axiosInstance.interceptors.request.use(
            (config) => {
                console.log(`[CategoryService] Making ${config.method.toUpperCase()} request to: ${config.url}`);
                if (config.params) {
                    console.log(`[CategoryService] Request params:`, config.params);
                }
                if (config.data) {
                    console.log(`[CategoryService] Request data:`, config.data);
                }
                return config;
            },
            (error) => {
                console.error('[CategoryService] Request error:', error.message);
                return Promise.reject(error);
            }
        );

        // Add response interceptor for error handling
        this.axiosInstance.interceptors.response.use(
            (response) => {
                console.log(`[CategoryService] Response received with status: ${response.status}`);
                return response;
            },
            (error) => {
                console.error('[CategoryService] Response error:', error.message);
                if (error.response) {
                    console.error('[CategoryService] Error status:', error.response.status);
                    console.error('[CategoryService] Error data:', error.response.data);
                }
                return Promise.reject(error);
            }
        );
    }

    /**
     * Get all categories
     * GET /api/v1/categories
     */
    async getAllCategories() {
        try {
            const response = await this.axiosInstance.get('');
            return response.data;
        } catch (error) {
            this.handleError(error, 'Error fetching all categories');
            throw error;
        }
    }

    /**
     * Get category by ID
     * GET /api/v1/categories/{categoryId}
     */
    async getCategoryById(categoryId) {
        try {
            const response = await this.axiosInstance.get(`/${categoryId}`);
            return response.data;
        } catch (error) {
            this.handleError(error, `Error fetching category with ID: ${categoryId}`);
            throw error;
        }
    }

    /**
     * Create a new category
     * POST /api/v1/categories
     */
    async createCategory(categoryData) {
        try {
            const response = await this.axiosInstance.post('', categoryData);
            return response.data;
        } catch (error) {
            this.handleError(error, 'Error creating category');
            throw error;
        }
    }

    /**
     * Update a category
     * PUT /api/v1/categories/{categoryId}
     */
    async updateCategory(categoryId, categoryData) {
        try {
            const response = await this.axiosInstance.put(`/${categoryId}`, categoryData);
            return response.data;
        } catch (error) {
            this.handleError(error, `Error updating category with ID: ${categoryId}`);
            throw error;
        }
    }

    /**
     * Delete a category
     * DELETE /api/v1/categories/{categoryId}
     */
    async deleteCategory(categoryId) {
        try {
            const response = await this.axiosInstance.delete(`/${categoryId}`);
            return response.data;
        } catch (error) {
            this.handleError(error, `Error deleting category with ID: ${categoryId}`);
            throw error;
        }
    }

    /**
     * Health check for category service (same as product service)
     */
    async healthCheck() {
        try {
            const productServiceBaseUrl = this.baseURL.replace('/api/v1/categories', '');
            const response = await axios.get(`${productServiceBaseUrl}/health`);
            return response.data;
        } catch (error) {
            this.handleError(error, 'Category service health check failed');
            throw error;
        }
    }

    /**
     * Handle and log errors consistently
     */
    handleError(error, context) {
        console.error(`[CategoryService] ${context}:`, {
            message: error.message,
            status: error.response?.status,
            data: error.response?.data
        });
    }
}

module.exports = new CategoryService();
