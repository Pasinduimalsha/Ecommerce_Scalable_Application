const axios = require('axios');
const { ORDER_SERVICE_URL } = process.env;

class OrderService {
    constructor() {
        this.baseURL = ORDER_SERVICE_URL || 'http://localhost:8083/api/v1/order';
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
                console.log(`[OrderService] Making ${config.method.toUpperCase()} request to: ${config.url}`);
                if (config.params) {
                    console.log(`[OrderService] Request params:`, config.params);
                }
                if (config.data) {
                    console.log(`[OrderService] Request data:`, config.data);
                }
                return config;
            },
            (error) => {
                console.error('[OrderService] Request error:', error.message);
                return Promise.reject(error);
            }
        );

        // Add response interceptor for error handling
        this.axiosInstance.interceptors.response.use(
            (response) => {
                console.log(`[OrderService] Response received with status: ${response.status}`);
                return response;
            },
            (error) => {
                console.error('[OrderService] Response error:', error.message);
                if (error.response) {
                    console.error('[OrderService] Error status:', error.response.status);
                    console.error('[OrderService] Error data:', error.response.data);
                }
                return Promise.reject(error);
            }
        );
    }

    /**
     * Create a new cart for a customer
     * POST /api/v1/order
     */
    async createCart(createCartRequest) {
        try {
            const response = await this.axiosInstance.post('', createCartRequest);
            return response.data;
        } catch (error) {
            this.handleError(error, 'Error creating cart');
            throw error;
        }
    }

    /**
     * Add item to cart
     * POST /api/v1/order/{cartId}
     */
    async addItemToCart(cartId, addItemRequest) {
        try {
            const response = await this.axiosInstance.post(`/${cartId}`, addItemRequest);
            return response.data;
        } catch (error) {
            this.handleError(error, `Error adding item to cart: ${cartId}`);
            throw error;
        }
    }

    /**
     * Get cart by ID
     * GET /api/v1/order/{cartId}
     */
    async getCartById(cartId) {
        try {
            const response = await this.axiosInstance.get(`/${cartId}`);
            return response.data;
        } catch (error) {
            this.handleError(error, `Error fetching cart: ${cartId}`);
            throw error;
        }
    }

    /**
     * Get cart by customer ID
     * GET /api/v1/order/customer/{customerId}
     */
    async getCartByCustomerId(customerId) {
        try {
            const response = await this.axiosInstance.get(`/customer/${customerId}`);
            return response.data;
        } catch (error) {
            this.handleError(error, `Error fetching cart for customer: ${customerId}`);
            throw error;
        }
    }

    /**
     * Remove item from cart
     * DELETE /api/v1/order/{cartId}/{skuCode}
     */
    async removeItemFromCart(cartId, skuCode) {
        try {
            const response = await this.axiosInstance.delete(`/${cartId}/${skuCode}`);
            return response.data;
        } catch (error) {
            this.handleError(error, `Error removing item ${skuCode} from cart: ${cartId}`);
            throw error;
        }
    }

    /**
     * Remove cart
     * DELETE /api/v1/order/{cartId}
     */
    async removeCart(cartId) {
        try {
            const response = await this.axiosInstance.delete(`/${cartId}`);
            return response.data;
        } catch (error) {
            this.handleError(error, `Error removing cart: ${cartId}`);
            throw error;
        }
    }

    /**
     * Health check for order service
     */
    async healthCheck() {
        try {
            const response = await axios.get(`${this.baseURL.replace('/api/v1/order', '')}/health`);
            return response.data;
        } catch (error) {
            this.handleError(error, 'Order service health check failed');
            throw error;
        }
    }

    /**
     * Handle and log errors consistently
     */
    handleError(error, context) {
        console.error(`[OrderService] ${context}:`, {
            message: error.message,
            status: error.response?.status,
            data: error.response?.data
        });
    }
}

module.exports = new OrderService();
