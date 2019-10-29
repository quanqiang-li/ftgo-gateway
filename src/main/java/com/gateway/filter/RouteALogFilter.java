package com.gateway.filter;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.stereotype.Component;

import com.gateway.common.Constant;
import com.gateway.util.WebUtil;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;

/**
 * 记录下请求信息
 * 
 * @author Administrator
 *
 */
@Component
public class RouteALogFilter extends ZuulFilter {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Override
	public boolean shouldFilter() {
		return true;
	}

	@Override
	public Object run() throws ZuulException {
		RequestContext currentContext = RequestContext.getCurrentContext();
		HttpServletRequest request = currentContext.getRequest();
		// 接收到客户端请求的时间
		Date cr = (Date) currentContext.get(Constant.FILTER_KEY_CR);
		Date now = new Date();
		currentContext.set(Constant.FILTER_KEY_CS, now);
		long delay = now.getTime() - cr.getTime();
		logger.info("客户端设备={},IP={},请求={},执行路由,延迟={}ms", request.getHeader("User-Agent"), WebUtil.getIpAddr(request),
				request.getRequestURI(), delay);
		return null;
	}

	@Override
	public String filterType() {
		return "route";
	}

	@Override
	public int filterOrder() {
		// 同类型的filter越低越先执行
		return FilterConstants.SIMPLE_HOST_ROUTING_FILTER_ORDER - 1;
	}

}
