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
        <el-tooltip :content="isDark ? '切换到亮色' : '切换到暗色'" placement="bottom">
          <el-button text :icon="isDark ? Moon : Sunny" @click="toggleDark" />
        </el-tooltip>
        <el-button text :icon="User" @click="router.push('/profile')">个人中心</el-button>
        <el-button type="danger" text :icon="SwitchButton" @click="handleLogout">退出</el-button>
      </div>
    </el-header>

    <!-- 主体内容 -->
    <el-main class="todo-main">
      <!-- 统计面板 -->
      <el-row :gutter="16" class="stats-row">
        <el-col :span="4" v-for="item in statCards" :key="item.label">
          <el-card shadow="hover" class="stat-card">
            <div class="stat-content">
              <el-icon class="stat-icon" :style="{ color: item.color }">
                <component :is="item.icon" />
              </el-icon>
              <div class="stat-info">
                <div class="stat-value">{{ item.value }}</div>
                <div class="stat-label">{{ item.label }}</div>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>

      <el-card>
        <!-- 工具栏 -->
        <div class="toolbar">
          <div class="toolbar-left">
            <el-button type="primary" :icon="Plus" @click="handleAdd">新增待办</el-button>
            <el-button
              type="danger"
              :icon="Delete"
              :disabled="selectedIds.length === 0"
              @click="handleBatchDelete"
            >
              批量删除
            </el-button>
            <el-button
              type="success"
              :icon="Check"
              :disabled="selectedIds.length === 0"
              @click="handleBatchToggle"
            >
              批量完成
            </el-button>
          </div>
          <div class="toolbar-right">
            <el-select v-model="filter.completed" placeholder="完成状态" clearable style="width: 120px" @change="handleFilterChange">
              <el-option label="未完成" :value="0" />
              <el-option label="已完成" :value="1" />
            </el-select>
            <el-select v-model="filter.priority" placeholder="优先级" clearable style="width: 120px" @change="handleFilterChange">
              <el-option label="高" :value="2" />
              <el-option label="中" :value="1" />
              <el-option label="低" :value="0" />
            </el-select>
            <el-input
              v-model="filter.keyword"
              placeholder="搜索标题"
              clearable
              style="width: 180px"
              :prefix-icon="Search"
              @change="handleFilterChange"
            />
            <el-button :icon="Refresh" @click="handleReset">重置</el-button>
            <el-button-group>
              <el-button :type="viewMode === 'table' ? 'primary' : ''" :icon="Grid" @click="viewMode = 'table'" />
              <el-button :type="viewMode === 'card' ? 'primary' : ''" :icon="Menu" @click="viewMode = 'card'" />
            </el-button-group>
          </div>
        </div>

        <!-- 表格视图 -->
        <template v-if="viewMode === 'table'">
          <el-skeleton :rows="5" animated :loading="loading && tableData.length === 0">
            <template #default>
              <el-table :data="tableData" v-loading="loading" style="width: 100%" stripe @selection-change="handleSelectionChange">
                <el-table-column type="selection" width="45" />
                <el-table-column label="标题" prop="title" min-width="160">
                  <template #default="{ row }">
                    <span :class="{ 'todo-done': row.completed === 1 }">{{ row.title }}</span>
                  </template>
                </el-table-column>
                <el-table-column label="描述" prop="description" min-width="160" show-overflow-tooltip />
                <el-table-column label="优先级" width="90" align="center">
                  <template #default="{ row }">
                    <el-tag :type="priorityTagType(row.priority)" size="small">
                      {{ priorityText(row.priority) }}
                    </el-tag>
                  </template>
                </el-table-column>
                <el-table-column label="状态" width="90" align="center">
                  <template #default="{ row }">
                    <el-tag :type="row.completed === 1 ? 'success' : 'info'" size="small">
                      {{ row.completed === 1 ? '已完成' : '未完成' }}
                    </el-tag>
                  </template>
                </el-table-column>
                <el-table-column label="截止日期" width="140" align="center">
                  <template #default="{ row }">
                    <template v-if="row.dueDate">
                      <div :class="{ 'overdue-text': isOverdue(row) }">
                        {{ formatDateTime(row.dueDate) }}
                      </div>
                      <div class="countdown-text" :class="{ 'overdue-text': isOverdue(row) }">
                        {{ formatDueDateCountdown(row.dueDate).text }}
                      </div>
                    </template>
                    <span v-else class="muted-text">无</span>
                  </template>
                </el-table-column>
                <el-table-column label="创建时间" width="120" align="center">
                  <template #default="{ row }">
                    {{ formatRelativeTime(row.createdAt) }}
                  </template>
                </el-table-column>
                <el-table-column label="操作" width="200" align="center" fixed="right">
                  <template #default="{ row }">
                    <el-button size="small" :type="row.completed === 1 ? 'warning' : 'success'" @click="handleToggle(row)">
                      {{ row.completed === 1 ? '撤销' : '完成' }}
                    </el-button>
                    <el-button size="small" :icon="Edit" @click="handleEdit(row)">编辑</el-button>
                    <el-button size="small" type="danger" :icon="Delete" @click="handleDelete(row)" />
                  </template>
                </el-table-column>
                <template #empty>
                  <el-empty description="暂无待办事项，点击「新增待办」开始吧" />
                </template>
              </el-table>
            </template>
          </el-skeleton>
        </template>

        <!-- 卡片视图 -->
        <template v-else>
          <div v-loading="loading">
            <el-skeleton :rows="5" animated :loading="loading && tableData.length === 0">
              <template #default>
                <el-row :gutter="16" v-if="tableData.length > 0">
                  <el-col :span="6" v-for="row in tableData" :key="row.id" class="card-col">
                    <el-card shadow="hover" class="todo-card" :class="[`priority-border-${row.priority || 1}`]">
                      <div class="card-top">
                        <el-checkbox :model-value="row.completed === 1" @change="handleToggle(row)" />
                        <el-tag :type="priorityTagType(row.priority)" size="small">
                          {{ priorityText(row.priority) }}
                        </el-tag>
                      </div>
                      <div class="card-title" :class="{ 'todo-done': row.completed === 1 }">
                        {{ row.title }}
                      </div>
                      <div class="card-desc">{{ row.description || '暂无描述' }}</div>
                      <div class="card-footer">
                        <template v-if="row.dueDate">
                          <el-tag :type="isOverdue(row) ? 'danger' : 'warning'" size="small" effect="plain">
                            {{ formatDueDateCountdown(row.dueDate).text }}
                          </el-tag>
                        </template>
                        <span class="card-time">{{ formatRelativeTime(row.createdAt) }}</span>
                      </div>
                      <div class="card-actions">
                        <el-button size="small" :icon="Edit" @click="handleEdit(row)">编辑</el-button>
                        <el-button size="small" type="danger" :icon="Delete" @click="handleDelete(row)" />
                      </div>
                    </el-card>
                  </el-col>
                </el-row>
                <el-empty v-else description="暂无待办事项，点击「新增待办」开始吧" />
              </template>
            </el-skeleton>
          </div>
        </template>

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
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px" @closed="resetForm">
      <el-form ref="formRef" :model="form" :rules="formRules" label-width="80px">
        <el-form-item label="标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入标题" maxlength="200" show-word-limit />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="form.description" type="textarea" :rows="4" placeholder="请输入描述（选填）" maxlength="500" show-word-limit />
        </el-form-item>
        <el-form-item label="优先级" prop="priority">
          <el-select v-model="form.priority" placeholder="请选择优先级" style="width: 100%">
            <el-option label="高" :value="2">
              <el-tag type="danger" size="small">高</el-tag>
            </el-option>
            <el-option label="中" :value="1">
              <el-tag type="warning" size="small">中</el-tag>
            </el-option>
            <el-option label="低" :value="0">
              <el-tag type="info" size="small">低</el-tag>
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="截止日期" prop="dueDate">
          <el-date-picker
            v-model="form.dueDate"
            type="datetime"
            placeholder="请选择截止日期（选填）"
            format="YYYY-MM-DD HH:mm"
            value-format="YYYY-MM-DDTHH:mm:ss"
            style="width: 100%"
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
import { ref, reactive, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Plus, Edit, Delete, Search, Refresh, SwitchButton, Notebook,
  User, Sunny, Moon, Grid, Menu, Check, Document, Clock, Finished, Warning
} from '@element-plus/icons-vue'
import {
  getTodoList, getTodoStats, createTodo, updateTodo, toggleTodo, deleteTodo,
  batchToggleTodo, batchDeleteTodo
} from '@/api/todo'
import { useUserStore } from '@/stores/user'
import { useDark } from '@/composables/useDark'
import { formatRelativeTime, formatDateTime, formatDueDateCountdown } from '@/utils/format'

