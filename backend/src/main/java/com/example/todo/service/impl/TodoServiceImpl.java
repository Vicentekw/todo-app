package com.example.todo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.todo.common.BusinessException;
import com.example.todo.dto.TodoRequest;
import com.example.todo.entity.Todo;
import com.example.todo.mapper.TodoMapper;
import com.example.todo.service.TodoService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TodoServiceImpl implements TodoService {

    private final TodoMapper todoMapper;

    public TodoServiceImpl(TodoMapper todoMapper) {
        this.todoMapper = todoMapper;
    }

    @Override
    public IPage<Todo> page(Long userId, Integer pageNum, Integer pageSize, Integer completed, Integer priority, String keyword) {
        Page<Todo> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Todo> wrapper = new LambdaQueryWrapper<Todo>()
                .eq(Todo::getUserId, userId)
                .eq(completed != null, Todo::getCompleted, completed)
                .eq(priority != null, Todo::getPriority, priority)
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
        todo.setPriority(request.getPriority() != null ? request.getPriority() : 1);
        todo.setDueDate(request.getDueDate());
        todoMapper.insert(todo);
        return todo;
    }

    @Override
    public Todo update(Long userId, Long id, TodoRequest request) {
        Todo todo = getById(userId, id);
        todo.setTitle(request.getTitle());
        todo.setDescription(request.getDescription());
        if (request.getPriority() != null) {
            todo.setPriority(request.getPriority());
        }
        todo.setDueDate(request.getDueDate());
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
    public int batchToggle(Long userId, List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return 0;
        }
        LambdaUpdateWrapper<Todo> wrapper = new LambdaUpdateWrapper<Todo>()
                .eq(Todo::getUserId, userId)
                .in(Todo::getId, ids)
                .set(Todo::getCompleted, 1);
        return todoMapper.update(null, wrapper);
    }

    @Override
    public int batchDelete(Long userId, List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return 0;
        }
        LambdaQueryWrapper<Todo> wrapper = new LambdaQueryWrapper<Todo>()
                .eq(Todo::getUserId, userId)
                .in(Todo::getId, ids);
        return todoMapper.delete(wrapper);
    }

    @Override
    public void delete(Long userId, Long id) {
        Todo todo = getById(userId, id);
        todoMapper.deleteById(todo.getId());
    }

    @Override
    public Map<String, Object> stats(Long userId) {
        LambdaQueryWrapper<Todo> baseWrapper = new LambdaQueryWrapper<Todo>().eq(Todo::getUserId, userId);
        long total = todoMapper.selectCount(baseWrapper);

        long completedCount = todoMapper.selectCount(
                new LambdaQueryWrapper<Todo>().eq(Todo::getUserId, userId).eq(Todo::getCompleted, 1));

        long uncompletedCount = total - completedCount;

        // 逾期：未完成且截止日期早于现在
        long overdue = todoMapper.selectCount(
                new LambdaQueryWrapper<Todo>()
                        .eq(Todo::getUserId, userId)
                        .eq(Todo::getCompleted, 0)
                        .isNotNull(Todo::getDueDate)
                        .lt(Todo::getDueDate, LocalDateTime.now()));

        // 今日新增
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        long todayCreated = todoMapper.selectCount(
                new LambdaQueryWrapper<Todo>()
                        .eq(Todo::getUserId, userId)
                        .ge(Todo::getCreatedAt, todayStart));

        double completionRate = total == 0 ? 0 : (double) completedCount / total * 100;

        Map<String, Object> result = new HashMap<>();
        result.put("total", total);
        result.put("completed", completedCount);
        result.put("uncompleted", uncompletedCount);
        result.put("overdue", overdue);
        result.put("todayCreated", todayCreated);
        result.put("completionRate", Math.round(completionRate * 100) / 100.0);
        return result;
    }
}
