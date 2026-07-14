import request from '@/utils/request'

// 查询当前用户信息
export function getProfile() {
  return request({
    url: '/user/profile',
    method: 'get'
  })
}

// 修改昵称
export function updateProfile(data) {
  return request({
    url: '/user/profile',
    method: 'put',
    data
  })
}

// 修改密码
export function changePassword(data) {
  return request({
    url: '/user/password',
    method: 'put',
    data
  })
}
