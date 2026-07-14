package com.example.todo.security;

import com.example.todo.common.UnauthorizedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * JWT 登录拦截器
 * 校验请求头中的 token，并将用户信息存入线程上下文
 */
@Slf4j
@Component
public class JwtInterceptor implements HandlerInterceptor {

    private final JwtUtils jwtUtils;

    @Value("${jwt.header}")
    private String header;

    @Value("${jwt.prefix}")
    private String prefix;

    public JwtInterceptor(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 预检请求直接放行
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String authHeader = request.getHeader(header);
        if (!StringUtils.hasText(authHeader) || !authHeader.startsWith(prefix)) {
            throw new UnauthorizedException("未登录或 token 缺失");
        }

        String token = authHeader.substring(prefix.length()).trim();
        if (!jwtUtils.validateToken(token)) {
            throw new UnauthorizedException("token 无效或已过期，请重新登录");
        }

        // 解析用户信息并写入上下文
        Long userId = jwtUtils.getUserIdFromToken(token);
        String username = jwtUtils.getUsernameFromToken(token);
        UserContext.set(new UserContext.LoginUser(userId, username));
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        // 清理线程上下文，避免内存泄漏
        UserContext.clear();
    }
}
