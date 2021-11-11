package uz.devops.rpc4rj.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "uz.devops")
public class Rpc4rjApplication {

    public static void main(String[] args) {
        SpringApplication.run(Rpc4rjApplication.class, args);
    }

}
