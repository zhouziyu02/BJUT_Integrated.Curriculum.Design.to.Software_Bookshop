package cn.svtcc.edu.mybookshop.utils;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.annotation.WebInitParam;
/**
 * 设置请求和响应编码
 *
 */
@WebFilter(
		filterName="CharaterFilter",//过滤器的名称
		initParams={@WebInitParam(name="encode",value="utf-8")},//初始化参数，指定编码为utf-8
		urlPatterns={"/*"}//过滤器的作用范围，匹配所有请求
		)
public class CharaterFilter implements Filter {
	private String encode;//编码属性
	@Override
	public void destroy() {
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		request.setCharacterEncoding(encode);//设置请求的编码
		response.setCharacterEncoding(encode);//设置响应的编码
		chain.doFilter(request, response);//继续执行其他过滤器或目标资源
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
		encode = arg0.getInitParameter("encode");//从配置中获取编码参数
	}

}
