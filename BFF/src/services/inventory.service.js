const axios = require('axios');
const { INVENTORY_SERVICE_URL } = process.env;

class InventoryService {
    constructor() {
        this.baseURL = INVENTORY_SERVICE_URL || 'http://localhost:8082/api/v1/inventory';
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
                console.log(`[InventoryService] Making ${config.method.toUpperCase()} request to: ${config.url}`);
                if (config.params) {
                    console.log(`[InventoryService] Request params:`, config.params);
                }
                if (config.data) {
                    console.log(`[InventoryService] Request data:`, config.data);
                }
                return config;
            },
            (error) => {
                console.error('[InventoryService] Request error:', error.message);
                return Promise.reject(error);
            }
        );

        // Add response interceptor for error handling
        this.axiosInstance.interceptors.response.use(
            (response) => {
                console.log(`[InventoryService] Response received with status: ${response.status}`);
                return response;
            },
            (error) => {
                console.error('[InventoryService] Response error:', error.message);
                if (error.response) {
                    console.error('[InventoryService] Error status:', error.response.status);
                    console.error('[InventoryService] Error data:', error.response.data);
                }
                return Promise.reject(error);
            }
        );
    }

    /**
     * Create inventory for a product
     * POST /api/v1/inventory
     */
    async createInventoryForProduct(createInventoryRequest) {
        try {
            const response = await this.axiosInstance.post('', createInventoryRequest);
            return response.data;
        } catch (error) {
            this.handleError(error, 'Error creating inventory');
            throw error;
        }
    }

    /**
     * Get inventory by SKU
     * GET /api/v1/inventory/{sku}
     */
    async getInventoryBySku(sku) {
        try {
            const response = await this.axiosInstance.get(`/${sku}`);
            return response.data;
        } catch (error) {
            this.handleError(error, `Error fetching inventory for SKU: ${sku}`);
            throw error;
        }
    }

    /**
     * Update inventory quantity
     * PUT /api/v1/inventory/{sku}
     */
    async updateInventoryQuantity(sku, updateInventoryRequest) {
        try {
            const response = await this.axiosInstance.put(`/${sku}`, updateInventoryRequest);
            return response.data;
        } catch (error) {
            this.handleError(error, `Error updating inventory for SKU: ${sku}`);
            throw error;
        }
    }

    /**
     * Check if inventory exists for a SKU
     * GET /api/v1/inventory/{sku}/exists
     */
    async checkInventoryExists(sku) {
        try {
            const response = await this.axiosInstance.get(`/${sku}/exists`);
            return response.data;
        } catch (error) {
            this.handleError(error, `Error checking inventory existence for SKU: ${sku}`);
            throw error;
        }
    }

    /**
     * Get all inventories
     * GET /api/v1/inventory
     */
    async getAllInventories() {
        try {
            const response = await this.axiosInstance.get('');
            return response.data;
        } catch (error) {
            this.handleError(error, 'Error fetching all inventories');
            throw error;
        }
    }

    /**
     * Health check for inventory service
     */
    async healthCheck() {
        try {
            const response = await axios.get(`${this.baseURL.replace('/api/v1/inventory', '')}/health`);
            return response.data;
        } catch (error) {
            this.handleError(error, 'Inventory service health check failed');
            throw error;
        }
    }

    /**
     * Handle and log errors consistently
     */
    handleError(error, context) {
        console.error(`[InventoryService] ${context}:`, {
            message: error.message,
            status: error.response?.status,
            data: error.response?.data
        });
    }
}

module.exports = new InventoryService();
