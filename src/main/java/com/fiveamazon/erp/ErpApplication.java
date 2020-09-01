package com.fiveamazon.erp;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * @author chennan
 * @date 2020/7/28 test by terry
 */

@Slf4j
@SpringBootApplication
public class ErpApplication extends SpringBootServletInitializer {
    public static void main(String[] args) {
        SpringApplication.run(ErpApplication.class, args);
    }
}
