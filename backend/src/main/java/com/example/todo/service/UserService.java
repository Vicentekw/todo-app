package com.example.todo.service;

import com.example.todo.dto.ChangePasswordRequest;
import com.example.todo.dto.LoginRequest;
import com.example.todo.dto.LoginResponse;
import com.example.todo.dto.RegisterRequest;
import com.example.todo.dto.UpdateProfileRequest;
import com.example.todo.dto.UserProfile;

public interface UserService {

    /**
     * 用户注册
     */
    void register(RegisterRequest request);

    /**
     * 用户登录
     */
    LoginResponse login(LoginRequest request);

    /**
     * 查询当前用户信息
     */
    UserProfile getProfile(Long userId);

    /**
     * 修改昵称
     */
    UserProfile updateProfile(Long userId, UpdateProfileRequest request);

    /**
     * 修改密码
     */
    void changePassword(Long userId, ChangePasswordRequest request);
}
