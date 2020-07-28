package com.fiveamazon.erp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * @author chennan
 * @date 2020/7/28 test by terry
 */

@SpringBootApplication
public class ErpApplication extends SpringBootServletInitializer {
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(ErpApplication.class);
    }

    public static void main(String[] args) {
        SpringApplication.run(ErpApplication.class, args);
    }

}
