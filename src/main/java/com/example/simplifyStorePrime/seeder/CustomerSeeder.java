package com.example.simplifyStorePrime.seeder;

import com.example.simplifyStorePrime.entity.Customer;
import com.example.simplifyStorePrime.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Component
@Profile("seed")
@RequiredArgsConstructor
public class CustomerSeeder implements CommandLineRunner {

    private final CustomerRepository customerRepository;
    private final Random random = new Random();

    private final String[] categories = {"individual", "business", "retail chains", "wholesalers", "detail"};
    private final String[] orderTypes = {"service", "bulk", "order"};
    private final String[] yesNo = {"yes", "no"};

    private final String[] firstNames = {
            "Adam", "Anna", "Piotr", "Maria", "Jan", "Katarzyna", "Tomasz", "Agnieszka",
            "Michał", "Magdalena", "Krzysztof", "Aleksandra", "Paweł", "Monika", "Andrzej",
            "Joanna", "Marek", "Barbara", "Łukasz", "Ewa", "Robert", "Dorota", "Marcin",
            "Natalia", "Grzegorz", "Karolina", "Rafał", "Justyna", "Damian", "Patrycja"
    };

    private final String[] lastNames = {
            "Nowak", "Kowalski", "Wiśniewski", "Wójcik", "Kowalczyk", "Kamiński", "Lewandowski",
            "Zieliński", "Szymański", "Woźniak", "Dąbrowski", "Kozłowski", "Jankowski",
            "Mazur", "Kwiatkowski", "Krawczyk", "Piotrowski", "Grabowski", "Pawlak", "Michalski"
    };

    private final String[] domains = {
            "gmail.com", "outlook.com", "firma.pl", "biznes.pl", "mail.com", "company.com"
    };

    @Override
    public void run(String... args) {
        if (customerRepository.count() > 100) {
            System.out.println("Database already seeded. Skipping...");
            return;
        }

        System.out.println("Seeding 1000 customers...");
        long start = System.currentTimeMillis();

        List<Customer> customers = new ArrayList<>();
        for (int i = 1; i <= 1000; i++) {
            String firstName = firstNames[random.nextInt(firstNames.length)];
            String lastName = lastNames[random.nextInt(lastNames.length)];
            String email = (firstName + "." + lastName + i + "@" + domains[random.nextInt(domains.length)]).toLowerCase();

            customers.add(Customer.builder()
                    .info(firstName + " " + lastName + " - Customer #" + i)
                    .salesOrders(orderTypes[random.nextInt(orderTypes.length)])
                    .invoices(yesNo[random.nextInt(yesNo.length)])
                    .paymentHistory(yesNo[random.nextInt(yesNo.length)])
                    .communication(email)
                    .category(categories[random.nextInt(categories.length)])
                    .feedback("Feedback from " + firstName + " " + lastName)
                    .notes("Notes for customer #" + i)
                    .supportRequest(random.nextBoolean() ? "Request #" + i + ": " + getRandomRequest() : "None")
                    .build());
        }

        customerRepository.saveAll(customers);
        long elapsed = System.currentTimeMillis() - start;

        System.out.println("Seeded 1000 customers in " + elapsed + "ms");
    }

    private String getRandomRequest() {
        String[] requests = {
                "Issue with invoice payment",
                "Delivery delay complaint",
                "Product return request",
                "Account update needed",
                "Pricing inquiry",
                "Bulk order discount request",
                "Technical support needed",
                "Shipping address change"
        };
        return requests[random.nextInt(requests.length)];
    }
}