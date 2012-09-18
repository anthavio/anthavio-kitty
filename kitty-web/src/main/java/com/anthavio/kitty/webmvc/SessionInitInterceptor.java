package com.anthavio.kitty.webmvc;

/**
 * 
 */

import java.io.File;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

/**
 * @author vanek
 *
 */
public class SessionInitInterceptor extends HandlerInterceptorAdapter {

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		if (request.getSession().getAttribute(BaseScenarioController.BROWSE_DIR_KEY) == null) {
			File browseDir = new File(System.getProperty("user.dir"));
			request.getSession().setAttribute(BaseScenarioController.BROWSE_DIR_KEY, browseDir);
		}
		return true;
	}
}
