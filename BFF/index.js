require('dotenv').config();
const express = require('express');
const cors = require('cors');
const helmet = require('helmet');
const productsRouter = require('./src/routes/products');
const ordersRouter = require('./src/routes/orders');
const inventoryRouter = require('./src/routes/inventory');
const categoriesRouter = require('./src/routes/categories');

const app = express();
const PORT = process.env.PORT || 4000;

// Security middleware
app.use(helmet());

// CORS configuration
app.use(cors({
    origin: process.env.ALLOWED_ORIGINS ? process.env.ALLOWED_ORIGINS.split(',') : '*',
    methods: ['GET', 'POST', 'PUT', 'DELETE', 'PATCH'],
    allowedHeaders: ['Content-Type', 'Authorization']
}));

// Body parsing middleware
app.use(express.json({ limit: '10mb' }));
app.use(express.urlencoded({ extended: true, limit: '10mb' }));

// Request logging middleware
app.use((req, res, next) => {
    const start = Date.now();
    console.log(`[${new Date().toISOString()}] ${req.method} ${req.url} - Started`);
    
    res.on('finish', () => {
        const duration = Date.now() - start;
        console.log(`[${new Date().toISOString()}] ${req.method} ${req.url} - ${res.statusCode} - ${duration}ms`);
    });
    
    next();
});

// API Routes
app.use('/api/products', productsRouter);
app.use('/api/orders', ordersRouter);
app.use('/api/inventory', inventoryRouter);
app.use('/api/categories', categoriesRouter);

// Root endpoint
app.get('/', (req, res) => {
    res.json({
        service: 'E-commerce BFF',
        version: '1.0.0',
        status: 'running',
        timestamp: new Date().toISOString(),
        endpoints: {
            products: '/api/products',
            orders: '/api/orders',
            inventory: '/api/inventory',
            categories: '/api/categories',
            health: '/health'
        }
    });
});

// Health check endpoint with comprehensive status
app.get('/health', async (req, res) => {
    const health = {
        status: 'healthy',
        timestamp: new Date().toISOString(),
        uptime: process.uptime(),
        environment: process.env.NODE_ENV || 'development',
        services: {}
    };

    // Check product service health
    try {
        const axios = require('axios');
        const productServiceUrl = process.env.PRODUCT_SERVICE_URL?.replace('/api/v1/products', '') || 'http://localhost:8080';
        await axios.get(`${productServiceUrl}/health`, { timeout: 5000 });
        health.services.productService = 'healthy';
    } catch (error) {
        health.services.productService = 'unhealthy';
        health.status = 'degraded';
    }

    // Check order service health
    try {
        const axios = require('axios');
        const orderServiceUrl = process.env.ORDER_SERVICE_URL?.replace('/api/v1/order', '') || 'http://localhost:8083';
        await axios.get(`${orderServiceUrl}/health`, { timeout: 5000 });
        health.services.orderService = 'healthy';
    } catch (error) {
        health.services.orderService = 'unhealthy';
        health.status = 'degraded';
    }

    // Check inventory service health
    try {
        const axios = require('axios');
        const inventoryServiceUrl = process.env.INVENTORY_SERVICE_URL?.replace('/api/v1/inventory', '') || 'http://localhost:8082';
        await axios.get(`${inventoryServiceUrl}/health`, { timeout: 5000 });
        health.services.inventoryService = 'healthy';
    } catch (error) {
        health.services.inventoryService = 'unhealthy';
        health.status = 'degraded';
    }

    const statusCode = health.status === 'healthy' ? 200 : 503;
    res.status(statusCode).json(health);
});

// 404 handler
app.use('*', (req, res) => {
    res.status(404).json({
        status: 404,
        message: 'Endpoint not found',
        path: req.originalUrl,
        timestamp: new Date().toISOString()
    });
});

// Global error handler
app.use((error, req, res, next) => {
    console.error(`[${new Date().toISOString()}] Error in ${req.method} ${req.url}:`, {
        message: error.message,
        stack: error.stack,
        ...(error.response && { responseData: error.response.data })
    });

    // Don't send stack traces in production
    const isDevelopment = process.env.NODE_ENV !== 'production';
    
    res.status(error.status || 500).json({
        status: error.status || 500,
        message: error.message || 'Internal server error',
        timestamp: new Date().toISOString(),
        ...(isDevelopment && { stack: error.stack })
    });
});

// Graceful shutdown
process.on('SIGTERM', () => {
    console.log('[BFF] SIGTERM received. Shutting down gracefully...');
    process.exit(0);
});

process.on('SIGINT', () => {
    console.log('[BFF] SIGINT received. Shutting down gracefully...');
    process.exit(0);
});

// Start server
app.listen(PORT, () => {
    console.log(`[BFF] Server running on port ${PORT}`);
    console.log(`[BFF] Environment: ${process.env.NODE_ENV || 'development'}`);
    console.log(`[BFF] Product Service URL: ${process.env.PRODUCT_SERVICE_URL || 'http://localhost:8080/api/v1/products'}`);
    console.log(`[BFF] Order Service URL: ${process.env.ORDER_SERVICE_URL || 'http://localhost:8081/api/v1/order'}`);
    console.log(`[BFF] Inventory Service URL: ${process.env.INVENTORY_SERVICE_URL || 'http://localhost:8082/api/v1/inventory'}`);
});
