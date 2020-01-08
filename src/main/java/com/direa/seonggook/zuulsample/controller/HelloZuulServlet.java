package com.direa.seonggook.zuulsample.controller;

import com.netflix.zuul.http.ZuulServlet;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.annotation.WebServlet;

@RequestMapping("/")
@WebServlet
public class HelloZuulServlet extends ZuulServlet {

}
