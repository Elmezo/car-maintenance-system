package com.carmaintenance;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Smart Car Maintenance Analytics System
 * Main Application Class
 * 
 * This application manages car maintenance records, tracks failures,
 * and uses machine learning to predict future maintenance needs.
 * 
 * @author Auto-Intellix Team
 * @version 1.0.0
 */
@SpringBootApplication
@EnableCaching
@EnableScheduling
@EnableTransactionManagement
public class CarMaintenanceApplication {

    public static void main(String[] args) {
        SpringApplication.run(CarMaintenanceApplication.class, args);
        System.out.println("🚗 Smart Car Maintenance Analytics System Started Successfully!");
        System.out.println("📖 API Documentation: http://localhost:8080/api/swagger-ui.html");
    }
}
