package com.direa.seonggook.zuulsample.listener;

import com.direa.seonggook.zuulsample.filter.*;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.filters.FilterRegistry;
import com.netflix.zuul.monitoring.MonitoringHelper;

import javax.servlet.*;
import javax.servlet.annotation.WebListener;
import java.util.Iterator;

@WebListener
public class HelloListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("=====================");
        System.out.println("Context Initialized");
        System.out.println("=====================");

        MonitoringHelper.initMocks();

        final FilterRegistry filterRegistry = FilterRegistry.instance();

        filterRegistry.put("pre", new ZuulPreFilter());

        // put의 첫 번째 매개변수는 그냥 filter의 type명이 아니라 각 filter를 구분하기 위한 key로 사용된다.
        filterRegistry.put("firstRoute", new ZuulRouteFilter());
        filterRegistry.put("secondRoute", new SecondZuulRouterFilter());

        filterRegistry.put("post", new ZuulPostFilter());

        // 나중에 해야될거
//        filterRegistry.put("post", new SecondZuulPostFilter());

        filterRegistry.put("error", new ZuulErrorFilter());
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {

    }
}
