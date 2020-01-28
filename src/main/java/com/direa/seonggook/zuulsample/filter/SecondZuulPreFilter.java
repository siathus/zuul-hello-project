package com.direa.seonggook.zuulsample.filter;

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
    private static final String ZUUL_CLIENT_SERVICEURL = "ribbon.listOfServers";

    private static final DynamicPropertyFactory configInstance = DynamicPropertyFactory.getInstance();
    private final DynamicStringProperty defaultHost = configInstance.getStringProperty(ZuulConstants.ZUUL_DEFAULT_HOST, null);

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
        List<String> serviceUrlList = Arrays.asList(configInstance.getStringProperty(clientNamespace + "." + ZUUL_CLIENT_SERVICEURL, null)
                .get()
                .trim()
                .split(","));

        List<Server> serverList = new ArrayList<>();
        for (String serviceUrl : serviceUrlList) {
            // ":"를 기준으로 Host와 Port를 나눔
            int portColonIndex = serviceUrl.lastIndexOf(":");
            String host = serviceUrl.substring(0, portColonIndex);

            // URL 가장 앞에 http:// 나 https:// 가 붙어있지 않을 경우 http:// 를 붙이는 작업
            if (!host.startsWith("http://") && !host.startsWith("https://")) {
                host = "http://" + host;
            }
            // 콜론(:)은 제외시켜야 하므로 index에 1을 더한다.
            int port = Integer.parseInt(serviceUrl.substring(portColonIndex + 1));

            System.out.println("Service URL : " + host + ":" + port);
            /*
             * Server의 생성자
             *   Server(String id) : Host와 Port번호를 id에서 추출한다. 추출 도중 id의 http://와 https://를 삭제한다.
             *                       따라서 host와 port 두 개를 매개변수로 받는 생성자[Server(String host, int port)]를 이용해야 한다.
             */
            serverList.add(new Server(host, port));
        }

        // namespace를 제외한 URI를 RequestContext에 저장
        requestUri = requestUri.substring(clientNamespace.length() + 1);
        System.out.println("request URI : " + requestUri);
        ctx.put("requestUri", requestUri);

        // Server List를 RequestContext에 저장
        ctx.put("serverList", serverList);

        System.out.println("========== Second Pre Filter End ==========");
        return null;
    }
}
