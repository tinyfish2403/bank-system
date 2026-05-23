package com.bank.system;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.bank.system.mapper")
public class BankSystemApplication {
    public static void main(String[] args) {
        SpringApplication.run(BankSystemApplication.class, args);
    }
}