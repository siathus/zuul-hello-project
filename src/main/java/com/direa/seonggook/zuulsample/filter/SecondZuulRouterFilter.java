package com.direa.seonggook.zuulsample.filter;

import com.direa.seonggook.zuulsample.hystrix.ServiceHystrixCommand;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
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

        // Hystrix를 통해 Service 로직 실행 및 결과 가져오기
        ResponseEntity<String> responseEntity = new ServiceHystrixCommand(destinationUrl).execute();

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

        System.out.println("============== Second Route Filter End ===============");
        return null;
    }
}
