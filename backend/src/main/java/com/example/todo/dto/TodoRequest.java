package com.example.todo.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
public class TodoRequest {

    @NotBlank(message = "标题不能为空")
    @Size(max = 200, message = "标题长度不能超过 200 个字符")
    private String title;

    @Size(max = 500, message = "描述长度不能超过 500 个字符")
    private String description;

    /** 优先级：0 低 1 中 2 高，默认 1 */
    private Integer priority;

    /** 截止日期（可空） */
    private LocalDateTime dueDate;
}
