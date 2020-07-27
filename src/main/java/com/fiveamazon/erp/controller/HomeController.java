package com.fiveamazon.erp.controller;

import com.fiveamazon.erp.common.SimpleCommonController;
import com.fiveamazon.erp.common.SimpleCommonException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;

/**
 * @author chennan
 * @date 2018/8/7 15:51
 */

@Controller
@Slf4j
public class HomeController extends SimpleCommonController {
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String empty() {
		return "redirect:/home";
	}

	@RequestMapping(value = "/home", method = RequestMethod.GET)
	public ModelAndView home() {
		this.parameters.put("pageName", "home");
		UserDetails userDetails;
		try {
			userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		} catch (Exception e) {
			throw new SimpleCommonException("用户未登录");
		}
		List<String> userRoles = new ArrayList<>();
		for(GrantedAuthority grantedAuthority : userDetails.getAuthorities()){
			userRoles.add(grantedAuthority.getAuthority());
		}
		this.parameters.put("userRoles", userRoles);
		ModelAndView mv = new ModelAndView("home", parameters);
		return mv;
	}

	@RequestMapping(value = "/welcome", method = RequestMethod.GET)
	public ModelAndView welcome() {
		this.parameters.put("pageName", "welcome");
		ModelAndView mv = new ModelAndView("welcome", parameters);
		return mv;
	}

	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public ModelAndView login() {
		ModelAndView mv = new ModelAndView("login");
		return mv;
	}
}

