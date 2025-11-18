const express = require('express');
const router = express.Router();
const inventoryController = require('../controllers/inventory.controller');

// Health check endpoint
router.get('/health', inventoryController.healthCheck);

// Get all inventories
router.get('/', inventoryController.getAllInventories);

// Create inventory for a product
router.post('/', inventoryController.createInventoryForProduct);

// Check if inventory exists for a SKU
router.get('/:sku/exists', inventoryController.checkInventoryExists);

// Get inventory by SKU
router.get('/:sku', inventoryController.getInventoryBySku);

// Update inventory quantity
router.put('/:sku', inventoryController.updateInventoryQuantity);

module.exports = router;
