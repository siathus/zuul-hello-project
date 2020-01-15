package com.direa.seonggook.zuulsample.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;

import javax.servlet.http.HttpServletResponse;

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
        RequestContext ctx = RequestContext.getCurrentContext();

        // ResponseBody가 있을 때만 Post Filter 실행
        if (ctx.getResponseBody() != null) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public Object run() {
        System.out.println("======== POST FILTER 실행 =======");

        RequestContext ctx = RequestContext.getCurrentContext();

        HttpServletResponse response = ctx.getResponse();

        System.out.println("Response Body : " + ctx.getResponseBody());

        System.out.println("======== POST FILTER 끝 =======");
        return ctx.getResponseBody();
    }
}
