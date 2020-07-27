package com.fiveamazon.erp.security;

import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * @author chennan
 * @date 2018/8/7 15:51
 */

public class UsernameNotExistException extends UsernameNotFoundException {
    public UsernameNotExistException(String msg) {
        super(msg);
    }

    public UsernameNotExistException(String msg, Throwable t) {
        super(msg, t);
    }
}
