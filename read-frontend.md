# 前端 read.md

## 概览
- 技术栈：静态 HTML、Bootstrap 5、Bootstrap Icons、jQuery
- 样式库：`css/ui-tokens.css`（设计令牌）、`css/ui-components.css`（统一组件样式）、`css/vant-lite.css`（Vant3 风格轻量实现）
- 模板策略：
  - 管理端页面采用专属布局（顶部应用栏+分组侧边导航），由 `js/layout.js` 在 `/admin/` 路径注入
  - 读者端页面独立布局，不使用管理端模板
- 访问入口：`http://localhost:8080/`

## 页面结构
- 登录页 `static/index.html`
  - 双栏布局：左侧品牌与说明，右侧登录卡片
  - 记住我（7 天）、错误/成功提示
  - 登录接口：`POST /api/loginCheck`
- 管理端
  - 主页 `admin/main.html`：快捷入口卡片
  - 图书列表 `admin/books.html`：搜索、分页、编辑、删除
  - 新增图书 `admin/add-book.html`：Vant 风格表单（书名、作者、出版社、ISBN、价格、库存）
  - 读者列表 `admin/readers.html`：列表与编辑/删除
  - 新增读者 `admin/add-reader.html`：Vant 风格表单（用户名、姓名、性别、生日、电话、地址、初始密码）
  - 设置 `admin/settings.html`：管理员改密
- 读者端
  - 主页 `reader/main.html`：概览（已借/已还/逾期/收藏）、搜索、卡片列表、借阅/归还
  - 详情 `reader/book-detail.html`：Vant 风格卡片详情，借阅/归还与返回
  - 改密 `reader/change-password.html`

## 设计与交互规范
- 布局：顶部应用栏（logo/通知/头像）+ 分组折叠侧边导航（仅管理员）+ 面板化内容区
- 颜色：主要色蓝系；状态提示—成功绿、警告橙、错误红（柔和底色）
- 组件：按钮强调与中性色分级；输入聚焦环；表格斑马与悬停；提示框柔和底色
- 响应式：断点栅格，双列表单在窄屏降为单列

## 运行与预览
- 启动后端后访问：
  - 登录页 `http://localhost:8080/index.html`
  - 管理端 `http://localhost:8080/admin/main.html`
  - 读者端 `http://localhost:8080/reader/main.html`
- 示例账号（见 `library.sql`）：
  - 管理员：`admin` / `123456`
  - 读者：如 `张华` / `123456`（reader_card 表中的用户名，口令为 `123456`）

## 与后端的接口对接
- 图书
  - `GET /api/books?page&size&q` 列表分页
  - `GET /api/books/{id}` 获取详情
  - `POST /api/books` 新增
  - `PUT /api/books/{id}` 编辑
  - `DELETE /api/books/{id}` 删除
- 读者
  - `GET /api/readers` 列表
  - `GET /api/readers/{id}` 详情
  - `POST /api/readers` 新增（同时创建读者卡）
  - `PUT /api/readers/{id}` 编辑
  - `DELETE /api/readers/{id}` 删除
- 借阅
  - `GET /api/my-lends` 当前用户借阅列表（基于会话）
  - `POST /api/lend?bookId` 借阅
  - `POST /api/return?bookId` 归还
- 登录
  - `POST /api/loginCheck` 登录，返回管理员/读者状态并建立会话

## 关键实现说明
- 管理端模板注入 `js/layout.js`
  - 仅作用于 `/admin/` 路径，登录页与读者端不注入
  - 顶部头像点击弹出退出菜单，确认后清理本地存储并回到登录页
- 新增图书前端已去除“分类ID”，后端使用默认分类
- 读者端采用 Vant 风格的卡片与 Cell 布局，简洁、易读、可触控

## 常见问题
- 新增图书 500：后端已为缺失字段填充默认值；确保至少输入书名/作者
- 借阅/归还 401：需先登录建立会话；读者端按钮将根据借阅状态切换

