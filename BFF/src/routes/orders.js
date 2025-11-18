const express = require('express');
const router = express.Router();
const ordersController = require('../controllers/orders.controller');

// Health check endpoint
router.get('/health', ordersController.healthCheck);

// Cart operations - following the backend cart API structure
// GET /api/orders/customer/:customerId - Get cart by customer ID
router.get('/customer/:customerId', ordersController.getCartByCustomerId);

// POST /api/orders - Create new cart
router.post('/', ordersController.createCart);

// GET /api/orders/:cartId - Get cart by ID
router.get('/:cartId', ordersController.getCartById);

// POST /api/orders/:cartId - Add item to cart
router.post('/:cartId', ordersController.addItemToCart);

// DELETE /api/orders/:cartId/:skuCode - Remove item from cart
router.delete('/:cartId/:skuCode', ordersController.removeItemFromCart);

// DELETE /api/orders/:cartId - Remove cart
router.delete('/:cartId', ordersController.removeCart);

module.exports = router;
