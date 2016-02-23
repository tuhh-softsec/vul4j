package com.example;

import java.util.stream.Stream;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 * https://github.com/joshlong/java-mag-boot-article
 */
@SpringBootApplication
public class ReservationServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ReservationServiceApplication.class, args);
    }

    @Bean
    CommandLineRunner runner(ReservationRepository rr) {
        return args -> Stream.of("Julia", "Mia", "Phil", "Dave", "Pieter", "Bridget", "Stephane", "Josh", "Jenniffer")
                .forEach((n -> rr.save(new Reservation(n))));
    }
}
