package com.direa.seonggook.zuulsample.listener;

import com.direa.seonggook.zuulsample.filter.*;
import com.netflix.client.ClientException;
import com.netflix.client.ClientFactory;
import com.netflix.client.config.DefaultClientConfigImpl;
import com.netflix.config.DynamicPropertyFactory;
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

        filterRegistry.put("thirdPre", new ThirdZuulPreFilter());
        filterRegistry.put("pre", new ZuulPreFilter());
        filterRegistry.put("secondPre", new SecondZuulPreFilter());

        // put의 첫 번째 매개변수는 그저 filter의 type명이 아니라 각 filter를 구분하기 위한 key로 사용된다.
        filterRegistry.put("firstRoute", new ZuulRouteFilter());
        filterRegistry.put("secondRoute", new SecondZuulRouterFilter());

        filterRegistry.put("post", new ZuulPostFilter());
        filterRegistry.put("secondPost", new SecondZuulPostFilter());

//        filterRegistry.put("error", new ZuulErrorFilter());

    }


    @Override
    public void contextDestroyed(ServletContextEvent sce) {

    }
}
