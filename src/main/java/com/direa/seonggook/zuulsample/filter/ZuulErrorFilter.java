package com.direa.seonggook.zuulsample.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;

public class ZuulErrorFilter extends ZuulFilter {
    @Override
    public String filterType() {
        return "error";
    }

    @Override
    public int filterOrder() {
        return 500;
    }

    @Override
    public boolean shouldFilter() {
        RequestContext ctx = RequestContext.getCurrentContext();

        // response의 status code가 500일 때 Error filter 실행
        if (ctx.getResponseStatusCode() == 500) {
            return true;
        }
        return false;
    }

    @Override
    public Object run() {
        System.out.println("================== 500 Internal Server ERROR FILTER 실행 ================");
        System.out.println("================== 500 Internal Server ERROR FILTER 끝 ================");

        return null;
    }
}
