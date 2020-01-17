package com.direa.seonggook.zuulsample.filter;

import com.direa.seonggook.zuulsample.hystrix.ServiceHystrixCommand;
import com.netflix.hystrix.*;
import com.netflix.hystrix.strategy.properties.HystrixPropertiesCommandDefault;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.sun.org.apache.xpath.internal.operations.Bool;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.UriBuilderFactory;

public class SecondZuulRouterFilter extends ZuulFilter {

    @Override
    public String filterType() {
        return "route";
    }

    @Override
    public int filterOrder() {
        return 2000;
    }

    @Override
    public boolean shouldFilter() {
        RequestContext ctx = RequestContext.getCurrentContext();
        if (ctx.get("destinationUrl") != null) {
            return true;
        }
        return false;
    }

    @Override
    public Object run() {
        System.out.println("============== Second Route Filter Run ============");

        RequestContext ctx = RequestContext.getCurrentContext();
        String destinationUrl = (String) ctx.get("destinationUrl");

        System.out.println("destinationUrl : " + destinationUrl);

        // 실제 로직을 수행할 HystrixCommand 생성
        ServiceHystrixCommand serviceHystrixCommand = new ServiceHystrixCommand(destinationUrl);
        ResponseEntity<String> responseEntity = serviceHystrixCommand.execute();

        // fallback 메소드가 실행되어 500 Internal Server Error가 발생했을 때
        if (responseEntity.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR) {
            // 500 오류지만 클라이언트 화면에 띄우기 위해 200으로 설정
            ctx.setResponseStatusCode(200);
            ctx.setResponseBody("500 Internal Server Error !!");
        } else {
            // 요청이 정상적으로 처리되었을 때
            // 반환받은 값을 RequestContext의 ResponseBody에 설정
            ctx.setResponseBody(responseEntity.getBody());
        }

        System.out.println("============== Second Route Filter End ===============");
        return null;

        /*
        HystrixCommandProperties.Setter propertiesSetter = HystrixCommandProperties.Setter()
                .withCircuitBreakerEnabled(true)
                .withExecutionIsolationStrategy(HystrixCommandProperties.ExecutionIsolationStrategy.SEMAPHORE)
                .withExecutionTimeoutInMilliseconds(3 * 1000)
                .withFallbackEnabled(true)
                .withCircuitBreakerSleepWindowInMilliseconds(2 * 1000);

        HystrixCommandKey hystrixCommandKey = HystrixCommandKey.Factory.asKey("demo");
        HystrixCommandProperties properties = new HystrixPropertiesCommandDefault(hystrixCommandKey, propertiesSetter);
        HystrixCommandMetrics metrics = HystrixCommandMetrics.getInstance(hystrixCommandKey, HystrixCommandGroupKey.Factory.asKey("demoGroup"), properties);

        HystrixCircuitBreaker circuitBreaker = HystrixCircuitBreaker.HystrixCircuitBreakerImpl.Factory.getInstance(hystrixCommandKey, HystrixCommandGroupKey.Factory.asKey("demoGroup"), properties, metrics);

        ResponseEntity<String> responseEntity = null;
        // Hystrix를 통해 Service 로직 실행 및 결과 가져오기
        if (circuitBreaker.allowRequest()) {
            System.out.println("Circuit Breaker is Closed");
            // fallback 메소드 실행되어야함
            circuitBreaker.markNonSuccess();
            System.out.println(circuitBreaker.isOpen());
            try {
                Thread.sleep(4000);
                circuitBreaker.markSuccess();
                Thread.sleep(4000);
                System.out.println(circuitBreaker.isOpen());

                 responseEntity = serviceHystrixCommand.execute();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // fallback 메소드가 실행되어 500 Internal Server Error가 발생했을 때
            if (responseEntity.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR) {
                // 500 오류지만 클라이언트 화면에 띄우기 위해 200으로 설정
                ctx.setResponseStatusCode(200);
                ctx.setResponseBody("500 Internal Server Error 발생!!");
            } else {
                // 요청이 정상적으로 처리되었을 때
                // 반환받은 값을 RequestContext의 ResponseBody에 설정
                ctx.setResponseBody(responseEntity.getBody());
            }
        } else {
            System.out.println("Circuit Breaker is Opened");
            circuitBreaker.markSuccess();
            if (!circuitBreaker.isOpen()) {
                System.out.println("Circuit Breaker is not Opened");
            }
        }
         */
    }
}
