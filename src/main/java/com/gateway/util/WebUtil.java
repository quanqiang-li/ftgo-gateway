package com.gateway.util;

import java.io.UnsupportedEncodingException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.config.RequestConfig.Builder;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

/**
 * web服务常用工具类
 * 
 * @author liqq
 *
 */
public class WebUtil {

	private static Logger logger = LoggerFactory.getLogger(WebUtil.class);

	private static final String defaultCharset = "UTF-8";

	/**
	 * 获取客户端ip地址
	 * 
	 * @param request
	 * @return
	 */
	public static String getIpAddr(HttpServletRequest request) {
		String ip = null;

		// X-Forwarded-For：Squid 服务代理
		String ipAddresses = request.getHeader("X-Forwarded-For");
		if (ipAddresses == null || ipAddresses.length() == 0 || "unknown".equalsIgnoreCase(ipAddresses)) {
			// Proxy-Client-IP：apache 服务代理
			ipAddresses = request.getHeader("Proxy-Client-IP");
		}
		if (ipAddresses == null || ipAddresses.length() == 0 || "unknown".equalsIgnoreCase(ipAddresses)) {
			// WL-Proxy-Client-IP：weblogic 服务代理
			ipAddresses = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ipAddresses == null || ipAddresses.length() == 0 || "unknown".equalsIgnoreCase(ipAddresses)) {
			// HTTP_CLIENT_IP：有些代理服务器
			ipAddresses = request.getHeader("HTTP_CLIENT_IP");
		}
		if (ipAddresses == null || ipAddresses.length() == 0 || "unknown".equalsIgnoreCase(ipAddresses)) {
			// X-Real-IP：nginx服务代理
			ipAddresses = request.getHeader("X-Real-IP");
		}
		// 有些网络通过多层代理，那么获取到的ip就会有多个，一般都是通过逗号（,）分割开来，并且第一个ip为客户端的真实IP
		if (ipAddresses != null && ipAddresses.length() != 0) {
			ip = ipAddresses.split(",")[0];
		}
		// 还是不能获取到，最后再通过request.getRemoteAddr();获取
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ipAddresses)) {
			ip = request.getRemoteAddr();
		}
		return ip;
	}

	/**
	 * get请求,支持返回字符串类型
	 * 
	 * @param url            参数拼接到url后
	 * @param connectTimeout 连接超时,单位毫秒,<0 默认系统超时时间,0 永不超时
	 * @param readTimeout    读取数据超时时间,单位毫秒,<0 默认系统超时时间,0 永不超时
	 * @param headers
	 * @return
	 */
	public static String getRequest(String url, int connectTimeout, int readTimeout, Header[] headers) {
		logger.info("getStr url:{}", url);
		CloseableHttpClient httpClient = createClient(url, connectTimeout, readTimeout);

		HttpGet httpget = new HttpGet(url);
		if (headers != null) {
			httpget.setHeaders(headers);
		}
		try {
			CloseableHttpResponse response = httpClient.execute(httpget);
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				String responseStr = EntityUtils.toString(entity, defaultCharset);
				logger.info("getStr response:{}", responseStr);
				return responseStr;
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		}

		return null;
	}

	/**
	 * 
	 * @param url
	 * @param connectTimeout 连接超时,单位毫秒,<0 默认系统超时时间,0 永不超时
	 * @param readTimeout    读取数据超时时间,单位毫秒,<0 默认系统超时时间,0 永不超时
	 * @param json
	 * @param headers
	 * @return
	 */
	public static String postJson(String url, int connectTimeout, int readTimeout, String json, Header[] headers) {
		logger.info("postJson url:{}", url);
		CloseableHttpClient httpClient = createClient(url, connectTimeout, readTimeout);
		HttpPost httpPost = new HttpPost(url);
		if (headers != null) {
			httpPost.setHeaders(headers);
		}
		// 编码格式
		StringEntity stringEntity = new StringEntity(json, defaultCharset);
		httpPost.setEntity(stringEntity);
		httpPost.addHeader("Content-Type", "application/json");
		try {
			CloseableHttpResponse response = httpClient.execute(httpPost);
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				String responseStr = EntityUtils.toString(entity, defaultCharset);
				logger.info("postJson response:{}", responseStr);
				return responseStr;
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return null;
	}

	/**
	 * 
	 * @param url
	 * @param connectTimeout 连接超时,单位毫秒,<0 默认系统超时时间,0 永不超时
	 * @param readTimeout    读取数据超时时间,单位毫秒,<0 默认系统超时时间,0 永不超时
	 * @param parameters
	 * @param headers
	 * @return
	 */
	public static String postForm(String url, int connectTimeout, int readTimeout,
			List<? extends NameValuePair> parameters, Header[] headers) {
		logger.info("postForm url:{}", url);
		CloseableHttpClient httpClient = createClient(url, connectTimeout, readTimeout);
		HttpPost httpPost = new HttpPost(url);
		if (headers != null) {
			httpPost.setHeaders(headers);
		}
		// 编码格式
		try {
			UrlEncodedFormEntity uefEntity = new UrlEncodedFormEntity(parameters, defaultCharset);
			httpPost.setEntity(uefEntity);
		} catch (UnsupportedEncodingException e) {
			logger.error(e.getMessage());
		}
		try {
			CloseableHttpResponse response = httpClient.execute(httpPost);
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				String responseStr = EntityUtils.toString(entity, defaultCharset);
				logger.info("postForm response:{}", responseStr);
				return responseStr;
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return null;
	}

	/**
	 * 创建客户端
	 * 
	 * @param url
	 * @param connectTimeout
	 * @param readTimeout
	 * @return
	 */
	public static CloseableHttpClient createClient(String url, int connectTimeout, int readTimeout) {
		CloseableHttpClient httpClient = null;
		if (StringUtils.startsWithIgnoreCase(url, "https://")) {
			httpClient = createAcceptSelfSignedCertificateClient(connectTimeout, readTimeout);
		} else if (StringUtils.startsWithIgnoreCase(url, "http://")) {
			httpClient = createDefaultClient(connectTimeout, readTimeout);
		} else {
			httpClient = null;
		}
		return httpClient;
	}

	/**
	 * 创建http客户端,适用自签名证书 的https客户端,需要添加信任机制
	 * 
	 * @param connectTimeout 连接超时,单位毫秒,<0 默认系统超时时间,0 永不超时
	 * @param readTimeout    读取数据超时时间,单位毫秒,<0 默认系统超时时间,0 永不超时
	 * @return
	 * @throws KeyManagementException
	 * @throws NoSuchAlgorithmException
	 * @throws KeyStoreException
	 */
	private static CloseableHttpClient createAcceptSelfSignedCertificateClient(int connectTimeout, int readTimeout) {

		// use the TrustSelfSignedStrategy to allow Self Signed Certificates
		SSLContext sslContext;
		try {
			sslContext = SSLContextBuilder.create().loadTrustMaterial(new TrustSelfSignedStrategy()).build();
		} catch (Exception e) {
			logger.error(e.getMessage());
			return null;
		}

		// we can optionally disable hostname verification.
		// if you don't want to further weaken the security, you don't have to
		// include this.
		HostnameVerifier allowAllHosts = new NoopHostnameVerifier();

		// create an SSL Socket Factory to use the SSLContext with the trust
		// self signed certificate strategy
		// and allow all hosts verifier.
		SSLConnectionSocketFactory connectionFactory = new SSLConnectionSocketFactory(sslContext, allowAllHosts);
		// 设置超时时间
		Builder builder = RequestConfig.custom();
		RequestConfig requestConfig = builder.setConnectTimeout(connectTimeout).setSocketTimeout(readTimeout).build();
		// finally create the HttpClient using HttpClient factory methods and
		// assign the ssl socket factory
		return HttpClients.custom().setDefaultRequestConfig(requestConfig).setSSLSocketFactory(connectionFactory)
				.build();
	}

	/**
	 * 创建HTTP默认客户端,适用http,采用公开证书的https
	 * 
	 * @param connectTimeout 连接超时,单位毫秒,<0 默认系统超时时间,0 永不超时
	 * @param readTimeout    读取数据超时时间,单位毫秒,<0 默认系统超时时间,0 永不超时
	 * @return
	 */
	private static CloseableHttpClient createDefaultClient(int connectTimeout, int readTimeout) {
		Builder builder = RequestConfig.custom();
		RequestConfig requestConfig = builder.setConnectTimeout(connectTimeout).setSocketTimeout(readTimeout).build();
		return HttpClients.custom().setDefaultRequestConfig(requestConfig).build();
	}

	/**
	 * 提取参数的值，优先级先header，后body
	 * 
	 * @param request
	 * @return
	 */
	public static String extractParam(HttpServletRequest request, String paramName) {
		if (StringUtils.isEmpty(paramName)) {
			return null;
		}
		String value = request.getHeader(paramName);
		if (StringUtils.isEmpty(value)) {
			value = request.getParameter(paramName);
		}
		return value;

	}

	public static void main(String[] args) {
		int readTimeout = 1000;
		int connectTimeout = 1000;
		String url = "http://www.baidu.com";
		getRequest(url, connectTimeout, readTimeout, null);

	}
}
