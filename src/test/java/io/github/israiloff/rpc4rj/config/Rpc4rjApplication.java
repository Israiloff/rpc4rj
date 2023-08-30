package io.github.israiloff.rpc4rj.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "io.github.israiloff.rpc4rj")
public class Rpc4rjApplication {

    public static void main(String[] args) {
        SpringApplication.run(Rpc4rjApplication.class, args);
    }

}
