package com.synkronos.ai;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

/**
 * Main application entry point for Synkronos AI Job Portal
 * 
 * @author Synkronos AI Team
 * @version 1.0.0
 */
@SpringBootApplication
@EnableMongoAuditing
public class SynkronosAiApplication {

    public static void main(String[] args) {
        SpringApplication.run(SynkronosAiApplication.class, args);
    }
}

