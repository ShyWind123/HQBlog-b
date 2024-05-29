package com.shywind.hqblog;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

/**
 * 启动类
 *
 */
//@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class })
@SpringBootApplication
@MapperScan("com.shywind.hqblog.mapper")
public class Application {
    public static void main( String[] args )
    {
        SpringApplication.run(Application.class, args);
    }
}
