package com.xunwu.leida;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.xunwu.leida.mapper")
public class LeidaApplication {

    public static void main(String[] args) {
        SpringApplication.run(LeidaApplication.class, args);
    }
}
