package com.github.dimka9910.redistest.app;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@Slf4j
@SpringBootApplication
@ComponentScan(basePackages = "com.github.dimka9910.redistest")
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
        log.info("v 1637");
    }
}
