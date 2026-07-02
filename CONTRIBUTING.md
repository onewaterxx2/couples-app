# 贡献指南

感谢你考虑为四叶草情侣 App 项目做出贡献！

## 如何贡献

### 报告 Bug

如果你发现了 Bug，请通过 GitHub Issues 报告：

1. 使用清晰描述性的标题
2. 详细描述复现步骤
3. 提供期望的行为和实际行为
4. 如果可能，提供截图或错误日志
5. 说明你的环境（操作系统、Java版本、Android版本等）

### 提出新功能

我们欢迎新功能建议！请：

1. 先检查是否已有相似的 Issue
2. 清楚地描述功能的用例和价值
3. 如果可能，提供实现思路

### 提交代码

1. **Fork 项目**
   ```bash
   # 在 GitHub 上点击 Fork 按钮
   git clone https://github.com/your-username/couples-app.git
   ```

2. **创建分支**
   ```bash
   git checkout -b feature/your-feature-name
   # 或
   git checkout -b fix/your-bug-fix
   ```

3. **编写代码**
   - 遵循项目现有的代码风格
   - 添加必要的注释
   - 编写单元测试（如果适用）

4. **提交更改**
   ```bash
   git add .
   git commit -m "feat: 添加某某功能"
   # 或
   git commit -m "fix: 修复某某问题"
   ```

   提交信息格式：
   - `feat:` 新功能
   - `fix:` Bug 修复
   - `docs:` 文档更新
   - `style:` 代码格式调整
   - `refactor:` 重构
   - `test:` 测试相关
   - `chore:` 构建或辅助工具的变动

5. **推送分支**
   ```bash
   git push origin feature/your-feature-name
   ```

6. **创建 Pull Request**
   - 在 GitHub 上创建 PR
   - 填写 PR 模板
   - 关联相关的 Issue

## 代码规范

### Java/Kotlin 代码

- 使用 4 个空格缩进
- 类名使用 `PascalCase`
- 方法名和变量名使用 `camelCase`
- 常量使用 `UPPER_SNAKE_CASE`
- 适当添加注释，特别是复杂逻辑

### 数据库

- 表名使用复数形式（`users`, `photos`）
- 字段名使用 `snake_case`
- 外键命名：`table_name_id`

### API 设计

- RESTful 风格
- 使用合适的 HTTP 方法（GET, POST, PUT, DELETE）
- 返回统一的 JSON 格式

## 测试

在提交 PR 之前，请确保：

- [ ] 代码能够编译通过
- [ ] 现有测试全部通过
- [ ] 为新功能添加了测试
- [ ] 手动测试了相关功能

运行测试：

```bash
# 后端测试
cd myphd/sever
./mvnw test

# Android 测试
cd myp
./gradlew test
```

## 文档

如果你的贡献涉及：

- 新功能：更新 README.md
- API 变更：更新 API 文档
- 配置变更：更新部署文档

## 审查流程

1. 提交 PR 后，维护者会进行代码审查
2. 根据反馈修改代码
3. 审查通过后，PR 会被合并到主分支

## 行为准则

- 尊重所有贡献者
- 接受建设性批评
- 专注于对项目最有利的事情
- 对社区成员保持同理心

## 需要帮助？

如果你有任何问题，可以：

- 查看现有的 Issues 和 Discussions
- 在 Issue 中提问
- 发送邮件给维护者

再次感谢你的贡献！🎉
