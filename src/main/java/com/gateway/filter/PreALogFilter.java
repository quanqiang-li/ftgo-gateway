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
 * @author Administrator
 *
 */
@Component
public class PreALogFilter extends ZuulFilter {
	
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
		currentContext.set(Constant.FILTER_KEY_CR, new Date());
		logger.info("客户端设备={},IP={},请求={}",request.getHeader("User-Agent"),WebUtil.getIpAddr(request),request.getRequestURI());
		return null;
	}

	@Override
	public String filterType() {
		return "pre";
	}

	@Override
	public int filterOrder() {
		// 在 PreDecorationFilter之前执行,同类型的filter越低越先执行
		return FilterConstants.PRE_DECORATION_FILTER_ORDER -10;
	}

}
