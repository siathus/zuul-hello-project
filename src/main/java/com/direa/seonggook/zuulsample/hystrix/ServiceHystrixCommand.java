package com.direa.seonggook.zuulsample.hystrix;

import com.netflix.hystrix.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class ServiceHystrixCommand extends HystrixCommand<ResponseEntity<String>> {

    private final String url;

    public ServiceHystrixCommand(String url) {
        super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("demoGroup"))
        .andCommandKey(HystrixCommandKey.Factory.asKey("demo"))
        .andThreadPoolKey(HystrixThreadPoolKey.Factory.asKey("demoThreadPool")));
        this.url = url;
    }

    @Override
    protected ResponseEntity<String> run() {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> responseEntity = null;
        try {

//            throw new RuntimeException("test");

            responseEntity = restTemplate.getForEntity(url, String.class);
            System.out.println("Response Body : " + responseEntity.getBody());

        } catch (Exception e) {
            throw new RuntimeException("DemoCommand 도중 예외 발생", e);
        }
        return responseEntity;
    }

    @Override
    protected ResponseEntity<String> getFallback() {
        // fallback 메소드 :  500 INTERNAL SERVER ERROR를 반환한다.
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}
