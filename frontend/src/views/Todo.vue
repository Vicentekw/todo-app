<template>
  <div class="todo-container">
    <!-- 顶部导航 -->
    <el-header class="todo-header">
      <div class="header-left">
        <el-icon class="logo-icon"><Notebook /></el-icon>
        <span class="title">待办事项管理</span>
      </div>
      <div class="header-right">
        <span class="welcome">你好，{{ userStore.userInfo?.nickname }}</span>
        <el-button type="danger" text :icon="SwitchButton" @click="handleLogout">
          退出
        </el-button>
      </div>
    </el-header>

    <!-- 主体内容 -->
    <el-main class="todo-main">
      <el-card>
        <!-- 工具栏 -->
        <div class="toolbar">
          <div class="toolbar-left">
            <el-button type="primary" :icon="Plus" @click="handleAdd">新增待办</el-button>
          </div>
          <div class="toolbar-right">
            <el-select
              v-model="filter.completed"
              placeholder="完成状态"
              clearable
              style="width: 140px"
              @change="loadData"
            >
              <el-option label="未完成" :value="0" />
              <el-option label="已完成" :value="1" />
            </el-select>
            <el-input
              v-model="filter.keyword"
              placeholder="搜索标题"
              clearable
              style="width: 200px"
              :prefix-icon="Search"
              @change="loadData"
            />
            <el-button :icon="Refresh" @click="handleReset">重置</el-button>
          </div>
        </div>

        <!-- 待办列表 -->
        <el-table :data="tableData" v-loading="loading" style="width: 100%" stripe>
          <el-table-column label="标题" prop="title" min-width="180">
            <template #default="{ row }">
              <span :class="{ 'todo-done': row.completed === 1 }">{{ row.title }}</span>
            </template>
          </el-table-column>
          <el-table-column label="描述" prop="description" min-width="200" show-overflow-tooltip />
          <el-table-column label="状态" width="100" align="center">
            <template #default="{ row }">
              <el-tag :type="row.completed === 1 ? 'success' : 'info'">
                {{ row.completed === 1 ? '已完成' : '未完成' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="创建时间" prop="createdAt" width="180" />
          <el-table-column label="操作" width="220" align="center" fixed="right">
            <template #default="{ row }">
              <el-button
                size="small"
                :type="row.completed === 1 ? 'warning' : 'success'"
                @click="handleToggle(row)"
              >
                {{ row.completed === 1 ? '撤销' : '完成' }}
              </el-button>
              <el-button size="small" :icon="Edit" @click="handleEdit(row)">编辑</el-button>
              <el-button size="small" type="danger" :icon="Delete" @click="handleDelete(row)" />
            </template>
          </el-table-column>
        </el-table>

        <!-- 分页 -->
        <div class="pagination">
          <el-pagination
            v-model:current-page="filter.pageNum"
            v-model:page-size="filter.pageSize"
            :page-sizes="[5, 10, 20, 50]"
            :total="total"
            layout="total, sizes, prev, pager, next, jumper"
            @size-change="loadData"
            @current-change="loadData"
          />
        </div>
      </el-card>
    </el-main>

    <!-- 新增/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="500px"
      @closed="resetForm"
    >
      <el-form ref="formRef" :model="form" :rules="formRules" label-width="60px">
        <el-form-item label="标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入标题" maxlength="200" show-word-limit />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input
            v-model="form.description"
            type="textarea"
            :rows="4"
            placeholder="请输入描述（选填）"
            maxlength="500"
            show-word-limit
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Plus, Edit, Delete, Search, Refresh, SwitchButton, Notebook
} from '@element-plus/icons-vue'
import {
  getTodoList, createTodo, updateTodo, toggleTodo, deleteTodo
} from '@/api/todo'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()

const loading = ref(false)
const tableData = ref([])
const total = ref(0)

const filter = reactive({
  pageNum: 1,
  pageSize: 10,
  completed: undefined,
  keyword: ''
})

// 加载待办列表
async function loadData() {
  loading.value = true
  try {
    const res = await getTodoList({
      pageNum: filter.pageNum,
      pageSize: filter.pageSize,
      completed: filter.completed,
      keyword: filter.keyword || undefined
    })
    tableData.value = res.data.records
    total.value = res.data.total
  } finally {
    loading.value = false
  }
}

function handleReset() {
  filter.pageNum = 1
  filter.completed = undefined
  filter.keyword = ''
  loadData()
}

// ===== 新增 / 编辑 =====
const dialogVisible = ref(false)
const dialogTitle = ref('')
const submitting = ref(false)
const formRef = ref()
const editingId = ref(null)

const form = reactive({
  title: '',
  description: ''
})

const formRules = {
  title: [
    { required: true, message: '请输入标题', trigger: 'blur' },
    { max: 200, message: '标题长度不能超过 200 个字符', trigger: 'blur' }
  ]
}

function handleAdd() {
  editingId.value = null
  dialogTitle.value = '新增待办'
  dialogVisible.value = true
}

function handleEdit(row) {
  editingId.value = row.id
  dialogTitle.value = '编辑待办'
  form.title = row.title
  form.description = row.description || ''
  dialogVisible.value = true
}

async function handleSubmit() {
  try {
    await formRef.value.validate()
    submitting.value = true
    const data = { title: form.title, description: form.description || undefined }
    if (editingId.value) {
      await updateTodo(editingId.value, data)
      ElMessage.success('修改成功')
    } else {
      await createTodo(data)
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    loadData()
  } catch (err) {
    // 校验或请求失败
  } finally {
    submitting.value = false
  }
}

function resetForm() {
  form.title = ''
  form.description = ''
  editingId.value = null
  formRef.value?.resetFields()
}

// ===== 切换完成 =====
async function handleToggle(row) {
  await toggleTodo(row.id)
  ElMessage.success(row.completed === 1 ? '已撤销完成' : '已完成')
  loadData()
}

// ===== 删除 =====
async function handleDelete(row) {
  try {
    await ElMessageBox.confirm('确定删除该待办事项吗？', '提示', {
      type: 'warning',
      confirmButtonText: '确定',
      cancelButtonText: '取消'
    })
    await deleteTodo(row.id)
    ElMessage.success('删除成功')
    loadData()
  } catch (err) {
    // 用户取消删除
  }
}

// ===== 退出登录 =====
function handleLogout() {
  userStore.logout()
  ElMessage.success('已退出登录')
  router.push('/login')
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.todo-container {
  height: 100vh;
  display: flex;
  flex-direction: column;
  background: #f5f7fa;
}

.todo-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  padding: 0 24px;
  height: 60px;
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
  color: #303133;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 16px;
}

.welcome {
  color: #606266;
  font-size: 14px;
}

.todo-main {
  flex: 1;
  padding: 24px;
  overflow-y: auto;
}

.toolbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
  flex-wrap: wrap;
  gap: 12px;
}

.toolbar-right {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.todo-done {
  text-decoration: line-through;
  color: #909399;
}

.pagination {
  margin-top: 16px;
  display: flex;
  justify-content: flex-end;
}
</style>
