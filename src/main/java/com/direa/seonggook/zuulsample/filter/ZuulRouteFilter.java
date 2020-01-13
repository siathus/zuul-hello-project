package com.direa.seonggook.zuulsample.filter;

import com.direa.seonggook.zuulsample.eureka.ZuulEurekaClient;
import com.direa.seonggook.zuulsample.hystrix.DemoHystrixCommand;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import com.netflix.loadbalancer.*;
import com.netflix.niws.loadbalancer.DiscoveryEnabledNIWSServerList;
import com.netflix.niws.loadbalancer.NIWSDiscoveryPing;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;
import com.netflix.niws.loadbalancer.DiscoveryEnabledServer;

import javax.inject.Provider;
import java.util.List;

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
//        InstanceInfo nextServerInfo = null;
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
//            System.out.println("검색된 서비스의 전체 목록");
//            for (int i = 0; i < deList.size(); i++) {
//                System.out.println(deList.get(i).getPort());
//                lb.addServer((Server) deList.get(i));
//            }
//            System.out.println();

//            ServerListFilter<DiscoveryEnabledServer> filter = new DefaultNIWSServerListFilter<>();
//            ServerListUpdater updater = new PollingServerListUpdater();
//            ZoneAwareLoadBalancer<DiscoveryEnabledServer> lb = LoadBalancerBuilder.<DiscoveryEnabledServer>newBuilder()
//                    .withClientConfig(new DefaultClientConfigImpl())
//                    .withDynamicServerList(list)
//                    .withRule(rule)
//                    .withServerListFilter(filter)
//                    .withServerListUpdater(updater)
//                    .buildDynamicServerListLoadBalancerWithUpdater();


            // Load Balancer 설정 후 서버 선택
            IRule rule = new RandomRule();
            lb.setPing(new NIWSDiscoveryPing());
            lb.setRule(rule);
            Server server = lb.chooseServer();

            if (server != null) {
                System.out.println("Load Balancing Completed");
                String destinationUrl = "http://localhost:" + server.getPort();
                System.out.println(destinationUrl);
                String result = new DemoHystrixCommand(destinationUrl).execute();

//                String result = restTemplate.getForObject(destinationUrl, String.class);
//                System.out.println(result);
            } else {
                System.out.println("Load Balancing Failed");
                System.exit(-1);
            }

            System.out.println("============== Route Filter End ===============");

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("cannot find eureka service");
            System.exit(-1);
        }
        return null;
    }
}
