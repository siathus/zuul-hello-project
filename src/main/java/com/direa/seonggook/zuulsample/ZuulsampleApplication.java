package com.direa.seonggook.zuulsample;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.boot.web.servlet.ServletComponentScan;

import java.util.Collections;

@ServletComponentScan
@SpringBootApplication
public class ZuulsampleApplication {

    public static void main(String[] args) {
        // Archaius를 통해 외부에 존재하는 properties 파일을 가져오기 위한 System Property 설정
//        System.setProperty("archaius.configurationSource.additionalUrls", "file:///C:/Users/DIR-P-42/Desktop/임성국/workspace_intellij/zuul-hello-project/props/application.properties");
//        System.setProperty("archaius.configurationSource.additionalUrls", "file:///C:/Users/DIR-P-42/Documents/application.properties");
//        System.setProperty("archaius.fixedDelayPollingScheduler.initialDelayMills", "10000");
//        System.setProperty("archaius.fixedDelayPollingScheduler.delayMills", "3000");

        SpringApplication app = new SpringApplication(ZuulsampleApplication.class);
        app.setDefaultProperties(Collections.singletonMap("server.port", 8101));
        app.run(args);
    }
}
