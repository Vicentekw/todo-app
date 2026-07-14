package com.example.todo.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.todo.dto.TodoRequest;
import com.example.todo.entity.Todo;

public interface TodoService {

    /**
     * 分页查询当前用户的待办事项
     */
    IPage<Todo> page(Long userId, Integer pageNum, Integer pageSize, Integer completed, String keyword);

    /**
     * 查询单条待办（需校验归属）
     */
    Todo getById(Long userId, Long id);

    /**
     * 新增待办
     */
    Todo create(Long userId, TodoRequest request);

    /**
     * 修改待办（需校验归属）
     */
    Todo update(Long userId, Long id, TodoRequest request);

    /**
     * 切换完成状态（需校验归属）
     */
    Todo toggleCompleted(Long userId, Long id);

    /**
     * 删除待办（需校验归属）
     */
    void delete(Long userId, Long id);
}
