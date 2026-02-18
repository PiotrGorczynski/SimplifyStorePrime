package com.example.simplifyStorePrime.service;

import io.github.bucket4j.Bucket;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RateLimiterServiceTest {

    private RateLimiterService rateLimiterService;

    @BeforeEach
    void setUp() {
        rateLimiterService = new RateLimiterService();
    }

    @Test
    void loginAttempt_withinLimit_shouldAllow() {
        Bucket bucket = rateLimiterService.resolveLoginBucket("192.168.1.1");

        for (int i = 0; i < 5; i++) {
            assertTrue(bucket.tryConsume(1),
                    "Attempt " + (i + 1) + " should be allowed");
        }
    }

    @Test
    void loginAttempt_exceedsLimit_shouldBlock() {
        Bucket bucket = rateLimiterService.resolveLoginBucket("192.168.1.2");

        for (int i = 0; i < 5; i++) {
            bucket.tryConsume(1);
        }

        assertFalse(bucket.tryConsume(1));
    }

    @Test
    void registerAttempt_withinLimit_shouldAllow() {
        Bucket bucket = rateLimiterService.resolveRegisterBucket("192.168.1.3");

        for (int i = 0; i < 3; i++) {
            assertTrue(bucket.tryConsume(1),
                    "Attempt " + (i + 1) + " should be allowed");
        }
    }

    @Test
    void registerAttempt_exceedsLimit_shouldBlock() {
        Bucket bucket = rateLimiterService.resolveRegisterBucket("192.168.1.4");

        for (int i = 0; i < 3; i++) {
            bucket.tryConsume(1);
        }

        assertFalse(bucket.tryConsume(1));
    }

    @Test
    void differentIPs_shouldHaveSeparateLimits() {
        Bucket bucket1 = rateLimiterService.resolveLoginBucket("10.0.0.1");
        Bucket bucket2 = rateLimiterService.resolveLoginBucket("10.0.0.2");

        for (int i = 0; i < 5; i++) {
            bucket1.tryConsume(1);
        }

        assertTrue(bucket2.tryConsume(1));
        assertFalse(bucket1.tryConsume(1));
    }

    @Test
    void forgotPassword_withinLimit_shouldAllow() {
        Bucket bucket = rateLimiterService.resolveForgotPasswordBucket("192.168.1.5");

        for (int i = 0; i < 3; i++) {
            assertTrue(bucket.tryConsume(1),
                    "Attempt " + (i + 1) + " should be allowed");
        }
    }

    @Test
    void forgotPassword_exceedsLimit_shouldBlock() {
        Bucket bucket = rateLimiterService.resolveForgotPasswordBucket("192.168.1.6");

        for (int i = 0; i < 3; i++) {
            bucket.tryConsume(1);
        }

        assertFalse(bucket.tryConsume(1));
    }
}