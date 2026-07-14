import request from '@/utils/request'

// 分页查询待办列表
export function getTodoList(params) {
  return request({
    url: '/todos',
    method: 'get',
    params
  })
}

// 查询待办统计
export function getTodoStats() {
  return request({
    url: '/todos/stats',
    method: 'get'
  })
}

// 查询单条待办
export function getTodo(id) {
  return request({
    url: `/todos/${id}`,
    method: 'get'
  })
}

// 新增待办
export function createTodo(data) {
  return request({
    url: '/todos',
    method: 'post',
    data
  })
}

// 修改待办
export function updateTodo(id, data) {
  return request({
    url: `/todos/${id}`,
    method: 'put',
    data
  })
}

// 切换完成状态
export function toggleTodo(id) {
  return request({
    url: `/todos/${id}/toggle`,
    method: 'patch'
  })
}

// 批量完成
export function batchToggleTodo(ids) {
  return request({
    url: '/todos/batch/toggle',
    method: 'patch',
    data: { ids }
  })
}

// 批量删除
export function batchDeleteTodo(ids) {
  return request({
    url: '/todos/batch',
    method: 'delete',
    data: { ids }
  })
}

// 删除待办
export function deleteTodo(id) {
  return request({
    url: `/todos/${id}`,
    method: 'delete'
  })
}
