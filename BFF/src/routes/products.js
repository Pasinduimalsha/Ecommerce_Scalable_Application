const express = require('express');
const router = express.Router();
const productsController = require('../controllers/products.controller');

// Health check endpoint
router.get('/health', productsController.healthCheck);

// Product search endpoint (with query parameter - should be before /:id to avoid conflicts)
router.get('/', (req, res, next) => {
    // If search query parameter exists, handle search
    if (req.query.search) {
        return productsController.searchProducts(req, res);
    }
    // Otherwise, get all products (admin/data steward)
    return productsController.getAllProducts(req, res);
});

// Get approved products (for customers)
router.get('/approved', productsController.getAllApprovedProducts);

// Get products by status
router.get('/status/:status', productsController.getProductsByStatus);

// Get products by category name
router.get('/categories/:categoryName', productsController.getProductsByCategoryName);

// Product CRUD operations
router.get('/:id', productsController.getProductById);
router.post('/', productsController.createProduct);
router.put('/:id', productsController.updateProduct);
router.delete('/:id', productsController.deleteProduct);

// Product review endpoint (with query parameter for status)
router.put('/:id/review', productsController.reviewProduct);

module.exports = router;
