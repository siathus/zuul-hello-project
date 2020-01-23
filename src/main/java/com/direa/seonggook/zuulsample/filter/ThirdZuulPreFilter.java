package com.direa.seonggook.zuulsample.filter;

import com.netflix.client.ClientFactory;
import com.netflix.loadbalancer.*;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ThirdZuulPreFilter extends ZuulFilter {

    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 100;
    }

    @Override
    public boolean shouldFilter() {
        RequestContext ctx = RequestContext.getCurrentContext();

        // SecondZuulPreFilter에서 저장한 ServerList를 확인하여 값이 존재할 때만 필터 실행
        List<Server> serverList = (List<Server>)ctx.get("serverList");
        if (serverList != null && serverList.size() > 0) {
            return true;
        }
        return false;
    }

    @Override
    public Object run() {
        // Ribbon Load Balancing 작업 수행하는 Pre Filter
        System.out.println("========== Third Pre Filter Run ==========");
        RequestContext ctx = RequestContext.getCurrentContext();
        List<Server> serverList = (List<Server>)ctx.get("serverList");

//        BaseLoadBalancer lb = (BaseLoadBalancer) ClientFactory.getNamedLoadBalancer("randomLoadBalancer");
//        ILoadBalancer lb = LoadBalancerBuilder.newBuilder().withRule(new RoundRobinRule()).buildDynamicServerListLoadBalancer();
        ILoadBalancer lb = ClientFactory.getNamedLoadBalancer("randomLoadBalancer");
        lb.addServers(serverList);
        Server server = lb.chooseServer(new RoundRobinRule());
//        IRule rule = new RandomRule();
//        BaseLoadBalancer lb = new BaseLoadBalancer();
//
//        lb.addServers(serverList);
//        lb.setRule(rule);
//        Server server = lb.chooseServer();

        System.out.println("Chosen Server Host & Port : " + server.getHostPort());
        ctx.putIfAbsent("server", server);
        System.out.println("========== Third Pre Filter End ==========");
        return null;
    }
}
