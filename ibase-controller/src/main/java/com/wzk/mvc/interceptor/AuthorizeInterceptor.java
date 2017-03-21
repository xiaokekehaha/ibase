package com.wzk.mvc.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;


public class AuthorizeInterceptor implements HandlerInterceptor{
	
	private String[] allowUrls; //允许不拦截的资源

	public void setAllowUrls(String[] allowUrls) {
		this.allowUrls = allowUrls;
	}

	
	@Override
	public boolean preHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler) throws Exception {
		String requestURI = request.getRequestURI().replace(request.getContextPath(), "");
		
		if(null!=allowUrls && allowUrls.length>=1){ //允许不拦截的资源
			for(String url:allowUrls){
				if(requestURI.contains(url)){
					return true;
				}
			}
		}
		
		return true;
	}
	@Override
	public void postHandle(HttpServletRequest request,HttpServletResponse response, Object handler,ModelAndView modelAndView) throws Exception {
	}

	@Override
	public void afterCompletion(HttpServletRequest request,HttpServletResponse response, Object handler, Exception ex)throws Exception {
		
	}
	
	
}
