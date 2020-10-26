package com.fiveamazon.erp;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class ErpApplicationTests {

    @Test
    public void contextLoads() {
        log.info("15818699587");
        log.info(new BCryptPasswordEncoder().encode("15818699587"));
    }

}
