# ===== 后端 Dockerfile：多阶段构建 =====
# 阶段1：用 Maven 构建可执行 jar
FROM maven:3.9-eclipse-temurin-8 AS builder
WORKDIR /build
# 先复制 pom.xml 预下载依赖（利用缓存层）
COPY pom.xml .
RUN mvn dependency:go-offline -B
# 复制源码并打包
COPY src ./src
RUN mvn clean package -DskipTests -B

# 阶段2：精简运行镜像
FROM eclipse-temurin:8-jre-jammy
WORKDIR /app
COPY --from=builder /build/target/todo-backend.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar", "--spring.profiles.active=prod"]
