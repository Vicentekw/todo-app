package com.example.todo.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("user")
public class User {

    /** 主键 */
    private Long id;

    /** 用户名 */
    private String username;

    /** 密码（BCrypt 加密），不返回给前端 */
    @JsonIgnore
    private String password;

    /** 昵称 */
    private String nickname;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    /** 逻辑删除字段 */
    @TableLogic
    @JsonIgnore
    private Integer deleted;
}
