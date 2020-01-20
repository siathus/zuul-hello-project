package com.direa.seonggook.zuulsample.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.springframework.http.RequestEntity;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class SecondZuulPostFilter extends ZuulFilter {
    @Override
    public String filterType() {
        return "post";
    }

    @Override
    public int filterOrder() {
        return 3000;
    }

    @Override
    public boolean shouldFilter() {
        RequestContext ctx = RequestContext.getCurrentContext();
        if (ctx.getThrowable() == null && ctx.getResponseBody() != null) {
            return true;
        }
        return false;
    }

    @Override
    public Object run() {
        System.out.println("============ Second Post Filter Run =================");

        try {
            System.out.println("Writing...");
            writeResponse();
            System.out.println("Writing End");
        } catch (Exception e) {
            System.out.println("Error with Writing!!!");
            e.printStackTrace();
        } finally {
            System.out.println("============ Second Post Filter End =================");
        }
        return null;
    }

    private void writeResponse() throws Exception {
        RequestContext ctx = RequestContext.getCurrentContext();

        HttpServletResponse response = ctx.getResponse();
        if (response.getCharacterEncoding() == null || !response.getCharacterEncoding().equals("UTF-8")) {
            response.setCharacterEncoding("UTF-8");
        }

        OutputStream outputStream = response.getOutputStream();
        InputStream inputStream = null;
        try {
            inputStream = new ByteArrayInputStream(ctx.getResponseBody().getBytes(response.getCharacterEncoding()));
            writeResponse(inputStream, outputStream);
            return;
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
    }

    private void writeResponse(InputStream inputStream, OutputStream outputStream) throws Exception {
        byte[] bytes = new byte[1024];
        int bytesRead = -1;
        while ((bytesRead = inputStream.read(bytes)) != -1) {
            try {
                outputStream.write(bytes, 0, bytesRead);
                outputStream.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
