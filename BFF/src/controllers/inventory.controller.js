const AbstractController = require('./AbstractController');
const inventoryService = require('../services/inventory.service');

class InventoryController extends AbstractController {

    /**
     * Create inventory for a product
     * POST /api/inventory
     */
    async createInventoryForProduct(req, res) {
        try {
            const createInventoryRequest = req.body;
            this.logRequest(req, 'Creating inventory for product');
            
            // Basic validation
            if (!createInventoryRequest.sku) {
                return this.sendBadRequestResponse(res, 'SKU is required');
            }

            if (!this.isValidSkuCode(createInventoryRequest.sku)) {
                return this.createValidationErrorResponse(res, 'SKU', 'must be between 2 and 50 characters');
            }

            if (createInventoryRequest.quantity === undefined || createInventoryRequest.quantity < 0) {
                return this.sendBadRequestResponse(res, 'Quantity must be a non-negative number');
            }

            const result = await inventoryService.createInventoryForProduct(createInventoryRequest);
            
            // If the service returns the standard format, pass it through
            if (result && result.status !== undefined && result.data !== undefined) {
                return res.status(result.status).json(result);
            }
            
            return this.sendCreatedResponse(res, result, `Inventory created successfully for product SKU: ${createInventoryRequest.sku}`);
        } catch (error) {
            console.error('[InventoryController] Error in createInventoryForProduct:', error.message);
            return this.handleAxiosError(res, error, 'Error creating inventory');
        }
    }

    /**
     * Get inventory by SKU
     * GET /api/inventory/:sku
     */
    async getInventoryBySku(req, res) {
        try {
            const { sku } = req.params;
            this.logRequest(req, `Getting inventory for SKU: ${sku}`);
            
            if (!this.isValidSkuCode(sku)) {
                return this.createValidationErrorResponse(res, 'SKU', 'must be between 2 and 50 characters');
            }

            const result = await inventoryService.getInventoryBySku(sku);
            
            // If the service returns the standard format, pass it through
            if (result && result.status !== undefined && result.data !== undefined) {
                return res.status(result.status).json(result);
            }
            
            return this.sendSuccessResponse(res, result, `Inventory retrieved successfully for SKU: ${sku}`);
        } catch (error) {
            console.error('[InventoryController] Error in getInventoryBySku:', error.message);
            return this.handleAxiosError(res, error, `Error retrieving inventory for SKU: ${req.params.sku}`);
        }
    }

    /**
     * Update inventory quantity
     * PUT /api/inventory/:sku
     */
    async updateInventoryQuantity(req, res) {
        try {
            const { sku } = req.params;
            const updateInventoryRequest = req.body;
            this.logRequest(req, `Updating inventory quantity for SKU: ${sku}`);
            
            if (!this.isValidSkuCode(sku)) {
                return this.createValidationErrorResponse(res, 'SKU', 'must be between 2 and 50 characters');
            }

            if (updateInventoryRequest.quantity === undefined || updateInventoryRequest.quantity < 0) {
                return this.sendBadRequestResponse(res, 'Quantity must be a non-negative number');
            }

            const result = await inventoryService.updateInventoryQuantity(sku, updateInventoryRequest);
            
            // If the service returns the standard format, pass it through
            if (result && result.status !== undefined && result.data !== undefined) {
                return res.status(result.status).json(result);
            }
            
            return this.sendSuccessResponse(res, result, `Inventory quantity updated successfully for SKU: ${sku}`);
        } catch (error) {
            console.error('[InventoryController] Error in updateInventoryQuantity:', error.message);
            return this.handleAxiosError(res, error, `Error updating inventory for SKU: ${req.params.sku}`);
        }
    }

    /**
     * Check if inventory exists for a SKU
     * GET /api/inventory/:sku/exists
     */
    async checkInventoryExists(req, res) {
        try {
            const { sku } = req.params;
            this.logRequest(req, `Checking inventory existence for SKU: ${sku}`);
            
            if (!this.isValidSkuCode(sku)) {
                return this.createValidationErrorResponse(res, 'SKU', 'must be between 2 and 50 characters');
            }

            const result = await inventoryService.checkInventoryExists(sku);
            
            // If the service returns the standard format, pass it through
            if (result && result.status !== undefined && result.data !== undefined) {
                return res.status(result.status).json(result);
            }
            
            return this.sendSuccessResponse(res, result, `Inventory existence check completed for SKU: ${sku}`);
        } catch (error) {
            console.error('[InventoryController] Error in checkInventoryExists:', error.message);
            return this.handleAxiosError(res, error, `Error checking inventory existence for SKU: ${req.params.sku}`);
        }
    }

    /**
     * Get all inventories
     * GET /api/inventory
     */
    async getAllInventories(req, res) {
        try {
            this.logRequest(req, 'Getting all inventories');
            
            const result = await inventoryService.getAllInventories();
            
            // If the service returns the standard format, pass it through
            if (result && result.status !== undefined && result.data !== undefined) {
                return res.status(result.status).json(result);
            }
            
            return this.sendSuccessResponse(res, result, 'All inventories retrieved successfully');
        } catch (error) {
            console.error('[InventoryController] Error in getAllInventories:', error.message);
            return this.handleAxiosError(res, error, 'Error retrieving inventories');
        }
    }

    /**
     * Health check - proxy to inventory service health check
     */
    async healthCheck(req, res) {
        try {
            console.log('[InventoryController] Performing health check');
            const result = await inventoryService.healthCheck();
            
            return this.sendSuccessResponse(res, result, 'Inventory service is healthy');
        } catch (error) {
            console.error('[InventoryController] Health check failed:', error.message);
            return this.sendServiceUnavailableResponse(res, 'Inventory service is unhealthy');
        }
    }
}

// Create an instance and export the methods
const inventoryController = new InventoryController();

module.exports = {
    createInventoryForProduct: (req, res) => inventoryController.createInventoryForProduct(req, res),
    getInventoryBySku: (req, res) => inventoryController.getInventoryBySku(req, res),
    updateInventoryQuantity: (req, res) => inventoryController.updateInventoryQuantity(req, res),
    checkInventoryExists: (req, res) => inventoryController.checkInventoryExists(req, res),
    getAllInventories: (req, res) => inventoryController.getAllInventories(req, res),
    healthCheck: (req, res) => inventoryController.healthCheck(req, res)
};
