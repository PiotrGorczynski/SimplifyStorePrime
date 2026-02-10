package com.example.simplifyStorePrime.seeder;

import com.example.simplifyStorePrime.entity.Customer;
import com.example.simplifyStorePrime.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.Random;

@Component
@Profile("perf")
@RequiredArgsConstructor
public class PerformanceTest implements CommandLineRunner {
    private final CustomerRepository customerRepository;
    private final Random random = new Random();

    @Override
    public void run(String... args) {
        System.out.println("=== SIMPLIFY STORE PRIME — Performance Tests ===\n");

        testSelectAll();
        testSelectById();
        testInsertSingle();
        testUpdateSingle();
        testDeleteSingle();
        testSearchByPhrase();

        System.out.println("\n=== Tests completed ===");
    }

    private void testSelectAll() {
        System.out.println("--- SELECT ALL customers ---");
        long start = System.currentTimeMillis();

        List<Customer> customers = customerRepository.findAll();

        long elapsed = System.currentTimeMillis() - start;
        System.out.println("  Records: " + customers.size() + " | Time: " + elapsed + "ms\n");
    }

    private void testSelectById() {
        System.out.println("--- SELECT BY ID (x100 queries) ---");
        long start = System.currentTimeMillis();

        for (int i = 0; i < 100; i++) {
            Optional<Customer> c = customerRepository.findById(random.nextInt(1000) + 1);
        }

        long elapsed = System.currentTimeMillis() - start;
        System.out.println("  100 queries | Total: " + elapsed + "ms | Avg: " + (elapsed / 100.0) + "ms\n");
    }

    private void testInsertSingle() {
        System.out.println("--- INSERT single customer (x100) ---");
        long start = System.currentTimeMillis();

        for (int i = 0; i < 100; i++) {
            Customer c = Customer.builder()
                    .info("PerfTest User #" + i)
                    .salesOrders("order")
                    .invoices("yes")
                    .paymentHistory("no")
                    .communication("perftest" + i + System.currentTimeMillis() + "@test.com")
                    .category("business")
                    .feedback("Test feedback")
                    .notes("Test notes")
                    .supportRequest("None")
                    .build();
            customerRepository.save(c);
        }

        long elapsed = System.currentTimeMillis() - start;
        System.out.println("  100 inserts | Total: " + elapsed + "ms | Avg: " + (elapsed / 100.0) + "ms\n");
    }

    private void testUpdateSingle() {
        System.out.println("--- UPDATE single customer (x100) ---");
        long start = System.currentTimeMillis();

        for (int i = 0; i < 100; i++) {
            int id = random.nextInt(1000) + 1;
            Optional<Customer> opt = customerRepository.findById(id);
            if (opt.isPresent()) {
                Customer c = opt.get();
                c.setInfo("Updated PerfTest #" + i);
                c.setFeedback("Updated feedback #" + i);
                customerRepository.save(c);
            }
        }

        long elapsed = System.currentTimeMillis() - start;
        System.out.println("  100 updates | Total: " + elapsed + "ms | Avg: " + (elapsed / 100.0) + "ms\n");
    }

    private void testDeleteSingle() {
        System.out.println("--- DELETE single customer (x100) ---");
        int[] ids = new int[100];
        for (int i = 0; i < 100; i++) {
            Customer c = Customer.builder()
                    .info("ToDelete #" + i)
                    .salesOrders("order")
                    .invoices("yes")
                    .paymentHistory("no")
                    .communication("delete" + i + System.currentTimeMillis() + "@test.com")
                    .category("individual")
                    .feedback("f")
                    .notes("n")
                    .supportRequest("None")
                    .build();
            ids[i] = customerRepository.save(c).getId();
        }

        long start = System.currentTimeMillis();

        for (int id : ids) {
            customerRepository.deleteById(id);
        }

        long elapsed = System.currentTimeMillis() - start;
        System.out.println("  100 deletes | Total: " + elapsed + "ms | Avg: " + (elapsed / 100.0) + "ms\n");
    }

    private void testSearchByPhrase() {
        System.out.println("--- SEARCH by phrase (x100) ---");
        String[] phrases = {"Adam", "business", "gmail", "bulk", "Nowak",
                "individual", "feedback", "order", "service", "detail"};

        long start = System.currentTimeMillis();

        for (int i = 0; i < 100; i++) {
            String phrase = phrases[i % phrases.length];
            customerRepository.findAll().stream()
                    .filter(c -> c.getInfo().contains(phrase) ||
                            c.getCategory().contains(phrase) ||
                            c.getCommunication().contains(phrase))
                    .toList();
        }

        long elapsed = System.currentTimeMillis() - start;
        System.out.println("  100 searches | Total: " + elapsed + "ms | Avg: " + (elapsed / 100.0) + "ms\n");
    }
}