const AbstractController = require('./AbstractController');
const categoryService = require('../services/categories.service');

class CategoryController extends AbstractController {

    /**
     * Get all categories
     * GET /api/categories
     */
    async getAllCategories(req, res) {
        try {
            this.logRequest(req, 'Getting all categories');
            const result = await categoryService.getAllCategories();
            
            // If the service returns the standard format, pass it through
            if (result && result.status !== undefined && result.data !== undefined) {
                return res.status(result.status).json(result);
            }
            
            return this.sendSuccessResponse(res, result, 'Categories retrieved successfully');
        } catch (error) {
            console.error('[CategoryController] Error in getAllCategories:', error.message);
            return this.handleAxiosError(res, error, 'Error fetching categories');
        }
    }

    /**
     * Get category by ID
     * GET /api/categories/:id
     */
    async getCategoryById(req, res) {
        try {
            const { id } = req.params;
            this.logRequest(req, `Getting category by ID: ${id}`);
            
            if (!this.isValidProductId(id)) { // Reusing product ID validation since it's the same logic
                return this.sendBadRequestResponse(res, 'Invalid category ID provided. Must be a positive number.');
            }

            const result = await categoryService.getCategoryById(id);
            
            // If the service returns the standard format, pass it through
            if (result && result.status !== undefined && result.data !== undefined) {
                return res.status(result.status).json(result);
            }
            
            return this.sendSuccessResponse(res, result, 'Category retrieved successfully');
        } catch (error) {
            console.error(`[CategoryController] Error in getCategoryById:`, error.message);
            return this.handleAxiosError(res, error, `Error fetching category with ID: ${req.params.id}`);
        }
    }

    /**
     * Create a new category
     * POST /api/categories
     */
    async createCategory(req, res) {
        try {
            const categoryData = req.body;
            this.logRequest(req, 'Creating new category');
            
            // Basic validation
            if (!categoryData.name) {
                return this.sendBadRequestResponse(res, 'Category name is required');
            }

            if (categoryData.name.trim().length < 2 || categoryData.name.trim().length > 100) {
                return this.sendBadRequestResponse(res, 'Category name must be between 2 and 100 characters');
            }

            const result = await categoryService.createCategory(categoryData);
            
            // If the service returns the standard format, pass it through
            if (result && result.status !== undefined && result.data !== undefined) {
                return res.status(result.status).json(result);
            }
            
            return this.sendCreatedResponse(res, result, 'Category created successfully');
        } catch (error) {
            console.error('[CategoryController] Error in createCategory:', error.message);
            return this.handleAxiosError(res, error, 'Error creating category');
        }
    }

    /**
     * Update a category
     * PUT /api/categories/:id
     */
    async updateCategory(req, res) {
        try {
            const { id } = req.params;
            const categoryData = req.body;
            this.logRequest(req, `Updating category with ID: ${id}`);
            
            if (!this.isValidProductId(id)) { // Reusing product ID validation
                return this.sendBadRequestResponse(res, 'Invalid category ID provided. Must be a positive number.');
            }

            // Basic validation
            if (!categoryData.name) {
                return this.sendBadRequestResponse(res, 'Category name is required');
            }

            if (categoryData.name.trim().length < 2 || categoryData.name.trim().length > 100) {
                return this.sendBadRequestResponse(res, 'Category name must be between 2 and 100 characters');
            }

            const result = await categoryService.updateCategory(id, categoryData);
            
            // If the service returns the standard format, pass it through
            if (result && result.status !== undefined && result.data !== undefined) {
                return res.status(result.status).json(result);
            }
            
            return this.sendSuccessResponse(res, result, 'Category updated successfully');
        } catch (error) {
            console.error(`[CategoryController] Error in updateCategory:`, error.message);
            return this.handleAxiosError(res, error, `Error updating category with ID: ${req.params.id}`);
        }
    }

    /**
     * Delete a category
     * DELETE /api/categories/:id
     */
    async deleteCategory(req, res) {
        try {
            const { id } = req.params;
            this.logRequest(req, `Deleting category with ID: ${id}`);
            
            if (!this.isValidProductId(id)) { // Reusing product ID validation
                return this.sendBadRequestResponse(res, 'Invalid category ID provided. Must be a positive number.');
            }

            const result = await categoryService.deleteCategory(id);
            
            // If the service returns the standard format, pass it through
            if (result && result.status !== undefined) {
                return res.status(result.status).json(result);
            }
            
            return this.sendSuccessResponse(res, null, 'Category deleted successfully');
        } catch (error) {
            console.error(`[CategoryController] Error in deleteCategory:`, error.message);
            return this.handleAxiosError(res, error, `Error deleting category with ID: ${req.params.id}`);
        }
    }

    /**
     * Health check - proxy to category service health check
     */
    async healthCheck(req, res) {
        try {
            console.log('[CategoryController] Performing health check');
            const result = await categoryService.healthCheck();
            
            return this.sendSuccessResponse(res, result, 'Category service is healthy');
        } catch (error) {
            console.error('[CategoryController] Health check failed:', error.message);
            return this.sendServiceUnavailableResponse(res, 'Category service is unhealthy');
        }
    }
}

// Create an instance and export the methods
const categoryController = new CategoryController();

module.exports = {
    getAllCategories: (req, res) => categoryController.getAllCategories(req, res),
    getCategoryById: (req, res) => categoryController.getCategoryById(req, res),
    createCategory: (req, res) => categoryController.createCategory(req, res),
    updateCategory: (req, res) => categoryController.updateCategory(req, res),
    deleteCategory: (req, res) => categoryController.deleteCategory(req, res),
    healthCheck: (req, res) => categoryController.healthCheck(req, res)
};
