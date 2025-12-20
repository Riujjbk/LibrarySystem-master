# 🔧 故障排除指南

## 前端页面空白或报错问题

### 🚨 常见错误

#### 1. "Unchecked runtime.lastError: Could not establish connection. Receiving end does not exist."

**原因**: 浏览器扩展或CDN资源加载失败

**解决方案**:
- ✅ 已修复: 所有CDN链接替换为本地资源
- ✅ 已修复: 移除外部依赖，改用本地文件

#### 2. 页面完全空白

**原因**: 应用未启动或端口问题

**解决方案**:
```bash
# 1. 检查应用状态
netstat -ano | findstr :8080

# 2. 如果没有应用在运行，启动应用
mvn spring-boot:run

# 3. 访问测试页面
# http://localhost:8080/basic.html (最简单)
# http://localhost:8080/compat.html (兼容性测试)
```

#### 3. Maven启动失败

**错误信息**: "No plugin found for prefix 'spring-boot'"

**解决方案**:
```bash
# 方法1: 使用完整插件名称
mvn org.springframework.boot:spring-boot-maven-plugin:run

# 方法2: 先打包再运行
mvn clean package -DskipTests
java -jar target/library-1.1.jar

# 方法3: 使用启动脚本
start.bat (Windows)
```

### 🧪 测试步骤

按以下顺序测试，确保每步都正常：

#### 步骤1: 基础连接测试
```
访问: http://localhost:8080/basic.html
期望: 看到绿色成功提示和当前时间
```

#### 步骤2: 浏览器兼容性测试
```
访问: http://localhost:8080/compat.html
期望: 所有测试项显示绿色
```

#### 步骤3: 静态资源测试
```
访问: http://localhost:8080/test.html
期望: CSS正常，图片加载，JavaScript工作
```

#### 步骤4: 主页面测试
```
访问: http://localhost:8080/
期望: 登录页面正常显示
```

### 🔍 诊断工具

#### 检查端口占用
```bash
# Windows
netstat -ano | findstr :8080

# 如果有占用，杀掉进程
taskkill /PID <PID> /F
```

#### 检查Java和Maven
```bash
java -version
mvn -version
```

#### 检查项目结构
```
LibrarySystem-master/
├── src/main/resources/static/
│   ├── index.html          ✅ 登录页面
│   ├── basic.html          ✅ 基础测试
│   ├── compat.html         ✅ 兼容性测试
│   ├── css/
│   │   └── bootstrap.min.css ✅ Bootstrap CSS
│   ├── js/
│   │   ├── jquery-3.2.1.js ✅ jQuery
│   │   └── bootstrap.min.js ✅ Bootstrap JS
│   └── img/                ✅ 图片资源
└── pom.xml                 ✅ Maven配置
```

### 🌐 正确的访问方式

| 错误方式 | ❌ |
|---------|-----|
| IDE直接打开HTML | `http://localhost:63342/...` |
| 文件系统路径 | `file:///C:/path/to/file.html` |

| 正确方式 | ✅ |
|---------|-----|
| Spring Boot应用 | `http://localhost:8080/` |
| 登录页面 | `http://localhost:8080/index.html` |
| 管理员页面 | `http://localhost:8080/admin/main.html` |
| 读者页面 | `http://localhost:8080/reader/main.html` |

### 🛠️ 手动修复

如果自动修复无效，可以手动检查：

1. **确认应用运行**:
   ```bash
   mvn spring-boot:run
   ```

2. **检查浏览器缓存**:
   - Ctrl+F5 强制刷新
   - 清除浏览器缓存

3. **检查控制台错误**:
   - F12 打开开发者工具
   - 查看Console标签页的错误信息

4. **尝试不同浏览器**:
   - Chrome, Firefox, Edge等

### 📞 获取帮助

如果问题仍然存在：

1. 运行 `http://localhost:8080/compat.html` 获取浏览器信息
2. 检查控制台的完整错误信息
3. 确认网络连接正常
4. 尝试重启计算机

### 🎯 最终验证

成功标志：
- ✅ 页面正常显示（不空白）
- ✅ 没有控制台错误
- ✅ 静态资源正常加载
- ✅ JavaScript功能正常
- ✅ 可以正常导航

---

**记住：永远通过 `http://localhost:8080` 访问Spring Boot应用！** 🎉
