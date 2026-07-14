package com.example.todo.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class TodoRequest {

    @NotBlank(message = "标题不能为空")
    @Size(max = 200, message = "标题长度不能超过 200 个字符")
    private String title;

    @Size(max = 500, message = "描述长度不能超过 500 个字符")
    private String description;
}
