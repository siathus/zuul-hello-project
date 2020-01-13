package com.direa.seonggook.zuulsample.filter;

import com.direa.seonggook.zuulsample.eureka.ZuulEurekaClient;
import com.direa.seonggook.zuulsample.hystrix.DemoCommand;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.client.ClientFactory;
import com.netflix.client.config.ClientConfigFactory;
import com.netflix.client.config.DefaultClientConfigImpl;
import com.netflix.discovery.DiscoveryClient;
import com.netflix.discovery.DiscoveryManager;
import com.netflix.discovery.EurekaClient;
import com.netflix.hystrix.HystrixCommandProperties;
import com.netflix.niws.loadbalancer.*;
import com.netflix.discovery.providers.DefaultEurekaClientConfigProvider;
import com.netflix.loadbalancer.*;
import com.netflix.niws.loadbalancer.DiscoveryEnabledNIWSServerList;
import com.netflix.niws.loadbalancer.NIWSDiscoveryPing;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;
import com.netflix.niws.loadbalancer.DiscoveryEnabledServer;

import javax.inject.Provider;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class ZuulRouteFilter extends ZuulFilter {
    @Autowired
    RestTemplate restTemplate;

    @Override
    public String filterType() {
        return "route";
    }

    @Override
    public int filterOrder() {
        return 100;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() {
        System.out.println("============== Route Filter Run ============");
        RequestContext ctx = RequestContext.getCurrentContext();
        RestTemplate restTemplate = new RestTemplate();

//        EurekaClient client = new ZuulEurekaClient().getZuulEurekaClient();

        // 유레카 서버찾기
        InstanceInfo nextServerInfo = null;
        try {
//            nextServerInfo = client.getNextServerFromEureka("sampleservice.mydomain.net", false);

//            System.out.println("Eureka : " + nextServerInfo.getVIPAddress() + ":" + nextServerInfo.getPort());
//            String destinationUrl = "http://" + nextServerInfo.getIPAddr() + ":" + nextServerInfo.getPort();
            // 유레카 서버찾기 끝

//            // Ribbon Load balancing 시작
//            DiscoveryEnabledServer discoveryEnabledServer = new DiscoveryEnabledServer(nextServerInfo, false);
//            System.out.println(discoveryEnabledServer.getInstanceInfo().getVIPAddress());

            final EurekaClient eurekaClient = new ZuulEurekaClient().getZuulEurekaClient();
            ServerList<DiscoveryEnabledServer> list = new DiscoveryEnabledNIWSServerList("sampleservice.mydomain.net", new Provider<EurekaClient>() {
                @Override
                public EurekaClient get() {
                    return eurekaClient;
                }
            });

            List<DiscoveryEnabledServer> deList = list.getInitialListOfServers();
            BaseLoadBalancer lb = new BaseLoadBalancer();
            System.out.println("검색된 서비스의 전체 목록");
            for (int i = 0; i < deList.size(); i++) {
                System.out.println(deList.get(i).getPort());
                lb.addServer((Server) deList.get(i));
            }
            System.out.println();

//            ServerListFilter<DiscoveryEnabledServer> filter = new DefaultNIWSServerListFilter<>();
//            ServerListUpdater updater = new PollingServerListUpdater();
//            ZoneAwareLoadBalancer<DiscoveryEnabledServer> lb = LoadBalancerBuilder.<DiscoveryEnabledServer>newBuilder()
//                    .withClientConfig(new DefaultClientConfigImpl())
//                    .withDynamicServerList(list)
//                    .withRule(rule)
//                    .withServerListFilter(filter)
//                    .withServerListUpdater(updater)
//                    .buildDynamicServerListLoadBalancerWithUpdater();


            IRule rule = new RandomRule();
            lb.setPing(new NIWSDiscoveryPing());
            lb.setRule(rule);
            Server server = lb.chooseServer();
            if (server != null) {
                System.out.println("lb passed");
                System.out.println(server.getPort());
                String destinationUrl = "http://localhost:" + server.getPort();
                DemoCommand demoCommand = new DemoCommand(destinationUrl);
//                String result = restTemplate.getForObject(destinationUrl, String.class);
//                System.out.println(result);
            } else {
                System.out.println("lb failed");
                System.exit(-1);
            }

            System.out.println("===========================================");

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("cannot find eureka service");
            System.exit(-1);
        }


        return null;
    }
}