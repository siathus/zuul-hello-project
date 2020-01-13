package com.direa.seonggook.zuulsample.filter;

import com.netflix.zuul.ZuulFilter;

public class ZuulPostFilter extends ZuulFilter {
    @Override
    public String filterType() {
        return "post";
    }

    @Override
    public int filterOrder() {
        return 3;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() {
        System.out.println("======== POST FILTER 실행 =======");

        System.out.println("======== POST FILTER 끝 =======");
        return null;
    }
}
