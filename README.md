# Warlock

Warlock 是一个基于 Spring Boot 和 Vue.js 的现代化全栈应用程序，采用 Java 25 和最新的技术栈构建。该项目结合了强大的后端功能和响应式的前端界面，为用户提供高效、稳定的服务。

## 项目概述

Warlock 是一个企业级应用框架，集成了多种现代技术组件，包括：

- 后端：基于 Spring Boot 4.0.1 构建，使用 Java 25
- 前端：基于 Vue 3 框架的现代化用户界面
- 数据库：MySQL 数据库支持
- 缓存：Redisson 分布式缓存解决方案
- ORM：MyBatis-Flex 对象关系映射框架
- 定时任务：Quartz 调度框架

## 技术栈

### 后端技术
- **语言**: Java 25
- **框架**: Spring Boot 4.0.1
- **Web 服务器**: Jetty (替代默认的 Tomcat)
- **数据库连接**: MySQL Connector/J
- **ORM 框架**: MyBatis-Flex
- **缓存**: Redisson 4.1.0
- **定时任务**: Quartz Scheduler
- **安全**: Spring Security
- **验证**: Bean Validation
- **连接池**: HikariCP
- **工具库**: Apache Commons Lang3 & Collections4

### 前端技术
- **框架**: Vue.js 3.5+
- **构建工具**: Vite
- **类型检查**: TypeScript
- **包管理**: pnpm

### 开发与部署
- **构建工具**: Maven
- **容器化**: Docker Compose
- **开发工具**: Spring Boot DevTools

## 项目结构

```
warlock/
├── backend/                  # 后端 Spring Boot 应用
│   ├── src/main/java/        # Java 源代码
│   ├── src/main/resources/   # 配置文件和静态资源
│   └── pom.xml              # Maven 依赖配置
├── frontend/                 # 前端 Vue.js 应用
│   ├── src/                  # Vue 组件源代码
│   ├── public/               # 静态资源
│   ├── package.json          # npm 依赖配置
│   └── vite.config.ts        # Vite 构建配置
├── compose.yaml             # Docker Compose 配置
├── pom.xml                  # 根项目 Maven 配置
└── README.md                # 项目说明文档
```

## 安装与运行

### 环境要求

- Java 25 或更高版本
- Node.js 18+ 和 pnpm
- Maven 3.6+
- MySQL 8+ (或使用 Docker)
- Docker 和 Docker Compose (可选)

### 本地开发模式

#### 1. 克隆项目

```bash
git clone <repository-url>
cd warlock
```

#### 2. 启动数据库 (使用 Docker)

```bash
docker-compose up -d
```

#### 3. 启动后端服务

```bash
# 在项目根目录下运行
./mvnw spring-boot:run
```

或者先构建项目:

```bash
./mvnw clean install
java -jar backend/target/backend-1.0.0.jar
```

#### 4. 启动前端服务

```bash
cd frontend
pnpm install
pnpm run dev
```

前端将在 http://localhost:5173 上运行。

### 生产环境部署

#### 1. 构建前端

```bash
cd frontend
pnpm run build
```

#### 2. 构建后端 (自动包含前端资源)

```bash
cd ..
./mvnw clean package
```

#### 3. 运行应用

```bash
java -jar backend/target/backend-1.0.0.jar --spring.profiles.active=prod
```

## 配置

项目提供多个环境配置:

- `application.yml` - 主配置文件
- `application-dev.yml` - 开发环境配置
- `application-prod.yml` - 生产环境配置

## 特性

- **前后端分离架构**: 前端使用 Vue 3 + Vite，后端提供 RESTful API
- **现代化技术栈**: 使用最新的 Java 25 和 Spring Boot 4.x
- **自动化构建**: Maven 自动处理前后端集成构建
- **分布式缓存**: Redisson 提供高性能分布式缓存
- **定时任务**: Quartz 支持复杂的任务调度
- **安全认证**: Spring Security 提供完善的安全机制
- **数据库访问**: MyBatis-Flex 提供灵活的数据库操作
- **容器化部署**: Docker Compose 简化部署流程

## 开发

### 后端开发

后端主要位于 `backend` 目录下，使用标准的 Spring Boot 结构:

- 控制器 (Controller): 处理 HTTP 请求
- 服务层 (Service): 业务逻辑实现
- 数据访问层 (Mapper): 数据库操作接口
- 实体类 (Entity): 数据模型定义

### 前端开发

前端位于 `frontend` 目录下，使用 Vue 3 Composition API:

- 组件 (Components): 可复用 UI 组件
- 页面 (Pages): 应用页面
- API (API): 与后端通信的接口

## 贡献

欢迎提交 Issue 和 Pull Request 来改进本项目。

## 许可证

本项目使用 MIT 许可证 - 查看 [LICENSE](LICENSE) 文件了解详情。

## 作者

- **11's papa** - 初始开发者
- 邮箱: the2ndindec@gmail.com