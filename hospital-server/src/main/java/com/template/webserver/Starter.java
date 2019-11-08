package com.template.webserver;

import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import java.util.logging.Logger;

import static org.springframework.boot.WebApplicationType.SERVLET;

/**
 * Our Spring Boot application.
 */
@SpringBootApplication(scanBasePackages = {"com.template.webserver","com.template.insurance"})
public class Starter {

    /**
     * Starts our Spring Boot application.
     */
    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(Starter.class);
        app.setBannerMode(Banner.Mode.OFF);
        app.setWebApplicationType(SERVLET);
        app.run(args);
    }
}