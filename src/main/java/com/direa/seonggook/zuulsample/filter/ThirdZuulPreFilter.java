package com.direa.seonggook.zuulsample.filter;

import com.netflix.client.ClientFactory;
import com.netflix.client.http.HttpRequest;
import com.netflix.client.http.HttpResponse;
import com.netflix.config.DynamicPropertyFactory;
import com.netflix.loadbalancer.*;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.niws.client.http.RestClient;

import java.net.URI;
import java.util.List;

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

        System.out.println("####### Round Robin Rule #######");

        // Servlet Context에 저장된 Load Balancer를 가져온다
        BaseLoadBalancer roundRobinLoadBalancer = (BaseLoadBalancer) ctx.getRequest().getServletContext().getAttribute("baseLoadBalancer");
        roundRobinLoadBalancer.setServersList(serverList);
        roundRobinLoadBalancer.setRule(new RoundRobinRule());

        for (int i = 0; i < 20; i++) {
            System.out.println(roundRobinLoadBalancer.choose(null));
        }
        System.out.println("##### Round Robin Rule End #####");


        System.out.println("\n####### Best Available Rule #######");
        BaseLoadBalancer bestAvailableLoadBalancer = LoadBalancerBuilder.newBuilder().withRule(new BestAvailableRule()).buildFixedServerListLoadBalancer(serverList);
        for (int i = 0; i < serverList.size(); i++) {
            ServerStats stats = bestAvailableLoadBalancer.getLoadBalancerStats().getSingleServerStat(serverList.get(i));
            // Request Connection을 인위적으로 증가시킨다.
            for (int j = 0; j < 10 - i; j++) {
                stats.incrementActiveRequestsCount();
            }
            System.out.print("Stats : " + stats.toString());
        }
        // Active Connection이 가장 낮은 서버를 선택한다.
        System.out.println("\nChosen Server : " + bestAvailableLoadBalancer.choose(null));

        System.out.println("##### Best Available Rule End #####");

        System.out.println("\n####### Availability Filtering Rule #######");
        BaseLoadBalancer availabilityFilteringLoadBalancer = LoadBalancerBuilder.newBuilder().withRule(new AvailabilityFilteringRule()).buildFixedServerListLoadBalancer(serverList);
        for (int i = 0; i < serverList.size() - 1; i++) {
            ServerStats stat = availabilityFilteringLoadBalancer.getLoadBalancerStats().getSingleServerStat(serverList.get(i));

            // niws.loadbalancer.default.connectionFailureCountThreshold=5
            // 마지막 서버를 제외한 모든 서버에 Connection Failure Count를 인위적으로 증가시킨다.
            int connectionFailureCountThreshold = DynamicPropertyFactory.getInstance().getIntProperty("niws.loadbalancer.default.connectionFailureCountThreshold", 3).getValue();
            for (int j = 0; j < connectionFailureCountThreshold; j++) {
                stat.incrementSuccessiveConnectionFailureCount();
            }
        }
        System.out.println(availabilityFilteringLoadBalancer.getLoadBalancerStats());

        // Failure Count가 없는 마지막 서버가 선택된다.
        System.out.println("\nChosen Server : " + availabilityFilteringLoadBalancer.choose(null));

        System.out.println("##### Availability Filtering Rule End #####");

        System.out.println("\n####### Weighted Response Time Rule #######");
        RestClient restClient = (RestClient) ClientFactory.getNamedClient("first");

        try {
            HttpRequest request = HttpRequest.newBuilder().uri(new URI("/")).build();

            // Response Time 계산을 위해 Load Balancer를 통해 20번 요청 보냄
            for (int i = 0; i < 20; i++) {
                restClient.executeWithLoadBalancer(request);
            }

            BaseLoadBalancer weightedResponseTimeLoadBalancer = (BaseLoadBalancer) restClient.getLoadBalancer();
            System.out.println(weightedResponseTimeLoadBalancer.getLoadBalancerStats());

            // Response Time이 가장 낮은 서버 선택
            System.out.println("\n Chosen Server : " + weightedResponseTimeLoadBalancer.choose(null));

            System.out.println("##### Weighted Response Time Rule End #####");
        } catch (Exception e) {
            e.printStackTrace();
        }

//        Server server = lb.chooseServer();
//        System.out.println("Chosen Server Host & Port : " + server.getHostPort());
//        ctx.putIfAbsent("server", server);
        System.out.println("========== Third Pre Filter End ==========");
        return null;
    }
}
