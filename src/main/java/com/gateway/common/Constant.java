package com.gateway.common;

/**
 * 定义常量数据,避免魔法数 final 不允许继承修改
 * 
 * @author liqq
 *
 */
public final class Constant {

	/**
	 * 字符集
	 */
	public static final String CHARSET_UTF8 = "UTF-8";
	/**
	 * token参数
	 */
	public static final String TOKEN_PARAM = "token";
	/**
	 * filter key client-received,接收到客户端请求
	 */
	public static final String FILTER_KEY_CR = "cr";
	/**
	 * filter key client-send,转发客户端请求
	 */
	public static final String FILTER_KEY_CS = "cs";
	/**
	 * filter key server-received,接收到服务端响应
	 */
	public static final String FILTER_KEY_SR = "sr";
	/**
	 * filter key server-send,发生服务端响应给客户端
	 */
	public static final String FILTER_KEY_SS = "ss";

}
