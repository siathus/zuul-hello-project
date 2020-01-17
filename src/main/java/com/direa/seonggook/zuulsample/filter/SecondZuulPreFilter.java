package com.direa.seonggook.zuulsample.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;

public class SecondZuulPreFilter extends ZuulFilter {

    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 50;
    }

    @Override
    public boolean shouldFilter() {
        RequestContext ctx = RequestContext.getCurrentContext();
        if (ctx.getRequest().getMethod().equals("GET")) {
            return true;
        }
        return false;
    }

    @Override
    public Object run() {
        System.out.println("========== Second Pre Filter Run =========");
        RequestContext ctx = RequestContext.getCurrentContext();

        System.out.println("Request URL : " + ctx.getRequest().getRequestURL().toString());
        System.out.println("========== Second Pre Filter End ==========");

        return null;
    }
}
