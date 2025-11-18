const express = require('express');
const router = express.Router();
const categoriesController = require('../controllers/categories.controller');

// Health check endpoint
router.get('/health', categoriesController.healthCheck);

// Categories CRUD operations
// GET /api/categories - Get all categories
router.get('/', categoriesController.getAllCategories);

// POST /api/categories - Create new category
router.post('/', categoriesController.createCategory);

// GET /api/categories/:id - Get category by ID
router.get('/:id', categoriesController.getCategoryById);

// PUT /api/categories/:id - Update category
router.put('/:id', categoriesController.updateCategory);

// DELETE /api/categories/:id - Delete category
router.delete('/:id', categoriesController.deleteCategory);

module.exports = router;
