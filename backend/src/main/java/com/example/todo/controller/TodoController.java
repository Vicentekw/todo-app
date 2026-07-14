package com.example.todo.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.todo.common.Result;
import com.example.todo.dto.TodoRequest;
import com.example.todo.entity.Todo;
import com.example.todo.security.UserContext;
import com.example.todo.service.TodoService;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 待办事项控制器
 * 所有接口均通过 JWT 拦截器鉴权，从 UserContext 获取当前用户
 */
@RestController
@RequestMapping("/todos")
public class TodoController {

    private final TodoService todoService;

    public TodoController(TodoService todoService) {
        this.todoService = todoService;
    }

    /** 分页查询待办列表 */
    @GetMapping
    public Result<IPage<Todo>> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Integer completed,
            @RequestParam(required = false) String keyword) {
        Long userId = UserContext.getCurrentUserId();
        return Result.success(todoService.page(userId, pageNum, pageSize, completed, keyword));
    }

    /** 查询单条待办 */
    @GetMapping("/{id}")
    public Result<Todo> get(@PathVariable Long id) {
        Long userId = UserContext.getCurrentUserId();
        return Result.success(todoService.getById(userId, id));
    }

    /** 新增待办 */
    @PostMapping
    public Result<Todo> create(@Valid @RequestBody TodoRequest request) {
        Long userId = UserContext.getCurrentUserId();
        return Result.success("新增成功", todoService.create(userId, request));
    }

    /** 修改待办 */
    @PutMapping("/{id}")
    public Result<Todo> update(@PathVariable Long id, @Valid @RequestBody TodoRequest request) {
        Long userId = UserContext.getCurrentUserId();
        return Result.success("修改成功", todoService.update(userId, id, request));
    }

    /** 切换完成状态 */
    @PatchMapping("/{id}/toggle")
    public Result<Todo> toggle(@PathVariable Long id) {
        Long userId = UserContext.getCurrentUserId();
        return Result.success(todoService.toggleCompleted(userId, id));
    }

    /** 删除待办 */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        Long userId = UserContext.getCurrentUserId();
        todoService.delete(userId, id);
        return Result.success();
    }
}
