package com.fiveamazon.erp;

import cn.hutool.core.date.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

/**
 * @author chennan
 * @date 2020/7/28 test by terry
 */

@Slf4j
@SpringBootApplication
public class ErpApplication extends SpringBootServletInitializer {
    public static void main(String[] args) {
        test();
        SpringApplication.run(ErpApplication.class, args);
    }

    public static void test(){
        String testStr = "sdfasdf|sdfasdf|sdfasdf111|";
        log.info(Arrays.asList(testStr.split("\\|")).toString());

        String test = "Jul 1, 2021 12:17:32 AM PDT";
//        Date abc = DateUtil.parse(test, "MMM d, yyyy h:m:s aa 'PDT'");
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM d, yyyy h:m:s aa 'PDT'", Locale.ENGLISH);
//        String test = "Jul 1, 2021 12:17:32 AM";
        Date abc = DateUtil.parse(test, dateFormat);
        log.info(test);
        log.info(DateUtil.format(abc, "yyyy-MM-dd HH:mm:ss"));


//        int i = 0;
//        for(i = 0; i < 1000; i ++){
//            int a = (int)(Math.random() * 10);
//            int b = (int)(Math.random() * 10);
//            System.out.print("" + a + " + " + b + " =                ");
//            if(i % 4 == 3){
//                System.out.println();
//                System.out.println();
//            }
//
//        }


    }
}
