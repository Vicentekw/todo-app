# 待办事项管理系统（Todo）

前后端分离的待办事项管理系统，支持用户注册登录、待办事项的增删改查与完成状态管理。

## 技术栈

| 层级 | 技术 |
|------|------|
| 前端 | Vue 3 + Vite + Pinia + Vue Router + Element Plus + Axios |
| 后端 | Spring Boot 2.7.18 + MyBatis-Plus + JWT + BCrypt |
| 数据库 | MySQL 8.0 |
| 部署 | Docker Compose（前端 nginx + 后端 jar + mysql 三容器） |

## 功能

- 用户注册 / 登录 / 登出（JWT 认证）
- 待办事项新增 / 修改 / 删除 / 查询（分页）
- 标记完成 / 撤销完成
- 按完成状态筛选、按标题关键字搜索
- 数据隔离：每个用户只能操作自己的待办

## 项目结构

```
ZCodeProject/
├── frontend/              # Vue3 前端
│   ├── src/
│   │   ├── api/           # 接口定义
│   │   ├── router/        # 路由 + 登录守卫
│   │   ├── stores/        # Pinia 状态管理
│   │   ├── utils/         # axios 封装
│   │   └── views/         # Login.vue / Todo.vue
│   ├── Dockerfile
│   └── nginx.conf
├── backend/               # Spring Boot 后端
│   ├── src/main/java/com/example/todo/
│   │   ├── config/        # 配置类
│   │   ├── common/        # 统一响应、异常处理
│   │   ├── security/      # JWT 工具、拦截器
│   │   ├── controller/    # 控制器
│   │   ├── service/       # 业务逻辑
│   │   ├── mapper/        # MyBatis-Plus Mapper
│   │   ├── entity/        # 实体类
│   │   └── dto/           # 数据传输对象
│   └── Dockerfile
├── docker-compose.yml     # 容器编排
├── .env                   # 环境变量
└── README.md
```

## 本地开发

### 后端

```bash
cd backend
# 确保本机 MySQL 已启动，并已创建 todo_db 数据库
mvn spring-boot:run
# 后端运行在 http://localhost:8080/api
```

### 前端

```bash
cd frontend
npm install
npm run dev
# 前端运行在 http://localhost:5173（已配置 /api 代理到后端）
```

## Docker Compose 部署（推荐）

一键启动前端、后端、MySQL 三个容器：

```bash
# 在项目根目录执行
docker compose up --build -d

# 查看容器状态
docker compose ps

# 查看日志
docker compose logs -f backend

# 停止
docker compose down

# 停止并删除数据卷（清空数据库）
docker compose down -v
```

部署后访问：
- 前端页面：http://localhost
- 后端接口：http://localhost:8080/api

## 默认账号

首次使用需在登录页注册账号。也可通过接口注册：

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"123456","nickname":"管理员"}'
```

## API 接口一览

| 方法 | 路径 | 说明 | 鉴权 |
|------|------|------|------|
| POST | /api/auth/register | 用户注册 | 否 |
| POST | /api/auth/login | 用户登录 | 否 |
| GET | /api/todos | 分页查询待办 | 是 |
| GET | /api/todos/{id} | 查询单条待办 | 是 |
| POST | /api/todos | 新增待办 | 是 |
| PUT | /api/todos/{id} | 修改待办 | 是 |
| PATCH | /api/todos/{id}/toggle | 切换完成状态 | 是 |
| DELETE | /api/todos/{id} | 删除待办 | 是 |
