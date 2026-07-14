# ===== 前端 Dockerfile：多阶段构建 =====
# 阶段1：用 Node 构建静态文件
FROM node:20-alpine AS builder
WORKDIR /build
COPY package*.json ./
RUN npm config set registry https://registry.npmmirror.com && npm install
COPY . .
RUN npm run build

# 阶段2：用 nginx 托管静态文件
FROM nginx:alpine
COPY --from=builder /build/dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
