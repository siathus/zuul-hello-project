package com.direa.seonggook.zuulsample;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

import java.util.Collections;

@ServletComponentScan
@SpringBootApplication
public class ZuulsampleApplication {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(ZuulsampleApplication.class);
        app.setDefaultProperties(Collections.singletonMap("server.port", 8101));
        app.run(args);
    }

}
