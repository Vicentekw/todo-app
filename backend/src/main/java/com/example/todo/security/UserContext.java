package com.example.todo.security;

/**
 * 当前登录用户上下文（线程隔离）
 * 拦截器解析 token 后写入，Controller / Service 中可读取
 */
public class UserContext {

    private static final ThreadLocal<LoginUser> CURRENT_USER = new ThreadLocal<>();

    public static void set(LoginUser user) {
        CURRENT_USER.set(user);
    }

    public static LoginUser get() {
        return CURRENT_USER.get();
    }

    /** 获取当前用户 ID */
    public static Long getCurrentUserId() {
        LoginUser user = get();
        return user == null ? null : user.getUserId();
    }

    public static void clear() {
        CURRENT_USER.remove();
    }

    /**
     * 登录用户信息
     */
    public static class LoginUser {
        private final Long userId;
        private final String username;

        public LoginUser(Long userId, String username) {
            this.userId = userId;
            this.username = username;
        }

        public Long getUserId() {
            return userId;
        }

        public String getUsername() {
            return username;
        }
    }
}
