const AbstractController = require('./AbstractController');
const orderService = require('../services/orders.service');

class OrderController extends AbstractController {

    /**
     * Create a new cart for a customer
     * POST /api/orders
     */
    async createCart(req, res) {
        try {
            const createCartRequest = req.body;
            this.logRequest(req, 'Creating cart for customer');
            
            // Basic validation
            if (!createCartRequest.customerId) {
                return this.sendBadRequestResponse(res, 'Customer ID is required');
            }

            if (!this.isValidCustomerId(createCartRequest.customerId)) {
                return this.createValidationErrorResponse(res, 'Customer ID', 'must be between 1 and 50 characters');
            }

            const result = await orderService.createCart(createCartRequest);
            
            // If the service returns the standard format, pass it through
            if (result && result.status !== undefined && result.data !== undefined) {
                return res.status(result.status).json(result);
            }
            
            return this.sendCreatedResponse(res, result, `Cart created successfully for customer: ${createCartRequest.customerId}`);
        } catch (error) {
            console.error('[OrderController] Error in createCart:', error.message);
            return this.handleAxiosError(res, error, 'Error creating cart');
        }
    }

    /**
     * Add item to cart
     * POST /api/orders/:cartId
     */
    async addItemToCart(req, res) {
        try {
            const { cartId } = req.params;
            const addItemRequest = req.body;
            this.logRequest(req, `Adding item to cart: ${cartId}`);
            
            if (!this.isValidCartId(cartId)) {
                return this.createValidationErrorResponse(res, 'Cart ID', 'must be a positive number');
            }

            // Basic validation for add item request
            if (!addItemRequest.skuCode) {
                return this.sendBadRequestResponse(res, 'SKU code is required');
            }

            if (!this.isValidSkuCode(addItemRequest.skuCode)) {
                return this.createValidationErrorResponse(res, 'SKU Code', 'must be between 2 and 50 characters');
            }

            if (addItemRequest.quantity === undefined || addItemRequest.quantity <= 0) {
                return this.sendBadRequestResponse(res, 'Quantity must be a positive number');
            }

            const result = await orderService.addItemToCart(cartId, addItemRequest);
            
            // If the service returns the standard format, pass it through
            if (result && result.status !== undefined && result.data !== undefined) {
                return res.status(result.status).json(result);
            }
            
            return this.sendSuccessResponse(res, result, 'Item added to cart successfully');
        } catch (error) {
            console.error('[OrderController] Error in addItemToCart:', error.message);
            return this.handleAxiosError(res, error, `Error adding item to cart: ${req.params.cartId}`);
        }
    }

    /**
     * Get cart by ID
     * GET /api/orders/:cartId
     */
    async getCartById(req, res) {
        try {
            const { cartId } = req.params;
            this.logRequest(req, `Retrieving cart: ${cartId}`);
            
            if (!this.isValidCartId(cartId)) {
                return this.createValidationErrorResponse(res, 'Cart ID', 'must be a positive number');
            }

            const result = await orderService.getCartById(cartId);
            
            // If the service returns the standard format, pass it through
            if (result && result.status !== undefined && result.data !== undefined) {
                return res.status(result.status).json(result);
            }
            
            return this.sendSuccessResponse(res, result, 'Cart retrieved successfully');
        } catch (error) {
            console.error('[OrderController] Error in getCartById:', error.message);
            return this.handleAxiosError(res, error, `Error retrieving cart: ${req.params.cartId}`);
        }
    }

    /**
     * Get cart by customer ID
     * GET /api/orders/customer/:customerId
     */
    async getCartByCustomerId(req, res) {
        try {
            const { customerId } = req.params;
            this.logRequest(req, `Retrieving cart for customer: ${customerId}`);
            
            if (!this.isValidCustomerId(customerId)) {
                return this.createValidationErrorResponse(res, 'Customer ID', 'must be between 1 and 50 characters');
            }

            const result = await orderService.getCartByCustomerId(customerId);
            
            // If the service returns the standard format, pass it through
            if (result && result.status !== undefined && result.data !== undefined) {
                return res.status(result.status).json(result);
            }
            
            return this.sendSuccessResponse(res, result, `Cart retrieved successfully for customer: ${customerId}`);
        } catch (error) {
            console.error('[OrderController] Error in getCartByCustomerId:', error.message);
            return this.handleAxiosError(res, error, `Error retrieving cart for customer: ${req.params.customerId}`);
        }
    }

    /**
     * Remove item from cart
     * DELETE /api/orders/:cartId/:skuCode
     */
    async removeItemFromCart(req, res) {
        try {
            const { cartId, skuCode } = req.params;
            this.logRequest(req, `Removing item ${skuCode} from cart: ${cartId}`);
            
            if (!this.isValidCartId(cartId)) {
                return this.createValidationErrorResponse(res, 'Cart ID', 'must be a positive number');
            }

            if (!this.isValidSkuCode(skuCode)) {
                return this.createValidationErrorResponse(res, 'SKU Code', 'must be between 2 and 50 characters');
            }

            const result = await orderService.removeItemFromCart(cartId, skuCode);
            
            // If the service returns the standard format, pass it through
            if (result && result.status !== undefined) {
                return res.status(result.status).json(result);
            }
            
            return this.sendNoContentResponse(res, 'Item removed from cart successfully');
        } catch (error) {
            console.error('[OrderController] Error in removeItemFromCart:', error.message);
            return this.handleAxiosError(res, error, `Error removing item ${req.params.skuCode} from cart: ${req.params.cartId}`);
        }
    }

    /**
     * Remove cart
     * DELETE /api/orders/:cartId
     */
    async removeCart(req, res) {
        try {
            const { cartId } = req.params;
            this.logRequest(req, `Removing cart: ${cartId}`);
            
            if (!this.isValidCartId(cartId)) {
                return this.createValidationErrorResponse(res, 'Cart ID', 'must be a positive number');
            }

            const result = await orderService.removeCart(cartId);
            
            // If the service returns the standard format, pass it through
            if (result && result.status !== undefined) {
                return res.status(result.status).json(result);
            }
            
            return this.sendNoContentResponse(res, 'Cart removed successfully');
        } catch (error) {
            console.error('[OrderController] Error in removeCart:', error.message);
            return this.handleAxiosError(res, error, `Error removing cart: ${req.params.cartId}`);
        }
    }

    /**
     * Health check - proxy to order service health check
     */
    async healthCheck(req, res) {
        try {
            console.log('[OrderController] Performing health check');
            const result = await orderService.healthCheck();
            
            return this.sendSuccessResponse(res, result, 'Order service is healthy');
        } catch (error) {
            console.error('[OrderController] Health check failed:', error.message);
            return this.sendServiceUnavailableResponse(res, 'Order service is unhealthy');
        }
    }
}

// Create an instance and export the methods
const orderController = new OrderController();

module.exports = {
    createCart: (req, res) => orderController.createCart(req, res),
    addItemToCart: (req, res) => orderController.addItemToCart(req, res),
    getCartById: (req, res) => orderController.getCartById(req, res),
    getCartByCustomerId: (req, res) => orderController.getCartByCustomerId(req, res),
    removeItemFromCart: (req, res) => orderController.removeItemFromCart(req, res),
    removeCart: (req, res) => orderController.removeCart(req, res),
    healthCheck: (req, res) => orderController.healthCheck(req, res)
};
