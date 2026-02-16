package com.example.simplifyStorePrime.seeder;

import com.example.simplifyStorePrime.entity.Delivery;
import com.example.simplifyStorePrime.entity.Transaction;
import com.example.simplifyStorePrime.repository.DeliveryRepository;
import com.example.simplifyStorePrime.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Component
@Profile("seed")
@RequiredArgsConstructor
@Order(3)
public class DeliverySeeder implements CommandLineRunner {

    private final DeliveryRepository deliveryRepository;
    private final TransactionRepository transactionRepository;
    private final Random random = new Random();

    private final String[] deliveryTypes = {"standard", "express", "next-day", "economy", "same-day", "pickup"};
    private final String[] deliveryStatuses = {"delivered", "in transit", "pending", "shipped", "returned", "processing"};
    private final String[] deliveryProviders = {
            "DHL Express", "InPost Paczkomaty", "DPD Polska", "UPS Standard",
            "FedEx International", "Poczta Polska", "GLS Poland",
            "Orlen Paczka", "Allegro One Box", "In-store pickup"
    };

    @Override
    public void run(String... args) {
        if (deliveryRepository.count() > 50) {
            System.out.println("Deliveries already seeded. Skipping...");
            return;
        }

        List<Transaction> transactions = transactionRepository.findAll();
        if (transactions.isEmpty()) {
            System.out.println("Cannot seed deliveries - no transactions found. Skipping...");
            return;
        }

        System.out.println("Seeding deliveries for transactions...");
        long start = System.currentTimeMillis();

        List<Delivery> deliveries = new ArrayList<>();

        for (Transaction transaction : transactions) {
            if (random.nextDouble() > 0.2) {
                String status = transaction.getStatus().equals("completed")
                        ? "delivered"
                        : deliveryStatuses[random.nextInt(deliveryStatuses.length)];

                deliveries.add(Delivery.builder()
                        .deliveryType(deliveryTypes[random.nextInt(deliveryTypes.length)])
                        .status(status)
                        .provider(deliveryProviders[random.nextInt(deliveryProviders.length)])
                        .transaction(transaction)
                        .build());
            }
        }

        deliveryRepository.saveAll(deliveries);
        long elapsed = System.currentTimeMillis() - start;
        System.out.println("Seeded " + deliveries.size() + " deliveries in " + elapsed + "ms");
    }
}