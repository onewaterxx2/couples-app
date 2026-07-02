# 四叶草情侣 App 🍀

> 一个专为情侣设计的私密记录空间，支持照片相册、留言、心情、任务和位置共享。

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.1.0-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Android](https://img.shields.io/badge/Android-7.0%2B-blue.svg)](https://www.android.com/)

## 📱 项目简介

四叶草是一个全栈情侣社交应用，提供：
- 📸 **照片相册**：上传共同回忆，支持点赞和删除
- 💬 **留言板**：记录想说的话
- 😊 **心情日记**：记录每天的心情状态
- ✅ **任务清单**：共同维护待办事项
- 📍 **位置共享**：在双方允许时共享实时位置

## 🎯 在线演示

访问 [官网首页](http://onewater.work:8080) 查看项目介绍和下载APK

## 🏗️ 技术栈

### 后端
- **框架**: Spring Boot 4.1.0
- **语言**: Java 17
- **数据库**: MySQL 8.0
- **ORM**: Spring Data JPA / Hibernate
- **安全**: Spring Security
- **邮件**: Spring Mail (QQ SMTP)

### Android 客户端
- **语言**: Kotlin
- **最低版本**: Android 7.0 (API 24)
- **网络库**: Retrofit 2
- **架构**: MVVM

### Web 展示页面
- **技术**: 纯静态 HTML/CSS/JavaScript
- **特性**: 响应式设计

## 📦 快速开始

### 前置要求

- JDK 17+
- Maven 3.6+
- MySQL 8.0+
- Android Studio (开发Android端)
- Git

### 1️⃣ 克隆项目

```bash
git clone https://github.com/onewaterxx2/couples-app.git
cd couples-app
```

### 2️⃣ 配置数据库

创建MySQL数据库：

```sql
CREATE DATABASE couples_app CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER 'couples_user'@'localhost' IDENTIFIED BY 'your_password';
GRANT ALL PRIVILEGES ON couples_app.* TO 'couples_user'@'localhost';
FLUSH PRIVILEGES;
```

### 3️⃣ 配置后端

复制配置文件模板：

```bash
cd myphd/sever/src/main/resources
cp application.properties.example application.properties
```

编辑 `application.properties`，修改以下配置：

```properties
# 数据库连接
spring.datasource.url=jdbc:mysql://localhost:3306/couples_app?...
spring.datasource.username=couples_user
spring.datasource.password=your_password

# 邮件服务（用于注册验证码）
spring.mail.username=your_email@qq.com
spring.mail.password=your_qq_smtp_code
```

> 💡 **如何获取QQ邮箱SMTP授权码**：
> 1. 登录QQ邮箱 → 设置 → 账户
> 2. 找到"POP3/IMAP/SMTP/Exchange/CardDAV/CalDAV服务"
> 3. 开启"SMTP服务"，获取授权码

### 4️⃣ 启动后端

```bash
cd myphd/sever
./mvnw spring-boot:run
```

后端将在 `http://localhost:8080` 启动。

### 5️⃣ 配置 Android 客户端

修改 API 地址：

```kotlin
// myp/app/src/main/java/com/example/myp/data/api/RetrofitClient.kt

private const val BASE_URL = "http://your-server-ip:8080/api/"
// 如果本地测试使用: http://10.0.2.2:8080/api/ (Android模拟器)
```

### 6️⃣ 构建 Android APK

```bash
cd myp
./gradlew assembleDebug
# APK生成路径: app/build/outputs/apk/debug/app-debug.apk
```

## 📖 API 文档

### 认证相关

| 接口 | 方法 | 描述 |
|------|------|------|
| `/api/auth/send-code` | POST | 发送邮箱验证码 |
| `/api/auth/register` | POST | 用户注册 |
| `/api/auth/login` | POST | 用户登录 |
| `/api/auth/create-couple` | POST | 创建情侣邀请码 |
| `/api/auth/join-couple` | POST | 加入情侣空间 |
| `/api/auth/couple-status` | GET | 获取情侣状态 |

### 照片相册

| 接口 | 方法 | 描述 |
|------|------|------|
| `/api/photos/upload` | POST | 上传照片 |
| `/api/photos/list` | GET | 获取照片列表（分页）|
| `/api/photos/all` | GET | 获取所有照片 |
| `/api/photos/{id}/like` | POST | 点赞照片 |
| `/api/photos/{id}` | DELETE | 删除照片 |

### 其他模块

- `/api/messages/*` - 留言功能
- `/api/moods/*` - 心情记录
- `/api/tasks/*` - 任务管理
- `/api/locations/*` - 位置共享

详细API文档请参考 [API.md](docs/API.md)（待补充）

## 🚀 部署指南

详细的云服务器部署步骤请查看 [部署配置说明.md](部署配置说明.md)

### 简要步骤

1. 购买云服务器（阿里云/腾讯云，2核2GB即可）
2. 安装 Java 17 和 MySQL 8.0
3. 修改配置文件中的数据库和邮件配置
4. 打包后端：`./mvnw clean package -DskipTests`
5. 上传 JAR 到服务器并运行
6. 配置 Nginx 反向代理（可选）
7. 配置 HTTPS 证书（推荐使用 Let's Encrypt）

## ⚙️ 配置说明

### 环境变量（推荐生产环境使用）

```bash
# 设置环境变量
export DB_HOST=localhost
export DB_PORT=3306
export DB_NAME=couples_app
export DB_USERNAME=couples_user
export DB_PASSWORD=your_password
export MAIL_USERNAME=your_email@qq.com
export MAIL_PASSWORD=your_smtp_code
```

然后在 `application.properties` 中引用：

```properties
spring.datasource.url=jdbc:mysql://${DB_HOST}:${DB_PORT}/${DB_NAME}
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}
spring.mail.username=${MAIL_USERNAME}
spring.mail.password=${MAIL_PASSWORD}
```

## 🔒 安全建议

⚠️ **重要提示**：当前版本适合学习和开发，生产环境需要加强安全性：

- [ ] 实现 JWT Token 认证（目前仅依赖 userId）
- [ ] 所有敏感信息使用环境变量
- [ ] 启用 HTTPS（必须）
- [ ] 添加请求频率限制
- [ ] 数据库连接使用非 root 账户
- [ ] 文件上传增加类型和大小验证
- [ ] 密码使用 BCrypt 加密存储

## 📂 项目结构

```
couples-app/
├── myphd/sever/              # Spring Boot 后端
│   ├── src/main/java/
│   │   └── com/example/sever/
│   │       ├── controller/   # REST API 控制器
│   │       ├── entity/       # JPA 实体类
│   │       ├── repository/   # 数据访问层
│   │       ├── service/      # 业务逻辑层
│   │       └── config/       # 配置类
│   ├── src/main/resources/
│   │   ├── application.properties  # 配置文件
│   │   └── static/          # 静态资源（官网）
│   └── pom.xml              # Maven 依赖
│
├── myp/                     # Android 客户端
│   ├── app/src/main/
│   │   ├── java/            # Kotlin 源代码
│   │   └── res/             # 资源文件
│   └── build.gradle         # Gradle 配置
│
├── 部署配置说明.md          # 详细部署文档
├── README.md                # 项目说明（本文件）
└── .gitignore               # Git 忽略规则
```

## 🤝 贡献指南

欢迎提交 Issue 和 Pull Request！

1. Fork 本项目
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交改动 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 提交 Pull Request

## 📝 开发路线图

- [ ] 实现 JWT 认证
- [ ] 添加消息推送功能
- [ ] 支持视频上传
- [ ] 开发 iOS 客户端
- [ ] 添加纪念日提醒
- [ ] 支持照片编辑和滤镜
- [ ] 实现深色主题

## 🐛 已知问题

- API 缺少统一的鉴权机制
- 大文件上传性能有待优化
- Android 客户端缺少离线缓存

## 📄 许可证

本项目采用 MIT 许可证 - 详见 [LICENSE](LICENSE) 文件

## 👨‍💻 作者

- GitHub: [@onewaterxx2](https://github.com/onewaterxx2)
- Email: onewaterxx2@gmail.com
- 官网: [onewater.work:8080](http://onewater.work:8080)

## 🙏 致谢

- [Spring Boot](https://spring.io/projects/spring-boot)
- [Android Developers](https://developer.android.com/)
- [Retrofit](https://square.github.io/retrofit/)

## 📞 联系方式

如果有任何问题或建议，欢迎通过以下方式联系：

- 提交 [Issue](https://github.com/onewaterxx2/couples-app/issues)
- 发送邮件到：onewaterxx2@gmail.com
- 访问官网：[onewater.work:8080](http://onewater.work:8080)

---

⭐ 如果这个项目对你有帮助，欢迎 Star 支持！
