package com.example.todo.controller;

import com.example.todo.common.Result;
import com.example.todo.dto.ChangePasswordRequest;
import com.example.todo.dto.UpdateProfileRequest;
import com.example.todo.dto.UserProfile;
import com.example.todo.security.UserContext;
import com.example.todo.service.UserService;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 用户控制器：个人资料 / 修改密码
 * 通过 JWT 拦截器鉴权，从 UserContext 获取当前用户
 */
@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /** 查询当前用户信息 */
    @GetMapping("/profile")
    public Result<UserProfile> getProfile() {
        Long userId = UserContext.getCurrentUserId();
        return Result.success(userService.getProfile(userId));
    }

    /** 修改昵称 */
    @PutMapping("/profile")
    public Result<UserProfile> updateProfile(@Valid @RequestBody UpdateProfileRequest request) {
        Long userId = UserContext.getCurrentUserId();
        return Result.success("修改成功", userService.updateProfile(userId, request));
    }

    /** 修改密码 */
    @PutMapping("/password")
    public Result<Void> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        Long userId = UserContext.getCurrentUserId();
        userService.changePassword(userId, request);
        return Result.success();
    }
}
