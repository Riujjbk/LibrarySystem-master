# 图书管理系统 - 前端说明

## 🎨 现代化前端重写

项目已完全重写前端，使用现代HTML5 + CSS3 + JavaScript + Bootstrap 5技术栈，告别老旧的JSP页面。

## ✨ 新特性

### 🎯 现代化设计
- **Bootstrap 5**: 最新UI框架，响应式设计
- **现代化UI**: 渐变背景、卡片布局、动画效果
- **移动端友好**: 完全响应式，支持各种设备

### 🚀 性能优化
- **CDN加速**: 使用CDN加载Bootstrap和jQuery
- **纯静态资源**: 无需JSP编译，提升加载速度
- **现代JavaScript**: ES6+语法，模块化开发

### 📱 用户体验
- **直观的界面**: 清晰的导航和布局
- **实时反馈**: 加载状态、错误提示
- **交互友好**: 悬停效果、动画过渡

## 📁 文件结构

```
src/main/resources/static/
├── index.html              # 登录页面
├── admin/
│   └── main.html          # 管理员主页
└── reader/
    └── main.html          # 读者主页
```

## 🌐 页面说明

### 登录页面 (`/index.html`)
- 现代化登录界面
- 账号密码验证
- 记住登录状态
- 错误提示和加载状态

### 管理员主页 (`/admin/main.html`)
- 控制台统计数据
- 图书管理入口
- 读者管理入口
- 借还管理入口
- 系统设置

### 读者主页 (`/reader/main.html`)
- 图书搜索和浏览
- 个人借阅记录
- 图书借还操作
- 个人资料管理

## 🔧 技术栈

- **前端框架**: Bootstrap 5.3.2
- **图标库**: Bootstrap Icons 1.11.1
- **JavaScript**: jQuery 3.7.1 + 原生JS
- **样式**: CSS3 + Flexbox/Grid
- **响应式**: 移动端优先设计

## 🚀 访问方式

1. 启动应用：
   ```bash
   mvn spring-boot:run
   ```

2. 访问地址：
   - 主页：http://localhost:8080/
   - 登录：http://localhost:8080/index.html
   - 管理员：http://localhost:8080/admin/main.html
   - 读者：http://localhost:8080/reader/main.html

## 🎨 自定义主题

可以通过修改CSS变量来自定义颜色主题：

```css
:root {
    --primary-color: #667eea;
    --secondary-color: #764ba2;
    --success-color: #ff9a9e;
    --warning-color: #fecfef;
}
```

## 📱 浏览器支持

- Chrome 90+
- Firefox 88+
- Safari 14+
- Edge 90+

## 🔄 API接口

前端通过以下API与后端交互：

- `POST /api/loginCheck` - 用户登录
- `GET /admin/books` - 获取图书列表
- `POST /admin/books` - 添加图书
- `PUT /admin/books/{id}` - 更新图书
- `DELETE /admin/books/{id}` - 删除图书

## 🛠️ 开发说明

如需修改前端页面：

1. 编辑对应的HTML文件
2. 修改CSS样式（内联或外联）
3. 更新JavaScript逻辑
4. 测试响应式布局

## 📝 注意事项

- 所有静态资源路径使用绝对路径（以`/`开头）
- 图片资源存储在`/img/`目录下
- CSS文件存储在`/css/`目录下
- JavaScript文件存储在`/js/`目录下

---

**享受现代化的图书管理系统前端体验！** 🎉

