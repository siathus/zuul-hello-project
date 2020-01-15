package com.direa.seonggook.zuulsample.hystrix;

import com.netflix.hystrix.*;
import org.springframework.web.client.RestTemplate;

public class ServiceHystrixCommand extends HystrixCommand<String> {

    private final String url;

    public ServiceHystrixCommand(String url) {
        super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("demoGroup"))
        .andCommandKey(HystrixCommandKey.Factory.asKey("demo"))
        .andThreadPoolKey(HystrixThreadPoolKey.Factory.asKey("demoThreadPool")));
        this.url = url;
    }

    @Override
    protected String run() {
        RestTemplate restTemplate = new RestTemplate();
        String result = null;
        try {
            result = restTemplate.getForObject(url, String.class);
        } catch (Exception e) {
            throw new RuntimeException("DemoCommand 도중 예외 발생", e);
        }
        return result;
    }

    @Override
    protected String getFallback() {
        return "Service 호출 실패 => Fallback 메소드 실행";
    }
}
