package com.direa.seonggook.zuulsample.filter;

import com.netflix.zuul.ZuulFilter;

public class ZuulErrorFilter extends ZuulFilter {
    @Override
    public String filterType() {
        return "error";
    }

    @Override
    public int filterOrder() {
        return 0;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() {
        System.out.println("================== ERROR FILTER 실행 ================");
        System.out.println("================== ERROR FILTER 끝 ================");

        return null;
    }
}
