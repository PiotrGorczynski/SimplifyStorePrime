package com.example.simplifyStorePrime.service;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RateLimiterService {

    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    public Bucket resolveLoginBucket(String key) {
        return buckets.computeIfAbsent("login:" + key, k -> createBucket(5, Duration.ofMinutes(1)));
    }

    public Bucket resolveRegisterBucket(String key) {
        return buckets.computeIfAbsent("register:" + key, k -> createBucket(3, Duration.ofMinutes(1)));
    }

    public Bucket resolveForgotPasswordBucket(String key) {
        return buckets.computeIfAbsent("forgot:" + key, k -> createBucket(3, Duration.ofHours(1)));
    }

    public Bucket resolveResetPasswordBucket(String key) {
        return buckets.computeIfAbsent("reset:" + key, k -> createBucket(5, Duration.ofMinutes(1)));
    }

    private Bucket createBucket(int tokens, Duration period) {
        Bandwidth limit = Bandwidth.classic(tokens, Refill.intervally(tokens, period));
        return Bucket.builder().addLimit(limit).build();
    }
}