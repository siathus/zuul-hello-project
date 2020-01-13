package com.direa.seonggook.zuulsample.filter;

import com.netflix.zuul.context.ContextLifecycleFilter;

import javax.servlet.annotation.WebFilter;

@WebFilter
public class ZuulSampleFilter extends ContextLifecycleFilter {
}
