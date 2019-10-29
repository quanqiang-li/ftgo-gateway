package com.gateway.filter;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;

import com.gateway.common.Constant;
import com.gateway.util.WebUtil;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
/**
 * 认证，授权处理
 * @author Administrator
 *
 */
@Component
public class PreBTokenFilter extends ZuulFilter {
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());

	private AntPathMatcher antPathMatcher = new AntPathMatcher();

	// 放行的资源，不需要入参token，正式切换到redis
	private static String[] permitAll = { "/", "/index.html", "/favicon.ico", "*.css", "/error/**", "/html/**", "*.js",
			"*.png", "/kaptchaLogin", "/logout", "/kaptcha/**", "/smsLogin", "/sms/**", "/v2/api-docs",
			"/swagger-ui.html", "/webjars/**", "/swagger-resources/**" };

	@Override
	public boolean shouldFilter() {
		HttpServletRequest request = RequestContext.getCurrentContext().getRequest();
		// 直接放行
		for (int i = 0; i < permitAll.length; i++) {
			if (antPathMatcher.match(permitAll[i], request.getRequestURI())) {
				logger.info("请求公开资源{}，放行",request.getRequestURI());
				return false;
			}
		}
		return true;
	}

	@Override
	public Object run() throws ZuulException {
		RequestContext context = RequestContext.getCurrentContext();
		HttpServletRequest request = context.getRequest();
		String token = WebUtil.extractParam(request, Constant.TOKEN_PARAM);
		if(StringUtils.isEmpty(token)) {
			logger.error("请求保护资源{}，当前状态未认证，拒绝服务",request.getRequestURI());
			// 1.抛出异常，可以终止route 类型 filter，即不会路由服务，但是post类型filter仍然会执行
			//throw new ZuulException("未认证,拒绝服务",ExecutionStatus.DISABLED.hashCode(), "非法请求，拒绝服务");
			// 2.正确的终止路由，不会终止任何filter，但是不会路由服务，并设置返回客户端信息
			context.setSendZuulResponse(false);
			context.setResponseStatusCode(500);
			context.setResponseBody("sorry!no auth");
		}
		return null;
	}

	@Override
	public String filterType() {
		return "pre";
	}

	@Override
	public int filterOrder() {
		// 在 PreDecorationFilter之前执行
		return FilterConstants.PRE_DECORATION_FILTER_ORDER -1;
	}

}
