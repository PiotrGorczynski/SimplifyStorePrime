package com.example.simplifyStorePrime.seeder;

import com.example.simplifyStorePrime.entity.Product;
import com.example.simplifyStorePrime.repository.ProductRepository;
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
@Order(1)
public class ProductSeeder implements CommandLineRunner {

    private final ProductRepository productRepository;
    private final Random random = new Random();

    private final String[][] productData = {
            {"Laptop", "Electronics", "ELEC", "1500", "8500", "High-performance laptop"},
            {"Smartphone", "Electronics", "ELEC", "800", "5500", "Latest generation smartphone"},
            {"Tablet", "Electronics", "ELEC", "600", "3500", "Portable tablet device"},
            {"Monitor", "Electronics", "ELEC", "400", "4000", "HD/4K display monitor"},
            {"Keyboard", "Electronics", "ELEC", "50", "600", "Mechanical/membrane keyboard"},
            {"Mouse", "Electronics", "ELEC", "30", "350", "Ergonomic computer mouse"},
            {"Headphones", "Electronics", "ELEC", "80", "1200", "Wireless/wired headphones"},
            {"Printer", "Electronics", "ELEC", "200", "2500", "Laser/inkjet printer"},
            {"T-Shirt", "Clothing", "CLO", "25", "120", "Cotton t-shirt"},
            {"Jeans", "Clothing", "CLO", "60", "300", "Denim jeans"},
            {"Jacket", "Clothing", "CLO", "100", "800", "Winter/spring jacket"},
            {"Sneakers", "Clothing", "CLO", "80", "600", "Sport sneakers"},
            {"Dress Shirt", "Clothing", "CLO", "50", "250", "Formal dress shirt"},
            {"Hoodie", "Clothing", "CLO", "40", "200", "Casual hoodie"},
            {"Office Desk", "Furniture", "FURN", "300", "3000", "Ergonomic office desk"},
            {"Office Chair", "Furniture", "FURN", "200", "2500", "Adjustable office chair"},
            {"Bookshelf", "Furniture", "FURN", "100", "800", "Wooden bookshelf"},
            {"Filing Cabinet", "Furniture", "FURN", "80", "500", "Metal filing cabinet"},
            {"Coffee Table", "Furniture", "FURN", "60", "600", "Modern coffee table"},
            {"Notebook", "Office Supplies", "OFF", "5", "30", "Ruled/blank notebook"},
            {"Pen Set", "Office Supplies", "OFF", "8", "50", "Premium pen set"},
            {"Paper Ream", "Office Supplies", "OFF", "10", "40", "A4 printing paper 500 sheets"},
            {"Stapler", "Office Supplies", "OFF", "5", "25", "Heavy-duty stapler"},
            {"Binder", "Office Supplies", "OFF", "3", "15", "3-ring binder"},
            {"Protein Bar", "Food & Beverage", "FOOD", "3", "15", "High-protein snack bar"},
            {"Coffee Beans", "Food & Beverage", "FOOD", "15", "80", "Premium roasted coffee beans"},
            {"Green Tea", "Food & Beverage", "FOOD", "8", "40", "Organic green tea"},
            {"Energy Drink", "Food & Beverage", "FOOD", "3", "12", "Caffeinated energy drink"},
            {"USB Cable", "Accessories", "ACC", "5", "40", "USB-C/Lightning cable"},
            {"Phone Case", "Accessories", "ACC", "10", "60", "Protective phone case"},
            {"Screen Protector", "Accessories", "ACC", "5", "30", "Tempered glass screen protector"},
            {"Power Bank", "Accessories", "ACC", "30", "200", "Portable power bank"},
            {"Webcam", "Accessories", "ACC", "40", "300", "HD webcam for video calls"},
            {"Desk Lamp", "Accessories", "ACC", "20", "150", "LED desk lamp"},
    };

    private final String[] brands = {
            "ProTech", "UltraMax", "NovaStar", "PrimeLine", "CoreX",
            "ZenithPro", "AlphaGear", "NexGen", "TrueValue", "SwiftEdge"
    };

    private final String[] sizes = {"S", "M", "L", "XL", "Standard", "Compact", "Pro", "Mini"};

    @Override
    public void run(String... args) {
        if (productRepository.count() > 50) {
            System.out.println("Products already seeded. Skipping...");
            return;
        }

        System.out.println("Seeding 200 products...");
        long start = System.currentTimeMillis();

        List<Product> products = new ArrayList<>();
        int counter = 1;

        for (int round = 0; round < 6; round++) {
            for (String[] data : productData) {
                if (counter > 200) break;

                String brand = brands[random.nextInt(brands.length)];
                String size = sizes[random.nextInt(sizes.length)];
                String name = brand + " " + data[0] + " " + size;
                String code = data[2] + "-" + String.format("%04d", counter);
                double minPrice = Double.parseDouble(data[3]);
                double maxPrice = Double.parseDouble(data[4]);
                double price = Math.round((minPrice + random.nextDouble() * (maxPrice - minPrice)) * 100.0) / 100.0;
                int stock = random.nextInt(500) + 1;
                int minQty = random.nextInt(10) + 1;

                products.add(Product.builder()
                        .name(name)
                        .code(code)
                        .category(data[1])
                        .price(price)
                        .stock(stock)
                        .description(data[5] + " by " + brand + ". Model: " + size + " edition.")
                        .notes(stock < 20 ? "Low stock - reorder soon" : "Stock level OK")
                        .minQuantity(minQty)
                        .another(random.nextBoolean() ? "Featured product" : "Standard listing")
                        .build());

                counter++;
            }
        }

        productRepository.saveAll(products);
        long elapsed = System.currentTimeMillis() - start;
        System.out.println("Seeded " + products.size() + " products in " + elapsed + "ms");
    }
}