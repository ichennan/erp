package com.fiveamazon.erp.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.logging.Logger;

/**
 * @author chennan
 * @date 2018/8/7 15:51
 */

public class LoginAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private static  final Logger logger=Logger.getLogger(LoginAuthenticationFilter.class.getName());

    public LoginAuthenticationFilter() {
    }

    @Override
    protected String obtainUsername(HttpServletRequest request) {
        return request.getParameter(getUsernameParameter()) +
                (StringUtils.isEmpty(request.getParameter("forgotPassword")) ? "" : "?FORGOTPASSWORD");
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        HttpSession session = request.getSession();
        session.setAttribute("LAST_USERNAME", request.getParameter("username"));
//        //忘记密码无需校验图形验证码
//        if(!StringUtils.isEmpty(request.getParameter("forgotPassword"))){
//            return super.attemptAuthentication(request, response);
//        }
//        //获取表单提交的验证码的值
//        String verification = request.getParameter("code");
//        //获取下发的存在session中的验证码的值
//        String captcha = (String) request.getSession().getAttribute(Constants.KAPTCHA_SESSION_KEY);
//        if (captcha==null){
//            throw new CaptchaException("验证码为空");
//        }
//        else  if (!captcha.contentEquals(verification)) {
//            throw new CaptchaException("验证码不匹配");
//        }
        //调用UsernamePasswordAuthenticationFilter的认证方法
        return super.attemptAuthentication(request, response);
    }
}
