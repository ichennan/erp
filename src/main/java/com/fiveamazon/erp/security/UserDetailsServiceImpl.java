package com.fiveamazon.erp.security;

import com.fiveamazon.erp.security.entity.SimpleUser;
import com.fiveamazon.erp.security.service.SimpleGroupService;
import com.fiveamazon.erp.security.service.SimpleUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

/**
 * @author chennan
 * @date 2018/8/7 15:51
 */

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    private SimpleUserService simpleUserService;
    @Autowired
    private SimpleGroupService simpleGroupService;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String userName) throws AuthenticationException {
        Set<GrantedAuthority> grantedAuthorities = new HashSet<>();
        grantedAuthorities.add(new SimpleGrantedAuthority("SYS_LOGIN"));
        boolean isActive = true;
        boolean isForbidden = false;
        // 问号分隔 是否忘记密码
        String[] array = userName.split("\\u003F");
        boolean forgotPassword = false;
        if (array.length > 1) {
            userName = array[0];
            forgotPassword = true;
            grantedAuthorities.add(new SimpleGrantedAuthority("SYS_FORGOTPASSWORD"));
            isActive = false;
        }
        //
        SimpleUser simpleUser = simpleUserService.getByUserName(userName);
        if (simpleUser == null) {
            if (forgotPassword) {
                throw new UsernameNotExistException("用户名不存在: + " + userName);
            }
            throw new UsernameNotFoundException("用户名不存在: + " + userName);
        }
//        if(vcpuser.getUserStatus() == VcpuserStatus.PWDINIT){
//            grantedAuthorities.add(new SimpleGrantedAuthority("SYS_REGISTER"));
//            isActive = false;
//        }else{
//            Date passwordChangeDate = simpleUser.getPasswordChangeDate();
//            if(passwordChangeDate == null){
//                grantedAuthorities.add(new SimpleGrantedAuthority("SYS_PASSWORDRANDOM"));
//                isActive = false;
//            }else{
//                Calendar passwordChangeDateCalendar = Calendar.getInstance();
//                passwordChangeDateCalendar.setTime(passwordChangeDate);
//                passwordChangeDateCalendar.add(Calendar.MONTH, 3);
//                if(passwordChangeDateCalendar.getTime().before(new Date())){
//                    grantedAuthorities.add(new SimpleGrantedAuthority("SYS_PASSWORDEXPIRED"));
//                    isActive = false;
//                }
//            }
//        }

        if (isActive) {
            grantedAuthorities.add(new SimpleGrantedAuthority("SYS_ACTIVE"));
            for (String authorityName : simpleGroupService.findSimpleAuthoriyListByUserName(userName)) {
                grantedAuthorities.add(new SimpleGrantedAuthority(authorityName));
            }
        }
        User user = new User(simpleUser.getUserName(),
//                forgotPassword ? BCRYPT_PASSWORD_ENCODER_BLANK : vcpuser.getPassword(),
                forgotPassword ? new BCryptPasswordEncoder().encode("") : simpleUser.getPassword(),
                true,
                true,
                !isForbidden,
                true,
                grantedAuthorities);
        return user;
    }
}
