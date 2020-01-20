package com.direa.seonggook.zuulsample.filter;

import com.netflix.config.DynamicIntProperty;
import com.netflix.config.DynamicPropertyFactory;
import com.netflix.config.DynamicStringProperty;
import com.netflix.loadbalancer.Server;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.constants.ZuulConstants;
import com.netflix.zuul.context.RequestContext;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SecondZuulPreFilter extends ZuulFilter {
    private static final String ZUUL_CLIENT_SERVICEURL = "zuul.client.serviceUrl";

    private DynamicStringProperty defaultHost = DynamicPropertyFactory.getInstance().getStringProperty(ZuulConstants.ZUUL_DEFAULT_HOST, null);

    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 50;
    }

    @Override
    public boolean shouldFilter() {
        // properties 파일에 "zuul.default.host" 설정이 localhost로 되어있을 때만 실행
        if (defaultHost.get().equals("localhost")) return true;

        return false;
    }

    @Override
    public Object run() {
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();

        /*
         Eureka Server를 이용하지 않고 properties 파일에서 직접 설정한
         routing path를 이용하여 routing
         */
        System.out.println("========== Second Pre Filter Run =========");
        System.out.println("Request URL : " + request.getRequestURL());
        System.out.println("Request URI : " + request.getRequestURI());

        String requestUri = request.getRequestURI();

        // Request URI를 "/"를 기준으로 split하면 <<두 번째>> 인덱스가 client의 namespace가 된다.
        // URI 가장 앞에 "/"가 있으므로 첫 번째 인덱스는 ""가 들어간다.
        String clientNamespace = requestUri.split("/")[1];
        System.out.println("Client Namespace : " + clientNamespace);

        // properties 파일에서 namespace값을 기준으로 해당하는 서비스들의 URL을 받아온 뒤 쉼표(,)를 기준으로 URL을 나눈다.
        List<String> serviceUrlList = Arrays.asList(DynamicPropertyFactory.getInstance().getStringProperty(clientNamespace + "." + ZUUL_CLIENT_SERVICEURL, null).get().split(","));

        List<Server> serverList = new ArrayList<>();
        for (int i = 0; i < serviceUrlList.size(); i++) {
            System.out.println("Service URL " + (i + 1) + ") : " + serviceUrlList.get(i));
            serverList.add(new Server(serviceUrlList.get(i)));
        }

        // namespace를 제외한 URI를 RequestContext에 저장
//        requestUri = requestUri.substring(clientNamespace.length() + 1);
//        System.out.println("request URI : " + requestUri);
//        ctx.put("requestUri", requestUri);

        // Server List를 RequestContext에 저장
        ctx.put("serverList", serverList);

        System.out.println("========== Second Pre Filter End ==========");
        return null;
    }
}
