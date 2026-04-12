package com.example.simplifyStorePrime.commons;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class AppConstants {

    // ==================== ERROR MESSAGES ====================
    public static final String CUSTOMER_NOT_FOUND = "Customer not found";
    public static final String PRODUCT_NOT_FOUND = "Product not found";
    public static final String TRANSACTION_NOT_FOUND = "Transaction not found";
    public static final String DELIVERY_NOT_FOUND = "Delivery not found";
    public static final String DELIVERY_ALREADY_EXISTS = "Delivery already exists";
    public static final String NOT_ENOUGH_STOCK = "Not enough stock for product: ";
    public static final String USER_NOT_FOUND = "User not found";
    public static final String USERNAME_ALREADY_EXISTS = "Username already exists: ";
    public static final String INVALID_RESET_TOKEN = "Invalid or expired reset token";
    public static final String RESET_TOKEN_EXPIRED = "Reset token has expired. Please request a new one.";
    public static final String RESET_TOKEN_USED = "This reset token has already been used.";
    public static final String EMAIL_SEND_FAILED = "Failed to send email: ";
    public static final String EMAIL_REQUIRED = "Email is required";
    public static final String TOKEN_PASSWORD_REQUIRED = "Token and password (min 6 chars) are required";
    public static final String PASSWORD_RESET_SUCCESS = "Password has been reset successfully";
    public static final String PASSWORD_RESET_EMAIL_SENT = "If an account with that email exists, a reset link has been sent.";
    public static final String ERROR_NOT_FOUND = "Not Found";
    public static final String ERROR_CONFLICT = "Conflict";
    public static final String ERROR_INTERNAL = "Internal Server Error";

    // ==================== RATE LIMITING MESSAGES ====================
    public static final String TOO_MANY_REGISTRATIONS = "Too many registration attempts. Please try again later.";
    public static final String TOO_MANY_LOGINS = "Too many login attempts. Please try again in a minute.";
    public static final String TOO_MANY_FORGOT_PASSWORD = "Too many reset requests. Please try again later.";
    public static final String TOO_MANY_RESET_PASSWORD = "Too many reset attempts. Please try again in a minute.";

    // ==================== TRANSACTION TYPES ====================
    public static final String SALE = "sale";
    public static final String RETURN = "return";
    public static final String EXCHANGE = "exchange";
    public static final String REFUND = "refund";

    // ==================== JWT / AUTH ====================
    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String BEARER_PREFIX = "Bearer ";
    public static final String BEARER_SCHEME = "bearer";
    public static final String BEARER_FORMAT = "JWT";
    public static final String BEARER_AUTH = "Bearer Authentication";

    // ==================== HTTP HEADERS ====================
    public static final String X_FORWARDED_FOR = "X-Forwarded-For";

    // ==================== HTTP METHODS ====================
    public static final String METHOD_GET = "GET";
    public static final String METHOD_POST = "POST";
    public static final String METHOD_PUT = "PUT";
    public static final String METHOD_DELETE = "DELETE";
    public static final String METHOD_OPTIONS = "OPTIONS";

    // ==================== REQUEST KEYS ====================
    public static final String REQUEST_EMAIL = "email";
    public static final String REQUEST_TOKEN = "token";
    public static final String REQUEST_NEW_PASSWORD = "newPassword";

    // ==================== DEFAULT VALUES ====================
    public static final String DEFAULT_PROVIDER = "Default Provider";
    public static final String DEFAULT_DELIVERY_TYPE = "standard";
    public static final String DEFAULT_DELIVERY_STATUS = "pending";

    // ==================== ROLES ====================
    public static final String DEFAULT_ROLE = "USER";
    public static final String ROLE_ADMIN = "ADMIN";
    public static final String ROLE_USER_DEFAULT = "ROLE_USER";
    public static final String CLAIM_ROLE = "role";

    // ==================== SECURITY URL PATTERNS ====================
    public static final String AUTH_ENDPOINTS = "/api/auth/**";
    public static final String SWAGGER_UI_PATTERN = "/swagger-ui/**";
    public static final String API_DOCS_PATTERN = "/v3/api-docs/**";
    public static final String SWAGGER_HTML = "/swagger-ui.html";
    public static final String ALL_API_PATTERN = "/api/**";
    public static final String CORS_PATTERN = "/**";

    // ==================== CORS ====================
    public static final String CORS_LOCALHOST = "http://localhost:4200";
    public static final String CORS_RAILWAY = "https://simplifystoreprimefrontend-production.up.railway.app";
    public static final String CONTENT_TYPE_HEADER = "Content-Type";
    public static final String X_REQUESTED_WITH_HEADER = "X-Requested-With";

    // ==================== SWAGGER / API DOCS ====================
    public static final String API_TITLE = "Simplify Store Prime API";
    public static final String API_DESCRIPTION = "REST API for e-commerce customer management system — Master's thesis project";
    public static final String API_VERSION = "1.0.0";
    public static final String API_AUTHOR = "Piotr";

    // ==================== EMAIL ====================
    public static final String EMAIL_FROM = "noreply@simplifystore.com";
    public static final String EMAIL_SUBJECT = "Simplify Store Prime - Password Reset";
    public static final String EMAIL_ENCODING = "UTF-8";
    public static final String RESET_PASSWORD_PATH = "/reset-password?token=";

    // ==================== LOGGING MESSAGES ====================
    public static final String LOG_GETTING_CUSTOMER = "Getting customer with id: {}";
    public static final String LOG_CREATING_CUSTOMER = "Creating new customer: {}";
    public static final String LOG_UPDATING_CUSTOMER = "Updated customer with id: {}";
    public static final String LOG_DELETING_CUSTOMER = "Deleting customer with id: {}";
    public static final String LOG_RETURNED_ALL_CUSTOMERS = "Returned all customers from database";

    // ==================== RESPONSE KEYS ====================
    public static final String RESPONSE_MESSAGE = "message";
    public static final String RESPONSE_TIMESTAMP = "timestamp";
    public static final String RESPONSE_STATUS = "status";
    public static final String RESPONSE_ERROR = "error";

    // ==================== DATABASE ====================
    public static final String COLUMN_TEXT = "TEXT";
}