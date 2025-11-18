const AbstractController = require('./AbstractController');
const productService = require('../services/products.service');

class ProductController extends AbstractController {

    /**
     * Get all approved products (for customers)
     * GET /api/products/approved
     */
    async getAllApprovedProducts(req, res) {
        try {
            this.logRequest(req, 'Getting all approved products');
            const result = await productService.getAllApprovedProducts();
            
            // If the service returns the standard format, pass it through
            if (result && result.status !== undefined && result.data !== undefined) {
                return res.status(result.status).json(result);
            }
            
            return this.sendSuccessResponse(res, result, 'Approved products retrieved successfully');
        } catch (error) {
            console.error('[ProductController] Error in getAllApprovedProducts:', error.message);
            return this.handleAxiosError(res, error, 'Error fetching approved products');
        }
    }

    /**
     * Get all products (for admin/data steward)
     * GET /api/products
     */
    async getAllProducts(req, res) {
        try {
            this.logRequest(req, 'Getting all products');
            const result = await productService.getAllProducts();
            
            // If the service returns the standard format, pass it through
            if (result && result.status !== undefined && result.data !== undefined) {
                return res.status(result.status).json(result);
            }
            
            return this.sendSuccessResponse(res, result, 'All products retrieved successfully');
        } catch (error) {
            console.error('[ProductController] Error in getAllProducts:', error.message);
            return this.handleAxiosError(res, error, 'Error fetching products');
        }
    }

    /**
     * Get product by ID
     * GET /api/products/:id
     */
    async getProductById(req, res) {
        try {
            const { id } = req.params;
            this.logRequest(req, `Getting product by ID: ${id}`);
            
            if (!this.isValidProductId(id)) {
                return this.sendBadRequestResponse(res, 'Invalid product ID provided. Must be a positive number.');
            }

            const result = await productService.getProductById(id);
            
            // If the service returns the standard format, pass it through
            if (result && result.status !== undefined && result.data !== undefined) {
                return res.status(result.status).json(result);
            }
            
            return this.sendSuccessResponse(res, result, 'Product retrieved successfully');
        } catch (error) {
            console.error(`[ProductController] Error in getProductById:`, error.message);
            return this.handleAxiosError(res, error, `Error fetching product with ID: ${req.params.id}`);
        }
    }

    /**
     * Create a new product
     * POST /api/products
     */
    async createProduct(req, res) {
        try {
            const productData = req.body;
            this.logRequest(req, 'Creating new product');
            
            // Basic validation
            if (!productData.name || !productData.sku || productData.price === undefined) {
                return this.sendBadRequestResponse(res, 'Missing required fields: name, sku, price');
            }

            const result = await productService.createProduct(productData);
            
            // If the service returns the standard format, pass it through
            if (result && result.status !== undefined && result.data !== undefined) {
                return res.status(result.status).json(result);
            }
            
            return this.sendCreatedResponse(res, result, 'Product created successfully');
        } catch (error) {
            console.error('[ProductController] Error in createProduct:', error.message);
            return this.handleAxiosError(res, error, 'Error creating product');
        }
    }

    /**
     * Update a product
     * PUT /api/products/:id
     */
    async updateProduct(req, res) {
        try {
            const { id } = req.params;
            const productData = req.body;
            this.logRequest(req, `Updating product with ID: ${id}`);
            
            if (!this.isValidProductId(id)) {
                return this.sendBadRequestResponse(res, 'Invalid product ID provided. Must be a positive number.');
            }

            const result = await productService.updateProduct(id, productData);
            
            // If the service returns the standard format, pass it through
            if (result && result.status !== undefined && result.data !== undefined) {
                return res.status(result.status).json(result);
            }
            
            return this.sendSuccessResponse(res, result, 'Product updated successfully');
        } catch (error) {
            console.error(`[ProductController] Error in updateProduct:`, error.message);
            return this.handleAxiosError(res, error, `Error updating product with ID: ${req.params.id}`);
        }
    }

    /**
     * Delete a product
     * DELETE /api/products/:id
     */
    async deleteProduct(req, res) {
        try {
            const { id } = req.params;
            this.logRequest(req, `Deleting product with ID: ${id}`);
            
            if (!this.isValidProductId(id)) {
                return this.sendBadRequestResponse(res, 'Invalid product ID provided. Must be a positive number.');
            }

            const result = await productService.deleteProduct(id);
            
            // If the service returns the standard format, pass it through
            if (result && result.status !== undefined) {
                return res.status(result.status).json(result);
            }
            
            return this.sendSuccessResponse(res, null, 'Product deleted successfully');
        } catch (error) {
            console.error(`[ProductController] Error in deleteProduct:`, error.message);
            return this.handleAxiosError(res, error, `Error deleting product with ID: ${req.params.id}`);
        }
    }

    /**
     * Get products by status
     * GET /api/products/status/:status
     */
    async getProductsByStatus(req, res) {
        try {
            const { status } = req.params;
            this.logRequest(req, `Getting products by status: ${status}`);
            
            // Validate status
            const validStatuses = ['PENDING', 'APPROVED', 'REJECTED'];
            if (!validStatuses.includes(status.toUpperCase())) {
                return this.sendBadRequestResponse(res, `Invalid status. Must be one of: ${validStatuses.join(', ')}`);
            }

            const result = await productService.getProductsByStatus(status.toUpperCase());
            
            // If the service returns the standard format, pass it through
            if (result && result.status !== undefined && result.data !== undefined) {
                return res.status(result.status).json(result);
            }
            
            return this.sendSuccessResponse(res, result, `Products with status ${status} retrieved successfully`);
        } catch (error) {
            console.error('[ProductController] Error in getProductsByStatus:', error.message);
            return this.handleAxiosError(res, error, `Error fetching products by status: ${req.params.status}`);
        }
    }

