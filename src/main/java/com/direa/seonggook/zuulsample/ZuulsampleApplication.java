package com.direa.seonggook.zuulsample;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@ServletComponentScan
@SpringBootApplication
public class ZuulsampleApplication {

    public static void main(String[] args) {
        SpringApplication.run(ZuulsampleApplication.class, args);
    }

}
