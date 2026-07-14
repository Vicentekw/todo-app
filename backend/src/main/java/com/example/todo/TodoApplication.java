package com.example.todo;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.example.todo.mapper")
public class TodoApplication {

    public static void main(String[] args) {
        SpringApplication.run(TodoApplication.class, args);
        System.out.println("" +
                "\n===============================" +
                "\n  Todo Backend 启动成功!" +
                "\n  接口地址: http://localhost:8080/api" +
                "\n===============================");
    }
}
