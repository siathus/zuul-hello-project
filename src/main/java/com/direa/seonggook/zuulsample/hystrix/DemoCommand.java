package com.direa.seonggook.zuulsample.hystrix;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixThreadPoolKey;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class DemoCommand extends HystrixCommand<String> {

    private final String url;

    public DemoCommand(String url) {
        super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("demoGroup"))
        .andCommandKey(HystrixCommandKey.Factory.asKey("demo"))
        .andThreadPoolKey(HystrixThreadPoolKey.Factory.asKey("demoThreadPool")));
        this.url = url;
    }

    @Override
    protected String run() {
        RestTemplate restTemplate = new RestTemplate();
        try {
            String result = restTemplate.getForObject(url, String.class);
            System.out.println(result);
        } catch (Exception e) {
            throw new RuntimeException("DemoCommand 도중 예외 발생", e);
        }
        return "aa";
    }

    @Override
    protected String getFallback() {
        return "{}";
    }
}
