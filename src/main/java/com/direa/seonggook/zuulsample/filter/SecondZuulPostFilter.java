package com.direa.seonggook.zuulsample.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.exception.ZuulException;

public class SecondZuulPostFilter extends ZuulFilter {
    @Override
    public String filterType() {
        return "post";
    }

    @Override
    public int filterOrder() {
        return 2000;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() {
        return new ZuulException("SecondZuulPostFilter always throws ZuulException", 500, "UNHANDLED_EXCEPTION");
    }
}
