package com.example.todo.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.todo.dto.TodoRequest;
import com.example.todo.entity.Todo;

import java.util.List;
import java.util.Map;

public interface TodoService {

    /**
     * 分页查询当前用户的待办事项
     *
     * @param userId    用户 ID
     * @param pageNum   页码
     * @param pageSize  每页条数
     * @param completed 完成状态筛选（null 不筛选）
     * @param priority  优先级筛选（null 不筛选）
     * @param keyword   标题关键字（null 不筛选）
     * @return 分页结果
     */
    IPage<Todo> page(Long userId, Integer pageNum, Integer pageSize, Integer completed, Integer priority, String keyword);

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
     * 批量切换完成状态
     *
     * @param userId 用户 ID
     * @param ids    待办 ID 列表
     * @return 实际更新条数
     */
    int batchToggle(Long userId, List<Long> ids);

    /**
     * 批量删除待办
     *
     * @param userId 用户 ID
     * @param ids    待办 ID 列表
     * @return 实际删除条数
     */
    int batchDelete(Long userId, List<Long> ids);

    /**
     * 删除待办（需校验归属）
     */
    void delete(Long userId, Long id);

    /**
     * 统计当前用户的待办数据
     *
     * @return 包含 total/completed/uncompleted/overdue/todayCreated/completionRate
     */
    Map<String, Object> stats(Long userId);
}
