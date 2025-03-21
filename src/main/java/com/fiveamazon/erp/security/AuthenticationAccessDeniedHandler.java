package com.fiveamazon.erp.security;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author chennan
 * @date 2018/8/7 15:51
 */

@Component
public class AuthenticationAccessDeniedHandler implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AccessDeniedException e) throws IOException, ServletException {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();
        String redirectUrl = "/login?notAuthority";
        for (GrantedAuthority grantedAuthority : userDetails.getAuthorities()) {
            String autority = grantedAuthority.getAuthority();
            if ("SYS_FORGOTPASSWORD".equalsIgnoreCase(autority)) {
                redirectUrl = "/changePassword?FORGOTPASSWORD";
                break;
            }
            if ("SYS_REGISTER".equalsIgnoreCase(autority)) {
                redirectUrl = "/changePassword?REGISTER";
                break;
            }
            if ("SYS_PASSWORDRANDOM".equalsIgnoreCase(autority)) {
                redirectUrl = "/changePassword?PASSWORDRANDOM";
                break;
            }
            if ("SYS_PASSWORDEXPIRED".equalsIgnoreCase(autority)) {
                redirectUrl = "/changePassword?PASSWORDEXPIRED";
                break;
            }
        }
        RequestDispatcher dispatcher = httpServletRequest.getRequestDispatcher(redirectUrl);
        dispatcher.forward(httpServletRequest, httpServletResponse);
    }
}