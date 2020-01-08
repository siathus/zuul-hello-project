package com.direa.seonggook.zuulsample;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.ServletContextListener;

@ServletComponentScan
@SpringBootApplication
public class ZuulsampleApplication {

    public static void main(String[] args) {
        SpringApplication.run(ZuulsampleApplication.class, args);
    }

}
