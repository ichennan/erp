package com.fiveamazon.erp.common;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

/**
 * @author chennan
 * @date 2018/8/7 15:51
 */

public class SimpleCommonController {
	@Value("${spring.application.version}")
	String applicationVersion;

	protected Map<String, Object> parameters;
	public SimpleCommonController() {
	}

	@PostConstruct
	public void init() {
		this.parameters = new HashMap();
		this.parameters.put("msg", "test");
		this.parameters.put("version", applicationVersion);
		this.parameters.put("pageTitle", "5Amazon ERP");
		this.parameters.put("pageName", "5Amazon");
	}

	public final static String getUsername(){
		String username = "";
		try{
			UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			username = userDetails.getUsername();
			return username;
		}catch (Exception e){
			throw new SimpleCommonException("用户未登录");
		}finally {
			System.out.println("username: " + username);
		}
	}
}
