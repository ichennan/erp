package com.fiveamazon.erp.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.*;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.ExceptionMappingAuthenticationFailureHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.HashMap;
import java.util.Map;

/**
 * @author chennan
 * @date 2018/8/7 15:51
 */

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    @Qualifier(value = "userDetailsServiceImpl")
    private UserDetailsService userDetailsService;
    @Autowired
    AuthenticationAccessDeniedHandler authenticationAccessDeniedHandler;

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        // Admin
        // Admin
        //$2a$10$6G5TOCsYD0pQcpY9e1Vc6.uNfkyexvpUuotiLkoDfLcV2ajhgIqBW
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        System.out.println("WebSecurityConfig.configure");
        http
                .headers().frameOptions().sameOrigin()
                .and()
                .csrf().disable().addFilterBefore(loginAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling().accessDeniedHandler(authenticationAccessDeniedHandler)
                .and()
                .authorizeRequests()
                .antMatchers("/login").permitAll()
                .antMatchers("/product/**").hasAnyAuthority("ROLE_Product", "ROLE_Product_Edit")
                .antMatchers("/welcome/**").access("hasAuthority('SYS_LOGIN')")
                .antMatchers("/changePassword/**").access("hasAuthority('SYS_LOGIN')")
                .anyRequest().hasAnyAuthority("SYS_LOGIN")
//				.anyRequest().permitAll()
                .and()
                .formLogin()
                .loginPage("/login")
                .permitAll()
                .and()
                .logout()
                .permitAll();
    }


    @Override
    public void configure(WebSecurity web) {
        web.ignoring().antMatchers("/resources/**", "/static/**", "/css/**", "/js/**", "/img/**", "/webjars/**");
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setHideUserNotFoundExceptions(false);
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(bCryptPasswordEncoder());
        return provider;
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) {
        auth.authenticationProvider(authenticationProvider());
    }


    /*声明LoginAuthenticationFilter Bean*/
    @Bean
    public LoginAuthenticationFilter loginAuthenticationFilter() throws Exception {
        System.out.println("WebSecurityConfig.loginAuthenticationFilter");
        // 增加自定义拦截器
        LoginAuthenticationFilter loginAuthenticationFilter = new LoginAuthenticationFilter();
        // 需要增加，虽然没有任何配置
        loginAuthenticationFilter.setAuthenticationManager(authenticationManagerBean());
        // 增加自定义的错误参数
        loginAuthenticationFilter.setAuthenticationFailureHandler(authenticationFailureHandler());
        // 默认设置：跳回登陆前页面
        // loginAuthenticationFilter.setAuthenticationSuccessHandler(new SavedRequestAwareAuthenticationSuccessHandler());
        return loginAuthenticationFilter;
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    /*声明授权失败异常处理 Bean*/
    @Bean
    public AuthenticationFailureHandler authenticationFailureHandler() {
        ExceptionMappingAuthenticationFailureHandler failureHandler = new ExceptionMappingAuthenticationFailureHandler();
        Map<String, String> failureUrlMap = new HashMap<>();
        failureUrlMap.put(UsernameNotFoundException.class.getName(), "/login?invalidUsername");
        failureUrlMap.put(UsernameNotExistException.class.getName(), "/login?forgotPassword=invalidUsername");
        failureUrlMap.put(BadCredentialsException.class.getName(), "/login?invalidPassword");
        failureUrlMap.put(CredentialsExpiredException.class.getName(), "/login?accountForbidden");
        failureUrlMap.put(AccountExpiredException.class.getName(), "/login?error");
        failureUrlMap.put(LockedException.class.getName(), "/login?accountLocked");
        failureUrlMap.put(DisabledException.class.getName(), "/login?accountDisabled");
        failureHandler.setExceptionMappings(failureUrlMap);
        return failureHandler;
    }

}
