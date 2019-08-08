package com.proxy.controller;


import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;


@WebFilter(urlPatterns = "/*")
public class DummyFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) 
    		throws ServletException, IOException {
        HttpServletResponseWrapper wrapper = new HttpServletResponseWrapper(response) {
            @Override
            public void setStatus(int sc) {
                super.setStatus(sc);
                handleStatus(sc);
            }
            @Override
            @SuppressWarnings("deprecation")
            public void setStatus(int sc, String sm) {
                super.setStatus(sc, sm);
                handleStatus(sc);
            }
            @Override
            public void sendError(int sc, String msg) throws IOException {
                super.sendError(sc, msg);
                handleStatus(sc);
            }
            @Override
            public void sendError(int sc) throws IOException {
                super.sendError(sc);
                handleStatus(sc);
            }
            private void handleStatus(int code) {
                //if(code == 404)
            	
            	System.out.println("code:"+code);
                this.addHeader("dummy-header", "dummy-value");
                                
            }
        };
        
        System.out.println("dummyfilter");
        
        filterChain.doFilter(request, wrapper);
        
        
    }
}

//@WebFilter(urlPatterns = "/*")
//public class DummyFilter implements Filter {
//
//	@Override
//	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
//		throws IOException, ServletException {
//		
//		HttpServletRequest req = (HttpServletRequest) request;
//		HttpServletResponse res = (HttpServletResponse) response;
//		
//		System.out.println("dummy filter start");
//		chain.doFilter(request, response);
//		
//		System.out.println("dummy filter end");
//	}
//
//
//
//	@Override
//	public void init(FilterConfig filterConfig) throws ServletException {
//		// TODO Auto-generated method stub
//		
//	}
//
//	
//
//	@Override
//	public void destroy() {
//		// TODO Auto-generated method stub
//		
//	}
//}

    




