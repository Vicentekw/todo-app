package com.example.todo.common;

/**
 * 未授权异常（token 缺失 / 无效 / 过期）
 */
public class UnauthorizedException extends RuntimeException {

    public UnauthorizedException(String message) {
        super(message);
    }
}
