package com.direa.seonggook.zuulsample.filter;

import com.direa.seonggook.zuulsample.eureka.ZuulEurekaClient;
import com.direa.seonggook.zuulsample.hystrix.ServiceHystrixCommand;
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
        return 1;
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

        try {

            // Eureka의 VIP Address를 이용하여 같은 서비스를 제공하는 서버 목록 가져오기
            ServerList<DiscoveryEnabledServer> list = new DiscoveryEnabledNIWSServerList("sampleservice.mydomain.net", new Provider<EurekaClient>() {
                private final EurekaClient eurekaClient = new ZuulEurekaClient().getZuulEurekaClient();
                @Override
                public EurekaClient get() {
                    return eurekaClient;
                }
            });

            List<DiscoveryEnabledServer> deList = list.getInitialListOfServers();

            // Load Balancer 추가
            BaseLoadBalancer lb = new BaseLoadBalancer();
            System.out.println("검색된 서비스의 전체 목록");
            for (int i = 0; i < deList.size(); i++) {
                System.out.println("Port Number : " + deList.get(i).getPort());

                // Load Balancer에 Server 추가
                lb.addServer((Server) deList.get(i));
            }
            System.out.println();

            // Load Balancer 설정 후 서버 선택
            IRule rule = new RandomRule();
            lb.setPing(new NIWSDiscoveryPing());
            lb.setRule(rule);
            Server server = lb.chooseServer();

            if (server != null) {
                System.out.println("Load Balancing Completed : Port Number is " + server.getPort());
                String destinationUrl = "http://localhost:" + server.getPort();
                System.out.println("최종 URL : " + destinationUrl);

                // Hystrix를 통해 Service 로직 실행 및 결과 가져오기
                String result = new ServiceHystrixCommand(destinationUrl).execute();

                // 결과값을 RequestContext에 Response Body에 설정
                ctx.setResponseBody(result);
            } else {
                System.out.println("Load Balancing Failed");
            }

            System.out.println("============== Route Filter End ===============");

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Cannot Find Eureka Service!!!");
        }
        return null;
    }

}
