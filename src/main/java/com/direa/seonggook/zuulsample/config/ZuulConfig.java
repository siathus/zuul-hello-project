package com.direa.seonggook.zuulsample.config;

import com.netflix.zuul.FilterFileManager;
import com.netflix.zuul.FilterLoader;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.filters.FilterRegistry;
import com.netflix.zuul.groovy.GroovyCompiler;
import com.netflix.zuul.groovy.GroovyFileFilter;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ZuulConfig {

    public ZuulConfig() {
        FilterLoader.getInstance().setCompiler(new GroovyCompiler());

        final FilterRegistry filterRegistry = FilterRegistry.instance();

        filterRegistry.put("javaPreFilter", new ZuulFilter() {
            @Override
            public String filterType() {
                return "pre";
            }

            @Override
            public int filterOrder() {
                return 500;
            }

            @Override
            public boolean shouldFilter() {
                return true;
            }

            @Override
            public Object run() {
                System.out.println("javaPreFilter run");
                RequestContext.getCurrentContext().set("javaPreFilter-ran", true);
                return null;
            }
        });
    }


}
