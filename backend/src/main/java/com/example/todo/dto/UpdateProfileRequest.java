package com.example.todo.dto;

import lombok.Data;

import javax.validation.constraints.Size;

/**
 * 修改个人资料请求
 */
@Data
public class UpdateProfileRequest {

    @Size(max = 50, message = "昵称长度不能超过 50 个字符")
    private String nickname;
}
