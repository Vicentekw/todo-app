package com.example.todo.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("todo")
public class Todo {

    /** 主键 */
    private Long id;

    /** 所属用户 ID */
    @JsonIgnore
    private Long userId;

    /** 标题 */
    private String title;

    /** 描述 */
    private String description;

    /** 是否完成：0 未完成 1 已完成 */
    private Integer completed;

    /** 优先级：0 低 1 中 2 高 */
    private Integer priority;

    /** 截止日期 */
    private LocalDateTime dueDate;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    /** 逻辑删除字段 */
    @TableLogic
    @JsonIgnore
    private Integer deleted;
}
