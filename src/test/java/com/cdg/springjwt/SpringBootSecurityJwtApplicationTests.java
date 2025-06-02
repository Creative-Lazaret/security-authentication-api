package com.cdg.springjwt;

import com.cdg.springjwt.services.MailService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest
public class SpringBootSecurityJwtApplicationTests {

    @MockBean
    private MailService mailService;

    @Test
    public void contextLoads() {
    }
}