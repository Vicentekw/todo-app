package com.example.todo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 用户信息响应
 */
@Data
@AllArgsConstructor
public class UserProfile {

    private Long id;

    private String username;

    private String nickname;
}