const router = useRouter()
const userStore = useUserStore()
const { isDark, toggleDark } = useDark()

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const viewMode = ref('table')
const selectedIds = ref([])

// ===== 统计 =====
const stats = ref({ total: 0, completed: 0, uncompleted: 0, overdue: 0, todayCreated: 0, completionRate: 0 })

const statCards = computed(() => [
  { label: '总待办', value: stats.value.total, icon: Document, color: '#409eff' },
  { label: '已完成', value: stats.value.completed, icon: Finished, color: '#67c23a' },
  { label: '未完成', value: stats.value.uncompleted, icon: Clock, color: '#e6a23c' },
  { label: '已逾期', value: stats.value.overdue, icon: Warning, color: '#f56c6c' },
  { label: '今日新增', value: stats.value.todayCreated, icon: Plus, color: '#909399' },
  { label: '完成率', value: stats.value.completionRate + '%', icon: Check, color: '#9b59b6' }
])

async function loadStats() {
  try {
    const res = await getTodoStats()
    stats.value = res.data
  } catch (err) {
    // 忽略统计加载失败
  }
}

const filter = reactive({
  pageNum: 1,
  pageSize: 10,
  completed: undefined,
  priority: undefined,
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
      priority: filter.priority,
      keyword: filter.keyword || undefined
    })
    tableData.value = res.data.records
    total.value = res.data.total
    loadStats()
  } finally {
    loading.value = false
  }
}

