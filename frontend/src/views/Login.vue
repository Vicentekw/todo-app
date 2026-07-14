<template>
  <div class="login-container">
    <el-card class="login-card">
      <div class="login-header">
        <h2>待办事项管理系统</h2>
        <p>{{ isLogin ? '欢迎回来，请登录' : '创建你的账号' }}</p>
      </div>

      <el-tabs v-model="activeTab" class="login-tabs" @tab-change="handleTabChange">
        <el-tab-pane label="登录" name="login" />
        <el-tab-pane label="注册" name="register" />
      </el-tabs>

      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="0"
        @submit.prevent="handleSubmit"
      >
        <el-form-item prop="username">
          <el-input
            v-model="form.username"
            placeholder="请输入用户名"
            :prefix-icon="User"
            size="large"
          />
        </el-form-item>

        <el-form-item prop="password">
          <el-input
            v-model="form.password"
            type="password"
            placeholder="请输入密码"
            :prefix-icon="Lock"
            size="large"
            show-password
          />
        </el-form-item>

        <el-form-item v-if="!isLogin" prop="nickname">
          <el-input
            v-model="form.nickname"
            placeholder="请输入昵称（选填）"
            :prefix-icon="UserFilled"
            size="large"
          />
        </el-form-item>

        <el-form-item>
          <el-button
            type="primary"
            size="large"
            :loading="loading"
            class="submit-btn"
            @click="handleSubmit"
          >
            {{ isLogin ? '登 录' : '注 册' }}
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock, UserFilled } from '@element-plus/icons-vue'
import { login, register } from '@/api/auth'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()

const formRef = ref()
const activeTab = ref('login')
const loading = ref(false)

const isLogin = computed(() => activeTab.value === 'login')

const form = reactive({
  username: '',
  password: '',
  nickname: ''
})

const rules = computed(() => ({
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度在 3-20 个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在 6-20 个字符', trigger: 'blur' }
  ],
  nickname: isLogin.value
    ? []
    : [{ max: 50, message: '昵称长度不能超过 50 个字符', trigger: 'blur' }]
}))

function handleTabChange() {
  formRef.value?.resetFields()
}

async function handleSubmit() {
  try {
    await formRef.value.validate()
    loading.value = true

    if (isLogin.value) {
      const res = await login({
        username: form.username,
        password: form.password
      })
      userStore.setLogin(res.data)
      ElMessage.success('登录成功')
      router.push('/todo')
    } else {
      await register({
        username: form.username,
        password: form.password,
        nickname: form.nickname || undefined
      })
      ElMessage.success('注册成功，请登录')
      activeTab.value = 'login'
      form.password = ''
    }
  } catch (err) {
    // 校验失败或请求失败，拦截器已处理提示
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-container {
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.login-card {
  width: 420px;
  max-width: 90%;
  border-radius: 12px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.15);
}

.login-header {
  text-align: center;
  margin-bottom: 8px;
}

.login-header h2 {
  color: #303133;
  margin-bottom: 8px;
  font-size: 22px;
}

.login-header p {
  color: #909399;
  font-size: 14px;
}

.login-tabs {
  margin-bottom: 16px;
}

.submit-btn {
  width: 100%;
}
</style>
