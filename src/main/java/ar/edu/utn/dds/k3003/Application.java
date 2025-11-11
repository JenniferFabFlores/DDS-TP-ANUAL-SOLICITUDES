package ar.edu.utn.dds.k3003;

import org.springframework.beans.factory.annotation.Value;
import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {

    @Value("${spring.datasource.url:NOT_FOUND}")
    private String jdbcUrl;

    @PostConstruct
    public void printConfig() {
        System.out.println("=== DEBUG DB CONFIG ===");
        System.out.println("spring.datasource.url = " + jdbcUrl);
        System.out.println("DB_HOST = " + System.getenv("DB_HOST"));
        System.out.println("DB_PORT = " + System.getenv("DB_PORT"));
        System.out.println("DB_NAME = " + System.getenv("DB_NAME"));
        System.out.println("DB_USER = " + System.getenv("DB_USER"));
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