function handleFilterChange() {
  filter.pageNum = 1
  loadData()
}

function handleReset() {
  filter.pageNum = 1
  filter.completed = undefined
  filter.priority = undefined
  filter.keyword = ''
  loadData()
}

// ===== 优先级辅助 =====
function priorityText(priority) {
  const map = { 0: '低', 1: '中', 2: '高' }
  return map[priority] || '中'
}

function priorityTagType(priority) {
  const map = { 0: 'info', 1: 'warning', 2: 'danger' }
  return map[priority] || 'warning'
}

function isOverdue(row) {
  if (!row.dueDate || row.completed === 1) return false
  return new Date(row.dueDate) < new Date()
}

// ===== 批量操作 =====
function handleSelectionChange(rows) {
  selectedIds.value = rows.map(r => r.id)
}

async function handleBatchToggle() {
  try {
    await ElMessageBox.confirm(`确定批量完成选中的 ${selectedIds.value.length} 项吗？`, '提示', {
      type: 'warning', confirmButtonText: '确定', cancelButtonText: '取消'
    })
    await batchToggleTodo(selectedIds.value)
    ElMessage.success('批量完成成功')
    loadData()
  } catch (err) {
    // 用户取消
  }
}

async function handleBatchDelete() {
  try {
    await ElMessageBox.confirm(`确定批量删除选中的 ${selectedIds.value.length} 项吗？`, '提示', {
      type: 'warning', confirmButtonText: '确定', cancelButtonText: '取消'
    })
    await batchDeleteTodo(selectedIds.value)
    ElMessage.success('批量删除成功')
    loadData()
  } catch (err) {
    // 用户取消
  }
}

// ===== 新增 / 编辑 =====
const dialogVisible = ref(false)
const dialogTitle = ref('')
const submitting = ref(false)
const formRef = ref()
const editingId = ref(null)

const form = reactive({
  title: '',
  description: '',
  priority: 1,
  dueDate: null
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
  form.priority = row.priority ?? 1
  form.dueDate = row.dueDate || null
  dialogVisible.value = true
}

async function handleSubmit() {
  try {
    await formRef.value.validate()
    submitting.value = true
    const data = {
      title: form.title,
      description: form.description || undefined,
      priority: form.priority,
      dueDate: form.dueDate || undefined
    }
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
  form.priority = 1
  form.dueDate = null
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
      type: 'warning', confirmButtonText: '确定', cancelButtonText: '取消'
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

html.dark .todo-container {
  background: #141414;
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

html.dark .todo-header {
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

.welcome {
  color: #606266;
  font-size: 14px;
}

.todo-main {
  flex: 1;
  padding: 24px;
  overflow-y: auto;
}

/* 统计面板 */
.stats-row {
  margin-bottom: 16px;
}

.stat-card {
  text-align: center;
}

.stat-content {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
}

.stat-icon {
  font-size: 32px;
}

.stat-value {
  font-size: 24px;
  font-weight: 700;
  line-height: 1.2;
}

.stat-label {
  font-size: 12px;
  color: #909399;
}

/* 工具栏 */
.toolbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
  flex-wrap: wrap;
  gap: 12px;
}

.toolbar-left {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.toolbar-right {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
  align-items: center;
}

/* 表格样式 */
.todo-done {
  text-decoration: line-through;
  color: #909399;
}

.overdue-text {
  color: #f56c6c;
  font-weight: 600;
}

.countdown-text {
  font-size: 12px;
  color: #909399;
}

.muted-text {
  color: #c0c4cc;
}

/* 卡片视图 */
.card-col {
  margin-bottom: 16px;
}

.todo-card {
  border-left: 4px solid #e6a23c;
  height: 100%;
}

.priority-border-0 {
  border-left-color: #909399;
}

.priority-border-1 {
  border-left-color: #e6a23c;
}

.priority-border-2 {
  border-left-color: #f56c6c;
}

.card-top {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 8px;
}

.card-title {
  font-size: 16px;
  font-weight: 600;
  margin-bottom: 6px;
  word-break: break-word;
}

.card-desc {
  font-size: 13px;
  color: #909399;
  margin-bottom: 12px;
  min-height: 20px;
  word-break: break-word;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.card-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 8px;
}

.card-time {
  font-size: 12px;
  color: #c0c4cc;
}

.card-actions {
  display: flex;
  gap: 8px;
}

.pagination {
  margin-top: 16px;
  display: flex;
  justify-content: flex-end;
}
</style>