    /**
     * Review a product (approve/reject)
     * PUT /api/products/:id/review?status=APPROVED
     */
    async reviewProduct(req, res) {
        try {
            const { id } = req.params;
            const { status } = req.query;
            const reviewData = req.body;
            
            this.logRequest(req, `Reviewing product with ID: ${id} to status: ${status}`);
            
            // Validate product ID
            if (!this.isValidProductId(id)) {
                return this.sendBadRequestResponse(res, 'Invalid product ID provided. Must be a positive number.');
            }

            // Validate review status
            const validReviewStatuses = ['APPROVED', 'REJECTED'];
            if (!status || !validReviewStatuses.includes(status.toUpperCase())) {
                return this.sendBadRequestResponse(res, `Invalid review status. Must be one of: ${validReviewStatuses.join(', ')}`);
            }

            const result = await productService.reviewProduct(id, status.toUpperCase(), reviewData);
            
            // If the service returns the standard format, pass it through
            if (result && result.status !== undefined && result.data !== undefined) {
                return res.status(result.status).json(result);
            }
            
            return this.sendSuccessResponse(res, result, 'Product reviewed successfully');
        } catch (error) {
            console.error('[ProductController] Error in reviewProduct:', error.message);
            return this.handleAxiosError(res, error, `Error reviewing product with ID: ${req.params.id}`);
        }
    }

    /**
     * Search products by name, category, brand, or SKU
     * GET /api/products?search=value
     */
    async searchProducts(req, res) {
        try {
            const { search } = req.query;
            this.logRequest(req, `Searching products with value: ${search}`);
            
            // Input validation
            if (!search) {
                return this.sendBadRequestResponse(res, 'Search value is required');
            }

            const trimmedValue = search.trim();
            
            // Validate search value length
            if (!this.isValidSearchValue(trimmedValue)) {
                return this.createSearchValidationErrorResponse(res);
            }

            // Call the service
            const result = await productService.searchProducts(trimmedValue);
            
            // If the service returns the standard format, pass it through
            if (result && result.status !== undefined && result.data !== undefined) {
                return res.status(result.status).json(result);
            }
            
            // Otherwise, wrap in standard format
            const data = result || [];
            const message = data.length > 0 
                ? `Found ${data.length} product(s) matching search criteria`
                : `No products found matching search criteria: '${trimmedValue}'`;
                
            return this.sendSuccessResponse(res, data, message);
             
        } catch (error) {
            console.error('[ProductController] Error in searchProducts:', error.message);
            return this.handleAxiosError(res, error, `Error searching products with value: ${req.query.search}`);
        }
    }

    /**
     * Get products by category name
     * GET /api/products/categories/:categoryName
     */
    async getProductsByCategoryName(req, res) {
        try {
            const { categoryName } = req.params;
            this.logRequest(req, `Getting products by category: ${categoryName}`);
            
            if (!categoryName || categoryName.trim().length === 0) {
                return this.sendBadRequestResponse(res, 'Category name is required');
            }

            const result = await productService.getProductsByCategoryName(categoryName);
            
            // If the service returns the standard format, pass it through
            if (result && result.status !== undefined && result.data !== undefined) {
                return res.status(result.status).json(result);
            }
            
            return this.sendSuccessResponse(res, result, 'Products retrieved by category successfully');
        } catch (error) {
            console.error('[ProductController] Error in getProductsByCategoryName:', error.message);
            return this.handleAxiosError(res, error, `Error fetching products by category: ${req.params.categoryName}`);
        }
    }

    /**
     * Health check - proxy to product service health check
     */
    async healthCheck(req, res) {
        try {
            console.log('[ProductController] Performing health check');
            const result = await productService.healthCheck();
            
            return this.sendSuccessResponse(res, result, 'Product service is healthy');
        } catch (error) {
            console.error('[ProductController] Health check failed:', error.message);
            return this.sendServiceUnavailableResponse(res, 'Product service is unhealthy');
        }
    }
}

// Create an instance and export the methods
const productController = new ProductController();

module.exports = {
    getAllApprovedProducts: (req, res) => productController.getAllApprovedProducts(req, res),
    getAllProducts: (req, res) => productController.getAllProducts(req, res),
    getProductById: (req, res) => productController.getProductById(req, res),
    createProduct: (req, res) => productController.createProduct(req, res),
    updateProduct: (req, res) => productController.updateProduct(req, res),
    deleteProduct: (req, res) => productController.deleteProduct(req, res),
    getProductsByStatus: (req, res) => productController.getProductsByStatus(req, res),
    reviewProduct: (req, res) => productController.reviewProduct(req, res),
    searchProducts: (req, res) => productController.searchProducts(req, res),
    getProductsByCategoryName: (req, res) => productController.getProductsByCategoryName(req, res),
    healthCheck: (req, res) => productController.healthCheck(req, res)
};
