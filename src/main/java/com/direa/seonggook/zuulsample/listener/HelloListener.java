package com.direa.seonggook.zuulsample.listener;

import com.netflix.zuul.FilterLoader;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.filters.FilterRegistry;
import com.netflix.zuul.groovy.GroovyCompiler;
import com.netflix.zuul.http.ZuulServlet;
import com.netflix.zuul.monitoring.MonitoringHelper;

import javax.servlet.*;
import javax.servlet.annotation.WebListener;
import java.util.Enumeration;

@WebListener
public class HelloListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("contextInitialized");

        MonitoringHelper.initMocks();

        final FilterRegistry filterRegistry = FilterRegistry.instance();

        filterRegistry.put("pre", new ZuulFilter() {
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
                System.out.println("pre filter run");
                RequestContext.getCurrentContext().set("pre-ran", true);
                return null;
            }
        });

    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {

    }
}
