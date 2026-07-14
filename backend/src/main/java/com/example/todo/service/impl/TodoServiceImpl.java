package com.example.todo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.todo.common.BusinessException;
import com.example.todo.dto.TodoRequest;
import com.example.todo.entity.Todo;
import com.example.todo.mapper.TodoMapper;
import com.example.todo.service.TodoService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class TodoServiceImpl implements TodoService {

    private final TodoMapper todoMapper;

    public TodoServiceImpl(TodoMapper todoMapper) {
        this.todoMapper = todoMapper;
    }

    @Override
    public IPage<Todo> page(Long userId, Integer pageNum, Integer pageSize, Integer completed, String keyword) {
        Page<Todo> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Todo> wrapper = new LambdaQueryWrapper<Todo>()
                .eq(Todo::getUserId, userId)
                .eq(completed != null, Todo::getCompleted, completed)
                .like(StringUtils.hasText(keyword), Todo::getTitle, keyword)
                .orderByDesc(Todo::getCreatedAt);
        return todoMapper.selectPage(page, wrapper);
    }

    @Override
    public Todo getById(Long userId, Long id) {
        Todo todo = todoMapper.selectById(id);
        if (todo == null || !todo.getUserId().equals(userId)) {
            throw new BusinessException("待办事项不存在或无权访问");
        }
        return todo;
    }

    @Override
    public Todo create(Long userId, TodoRequest request) {
        Todo todo = new Todo();
        todo.setUserId(userId);
        todo.setTitle(request.getTitle());
        todo.setDescription(request.getDescription());
        todo.setCompleted(0);
        todoMapper.insert(todo);
        return todo;
    }

    @Override
    public Todo update(Long userId, Long id, TodoRequest request) {
        Todo todo = getById(userId, id);
        todo.setTitle(request.getTitle());
        todo.setDescription(request.getDescription());
        todoMapper.updateById(todo);
        return todo;
    }

    @Override
    public Todo toggleCompleted(Long userId, Long id) {
        Todo todo = getById(userId, id);
        todo.setCompleted(todo.getCompleted() == 0 ? 1 : 0);
        todoMapper.updateById(todo);
        return todo;
    }

    @Override
    public void delete(Long userId, Long id) {
        Todo todo = getById(userId, id);
        todoMapper.deleteById(todo.getId());
    }
}
