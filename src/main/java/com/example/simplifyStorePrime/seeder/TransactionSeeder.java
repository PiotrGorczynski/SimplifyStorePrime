package com.example.simplifyStorePrime.seeder;

import com.example.simplifyStorePrime.entity.*;
import com.example.simplifyStorePrime.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Component
@Profile("seed")
@RequiredArgsConstructor
@Order(2)
public class TransactionSeeder implements CommandLineRunner {

    private final TransactionRepository transactionRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    private final AppUserRepository appUserRepository;
    private final Random random = new Random();

    private final String[] types = {"sale", "return", "exchange", "bulk order", "service"};
    private final String[] paymentMethods = {"cash", "credit card", "debit card", "bank transfer", "PayPal", "BLIK"};
    private final String[] statuses = {"completed", "pending", "cancelled", "processing", "refunded"};
    private final String[] providers = {
            "DHL", "InPost", "DPD", "UPS", "FedEx", "Poczta Polska",
            "GLS", "Orlen Paczka", "Allegro One", "In-store pickup"
    };

    @Override
    public void run(String... args) {
        if (transactionRepository.count() > 50) {
            System.out.println("Transactions already seeded. Skipping...");
            return;
        }

        List<Customer> customers = customerRepository.findAll();
        List<Product> products = productRepository.findAll();
        List<AppUser> users = appUserRepository.findAll();

        if (customers.isEmpty() || products.isEmpty() || users.isEmpty()) {
            System.out.println("Cannot seed transactions - missing customers, products, or users. Skipping...");
            return;
        }

        System.out.println("Seeding 500 transactions...");
        long start = System.currentTimeMillis();

        List<Transaction> transactions = new ArrayList<>();

        for (int i = 0; i < 500; i++) {
            Customer customer = customers.get(random.nextInt(customers.size()));
            AppUser employee = users.get(random.nextInt(users.size()));

            LocalDateTime date = LocalDateTime.now()
                    .minusDays(random.nextInt(365))
                    .minusHours(random.nextInt(24))
                    .minusMinutes(random.nextInt(60));

            String status = statuses[random.nextInt(statuses.length)];

            Transaction transaction = Transaction.builder()
                    .transactionDate(date)
                    .type(types[random.nextInt(types.length)])
                    .paymentMethod(paymentMethods[random.nextInt(paymentMethods.length)])
                    .status(status)
                    .provider(providers[random.nextInt(providers.length)])
                    .customer(customer)
                    .employee(employee)
                    .items(new ArrayList<>())
                    .build();

            int itemCount = random.nextInt(5) + 1;
            double total = 0.0;

            for (int j = 0; j < itemCount; j++) {
                Product product = products.get(random.nextInt(products.size()));
                int quantity = random.nextInt(10) + 1;
                double pricePerUnit = product.getPrice();

                TransactionItem item = TransactionItem.builder()
                        .transaction(transaction)
                        .product(product)
                        .quantity(quantity)
                        .pricePerUnit(pricePerUnit)
                        .build();

                transaction.getItems().add(item);
                total += pricePerUnit * quantity;
            }

            transaction.setTotal(Math.round(total * 100.0) / 100.0);
            transactions.add(transaction);
        }

        transactionRepository.saveAll(transactions);
        long elapsed = System.currentTimeMillis() - start;
        System.out.println("Seeded 500 transactions in " + elapsed + "ms");
    }
}