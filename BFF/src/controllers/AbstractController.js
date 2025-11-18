/**
 * Abstract base controller for standardized API responses across the BFF
 * Mirrors the backend abstract controller pattern for consistency
 */
class AbstractController {
    
    // Constants to avoid code duplication and ensure consistency
    static STATUS_KEY = 'status';
    static MESSAGE_KEY = 'message';
    static DATA_KEY = 'data';
    static TIMESTAMP_KEY = 'timestamp';
    static SERVICE_KEY = 'service';

    /**
     * Health check endpoint - can be overridden by specific controllers
     */
    healthCheck(req, res) {
        return res.status(200).json({
            [AbstractController.STATUS_KEY]: 'UP',
            [AbstractController.SERVICE_KEY]: 'BFF Service',
            [AbstractController.TIMESTAMP_KEY]: new Date().toISOString()
        });
    }

    /**
     * Send success response (200 OK)
     */
    sendSuccessResponse(res, data, message) {
        const result = {};
        result[AbstractController.STATUS_KEY] = 200;
        result[AbstractController.MESSAGE_KEY] = message;
        
        if (data !== null && data !== undefined) {
            result[AbstractController.DATA_KEY] = data;
        }
        
        return res.status(200).json(result);
    }

    /**
     * Send created response (201 Created)
     */
    sendCreatedResponse(res, data, message) {
        const result = {
            [AbstractController.STATUS_KEY]: 201,
            [AbstractController.MESSAGE_KEY]: message,
            [AbstractController.DATA_KEY]: data
        };
        return res.status(201).json(result);
    }

    /**
     * Send accepted response (202 Accepted)
     */
    sendAcceptedResponse(res, data, message) {
        const result = {
            [AbstractController.STATUS_KEY]: 202,
            [AbstractController.MESSAGE_KEY]: message,
            [AbstractController.DATA_KEY]: data
        };
        return res.status(202).json(result);
    }

    /**
     * Send no content response (204 No Content)
     */
    sendNoContentResponse(res, message) {
        const result = {
            [AbstractController.STATUS_KEY]: 204,
            [AbstractController.MESSAGE_KEY]: message
        };
        return res.status(204).json(result);
    }

    /**
     * Send bad request response (400 Bad Request)
     */
    sendBadRequestResponse(res, message) {
        const result = {
            [AbstractController.STATUS_KEY]: 400,
            [AbstractController.MESSAGE_KEY]: message
        };
        return res.status(400).json(result);
    }

    /**
     * Send unauthorized response (401 Unauthorized)
     */
    sendUnauthorizedResponse(res, message) {
        const result = {
            [AbstractController.STATUS_KEY]: 401,
            [AbstractController.MESSAGE_KEY]: message
        };
        return res.status(401).json(result);
    }

    /**
     * Send forbidden response (403 Forbidden)
     */
    sendForbiddenResponse(res, message) {
        const result = {
            [AbstractController.STATUS_KEY]: 403,
            [AbstractController.MESSAGE_KEY]: message
        };
        return res.status(403).json(result);
    }

    /**
     * Send not found response (404 Not Found)
     */
    sendNotFoundResponse(res, message) {
        const result = {
            [AbstractController.STATUS_KEY]: 404,
            [AbstractController.MESSAGE_KEY]: message
        };
        return res.status(404).json(result);
    }

    /**
     * Send conflict response (409 Conflict)
     */
    sendConflictResponse(res, message) {
        const result = {
            [AbstractController.STATUS_KEY]: 409,
            [AbstractController.MESSAGE_KEY]: message
        };
        return res.status(409).json(result);
    }

    /**
     * Send internal server error response (500 Internal Server Error)
     */
    sendInternalServerErrorResponse(res, message) {
        const result = {
            [AbstractController.STATUS_KEY]: 500,
            [AbstractController.MESSAGE_KEY]: message
        };
        return res.status(500).json(result);
    }

