<template>
  <div class="profile-container">
    <!-- 顶部导航 -->
    <el-header class="profile-header">
      <div class="header-left">
        <el-icon class="logo-icon"><Notebook /></el-icon>
        <span class="title">个人中心</span>
      </div>
      <div class="header-right">
        <el-button text :icon="Back" @click="router.push('/todo')">返回待办</el-button>
        <el-button type="danger" text :icon="SwitchButton" @click="handleLogout">退出</el-button>
      </div>
    </el-header>

    <el-main class="profile-main">
      <el-row :gutter="24">
        <!-- 修改昵称 -->
        <el-col :span="12">
          <el-card>
            <template #header>
              <div class="card-header">
                <el-icon><User /></el-icon>
                <span>修改昵称</span>
              </div>
            </template>
            <el-form ref="profileFormRef" :model="profileForm" :rules="profileRules" label-width="80px">
              <el-form-item label="用户名">
                <el-input :value="userStore.userInfo?.username" disabled />
              </el-form-item>
              <el-form-item label="昵称" prop="nickname">
                <el-input v-model="profileForm.nickname" placeholder="请输入新昵称" maxlength="50" show-word-limit />
              </el-form-item>
              <el-form-item>
                <el-button type="primary" :loading="profileLoading" @click="handleUpdateProfile">保存</el-button>
              </el-form-item>
            </el-form>
          </el-card>
        </el-col>

        <!-- 修改密码 -->
        <el-col :span="12">
          <el-card>
            <template #header>
              <div class="card-header">
                <el-icon><Lock /></el-icon>
                <span>修改密码</span>
              </div>
            </template>
            <el-form ref="passwordFormRef" :model="passwordForm" :rules="passwordRules" label-width="80px">
              <el-form-item label="原密码" prop="oldPassword">
                <el-input v-model="passwordForm.oldPassword" type="password" show-password placeholder="请输入原密码" />
              </el-form-item>
              <el-form-item label="新密码" prop="newPassword">
                <el-input v-model="passwordForm.newPassword" type="password" show-password placeholder="请输入新密码" />
              </el-form-item>
              <el-form-item label="确认密码" prop="confirmPassword">
                <el-input v-model="passwordForm.confirmPassword" type="password" show-password placeholder="请再次输入新密码" />
              </el-form-item>
              <el-form-item>
                <el-button type="primary" :loading="passwordLoading" @click="handleChangePassword">修改密码</el-button>
              </el-form-item>
            </el-form>
          </el-card>
        </el-col>
      </el-row>
    </el-main>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Back, SwitchButton, Notebook, User, Lock } from '@element-plus/icons-vue'
import { getProfile, updateProfile, changePassword } from '@/api/user'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()

// ===== 修改昵称 =====
const profileFormRef = ref()
const profileLoading = ref(false)
const profileForm = reactive({
  nickname: ''
})
const profileRules = {
  nickname: [
    { required: true, message: '请输入昵称', trigger: 'blur' },
    { max: 50, message: '昵称长度不能超过 50 个字符', trigger: 'blur' }
  ]
}

async function handleUpdateProfile() {
  try {
    await profileFormRef.value.validate()
    profileLoading.value = true
    const res = await updateProfile({ nickname: profileForm.nickname })
    userStore.updateNickname(res.data.nickname)
    ElMessage.success('昵称修改成功')
  } catch (err) {
    // 校验或请求失败
  } finally {
    profileLoading.value = false
  }
}

// ===== 修改密码 =====
const passwordFormRef = ref()
const passwordLoading = ref(false)
const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})
const passwordRules = {
  oldPassword: [{ required: true, message: '请输入原密码', trigger: 'blur' }],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度需在 6-20 个字符之间', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入新密码', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value !== passwordForm.newPassword) {
          callback(new Error('两次输入的密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
}

async function handleChangePassword() {
  try {
    await passwordFormRef.value.validate()
    passwordLoading.value = true
    await changePassword({
      oldPassword: passwordForm.oldPassword,
      newPassword: passwordForm.newPassword
    })
    ElMessage.success('密码修改成功，请重新登录')
    userStore.logout()
    router.push('/login')
  } catch (err) {
    // 校验或请求失败
  } finally {
    passwordLoading.value = false
  }
}

// ===== 退出登录 =====
function handleLogout() {
  userStore.logout()
  ElMessage.success('已退出登录')
  router.push('/login')
}

onMounted(async () => {
  try {
    const res = await getProfile()
    profileForm.nickname = res.data.nickname
  } catch (err) {
    // 忽略
  }
})
</script>

<style scoped>
.profile-container {
  height: 100vh;
  display: flex;
  flex-direction: column;
  background: #f5f7fa;
}

html.dark .profile-container {
  background: #141414;
}

.profile-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  padding: 0 24px;
  height: 60px;
}

html.dark .profile-header {
  background: #1d1e1f;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.3);
}

.header-left {
  display: flex;
  align-items: center;
  gap: 8px;
}

.logo-icon {
  font-size: 24px;
  color: #409eff;
}

.title {
  font-size: 18px;
  font-weight: 600;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 8px;
}

.profile-main {
  flex: 1;
  padding: 24px;
  overflow-y: auto;
}

.card-header {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 600;
}
</style>
