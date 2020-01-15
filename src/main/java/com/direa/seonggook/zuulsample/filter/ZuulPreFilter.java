package com.direa.seonggook.zuulsample.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import org.springframework.http.RequestEntity;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.util.Map;

public class ZuulPreFilter extends ZuulFilter {

    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 30;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() {
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();
        System.out.println("========== Pre Filter Run =========");
        System.out.println("Request Method : " + request.getMethod());
        System.out.println("Request URL : " + request.getRequestURL().toString());
        System.out.println("========== Pre Filter End ==========");

        return null;
    }
}
