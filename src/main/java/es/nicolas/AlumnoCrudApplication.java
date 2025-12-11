package es.nicolas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
public class AlumnoCrudApplication {

    public static void main(String[] args) {
        SpringApplication.run(AlumnoCrudApplication.class, args);
    }

}