    /**
     * Send service unavailable response (503 Service Unavailable)
     */
    sendServiceUnavailableResponse(res, message) {
        const result = {
            [AbstractController.STATUS_KEY]: 503,
            [AbstractController.MESSAGE_KEY]: message
        };
        return res.status(503).json(result);
    }

    /**
     * Create error response with specific status
     */
    createErrorResponse(res, statusCode, message) {
        const result = {
            [AbstractController.STATUS_KEY]: statusCode,
            [AbstractController.MESSAGE_KEY]: message
        };
        return res.status(statusCode).json(result);
    }

    /**
     * Helper method for input validation - search value
     */
    isValidSearchValue(searchValue) {
        return searchValue !== null && 
               searchValue !== undefined &&
               searchValue.trim() !== '' && 
               searchValue.trim().length >= 2 && 
               searchValue.trim().length <= 100;
    }

    /**
     * Helper method for input validation - customer ID
     */
    isValidCustomerId(customerId) {
        return customerId !== null && 
               customerId !== undefined &&
               customerId.trim() !== '' && 
               customerId.trim().length >= 1 && 
               customerId.trim().length <= 50;
    }

    /**
     * Helper method for input validation - SKU code
     */
    isValidSkuCode(skuCode) {
        return skuCode !== null && 
               skuCode !== undefined &&
               skuCode.trim() !== '' && 
               skuCode.trim().length >= 2 && 
               skuCode.trim().length <= 50;
    }

    /**
     * Helper method for input validation - cart ID
     */
    isValidCartId(cartId) {
        return cartId !== null && 
               cartId !== undefined && 
               !isNaN(cartId) && 
               parseInt(cartId) > 0;
    }

    /**
     * Helper method for input validation - product ID
     */
    isValidProductId(productId) {
        return productId !== null && 
               productId !== undefined && 
               !isNaN(productId) && 
               parseInt(productId) > 0;
    }

    /**
     * Create validation error response for search
     */
    createSearchValidationErrorResponse(res) {
        return this.sendBadRequestResponse(res, 'Search value must be between 2 and 100 characters long');
    }

    /**
     * Create validation error response for general validation
     */
    createValidationErrorResponse(res, field, requirement) {
        return this.sendBadRequestResponse(res, `${field} ${requirement}`);
    }

    /**
     * Handle axios errors and map to appropriate HTTP responses
     */
    handleAxiosError(res, error, context) {
        console.error(`[BFF] ${context}:`, {
            message: error.message,
            status: error.response?.status,
            data: error.response?.data
        });

        if (error.response) {
            // The request was made and the server responded with a status code
            const status = error.response.status;
            const errorData = error.response.data;
            
            // If the backend returns our standard format, pass it through
            if (errorData && typeof errorData === 'object' && errorData.status && errorData.message) {
                return res.status(status).json(errorData);
            }
            
            // Otherwise, create a standard error response
            let message = 'An error occurred';
            if (errorData && typeof errorData === 'object' && errorData.message) {
                message = errorData.message;
            } else if (typeof errorData === 'string') {
                message = errorData;
            } else if (error.message) {
                message = error.message;
            }
            
            return this.createErrorResponse(res, status, message);
        } else if (error.request) {
            // The request was made but no response was received
            console.error('[BFF] No response received:', error.request);
            return this.sendServiceUnavailableResponse(res, 'Service temporarily unavailable');
        } else {
            // Something happened in setting up the request
            console.error('[BFF] Request setup error:', error.message);
            return this.sendInternalServerErrorResponse(res, 'Internal server error');
        }
    }

    /**
     * Log request details for debugging
     */
    logRequest(req, context) {
        console.log(`[BFF] ${context} - ${req.method} ${req.originalUrl}`, {
            params: req.params,
            query: req.query,
            body: req.body ? Object.keys(req.body) : undefined
        });
    }
}

module.exports = AbstractController;