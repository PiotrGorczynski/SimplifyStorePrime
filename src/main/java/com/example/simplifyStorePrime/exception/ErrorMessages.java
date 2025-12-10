package com.example.simplifyStorePrime.exception;

import lombok.NoArgsConstructor;
import lombok.AccessLevel;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ErrorMessages {
    public static final String CUSTOMER_NOT_FOUND = "Customer not found";
    public static final String PRODUCT_NOT_FOUND = "Product not found";
    public static final String TRANSACTION_NOT_FOUND = "Transaction not found";
}
