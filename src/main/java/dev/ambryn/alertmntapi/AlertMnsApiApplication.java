package dev.ambryn.alertmntapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.web.bind.annotation.RequestMapping;

@SpringBootApplication
public class AlertMnsApiApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(AlertMnsApiApplication.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(AlertMnsApiApplication.class);
    }
}
