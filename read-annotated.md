# 项目注释指南（代码说明与调用关系）

本指南按模块解释每个关键文件“是什么、怎么调用、作用”，并给出源码定位，便于汇报展示与快速追踪。

## 登录与会话
- 控制器：`src/main/java/com/library/controller/LoginController.java`
  - `loginCheck` 登录接口（接收 JSON 或表单）  
    源码：`src/main/java/com/library/controller/LoginController.java:43`  
    作用：校验管理员/读者账号，成功后把对象放入 Session（键：`admin` 或 `readercard`），返回 `stateCode`（1=管理员，2=读者，0=失败）。
  - `logout.html` 退出登录  
    源码：`src/main/java/com/library/controller/LoginController.java:35`  
    作用：清理 Session 并重定向登录页。
- 前端脚本：`static/js/login.js`  
  - `performLogin` 发起 `POST /api/loginCheck`，根据 `stateCode` 跳转  
    源码：`src/main/resources/static/js/login.js:9`  
    作用：处理表单校验、加载态、记住我、本地错误提示。

## 图书模块
- 控制器：`src/main/java/com/library/controller/BookController.java`
  - 列表分页 `GET /api/books?page&size&q`  
    源码：`src/main/java/com/library/controller/BookController.java:54`  
    作用：按书名/作者模糊查询，返回分页数据。
  - 获取详情 `GET /api/books/{id}`  
    源码：`src/main/java/com/library/controller/BookController.java:69`  
    作用：按主键获取一本书。
  - 新增 `POST /api/books`  
    源码：`src/main/java/com/library/controller/BookController.java:74`  
    作用：新增图书；后端为缺失字段填充默认值（publish、ISBN、language、price、pub_date、class_id、number），并校验书名与作者。
  - 编辑 `PUT /api/books/{id}`  
    源码：`src/main/java/com/library/controller/BookController.java:118`  
    作用：按主键更新一本书。
  - 删除 `DELETE /api/books/{id}`  
    源码：`src/main/java/com/library/controller/BookController.java:128`  
    作用：按主键删除一本书。
- 服务层：`src/main/java/com/library/service/BookService.java`  
  - 使用 MyBatis-Plus 的 `BaseMapper` 完成分页与 CRUD。
- DAO：`src/main/java/com/library/dao/BookDao.java`  
  - 扩展查询（`like` 名称/作者）、库存查询。
- 实体：`src/main/java/com/library/bean/Book.java`  
  - 主键 `book_id`（`Long`，自增）；字段与表 `book_info` 一一映射，满足 NOT NULL 约束。

## 借阅模块
- 控制器：`src/main/java/com/library/controller/LendController.java`
  - 我的借阅 `GET /api/my-lends`（基于 Session）  
    源码：`src/main/java/com/library/controller/LendController.java:64`  
    作用：读取 `readercard`，返回当前读者的借阅记录。
  - 借阅 `POST /api/lend?bookId`  
    源码：`src/main/java/com/library/controller/LendController.java:72`  
    作用：校验会话、图书存在与库存，写借阅记录并库存-1。
  - 归还 `POST /api/return?bookId`  
    源码：`src/main/java/com/library/controller/LendController.java:106`  
    作用：校验会话，标注归还时间并库存+1。
- DAO：`src/main/java/com/library/dao/LendDao.java`  
  - 使用 SQL 更新借阅与库存（`update book_info set number...`）。

## 读者模块
- 控制器：`src/main/java/com/library/controller/ReaderController.java`  
  - 列表/详情：`GET /api/readers`、`GET /api/readers/{id}`  
  - 新增：`POST /api/readers`（同时创建读者卡）  
  - 编辑/删除：`PUT /api/readers/{id}`、`DELETE /api/readers/{id}`
  - 作用：维护读者基础信息与登录卡。

## 前端页面与脚本
- 登录页：`static/index.html`  
  - 提交到 `POST /api/loginCheck`，管理员跳 `/admin/main.html`，读者跳 `/reader/main.html`。
- 管理端
  - 列表页：`admin/books.html`  
    - 搜索框调用 `GET /api/books?q=...`；分页与刷新。
  - 新增图书：`admin/add-book.html`  
    - 提交到 `POST /api/books`，仅发送必要字段；分类使用后端默认值。
  - 读者列表：`admin/readers.html`  
    - 按按钮调用读者接口进行编辑/删除。
  - 设置：`admin/settings.html`  
    - 提交改密到 `/admin_repasswd_do`。
- 读者端
  - 主页：`reader/main.html`  
    - 概览通过 `GET /api/my-lends`；列表从 `GET /api/books`；借阅/归还按钮调用相应接口。
  - 详情页：`reader/book-detail.html`  
    - 加载 `GET /api/books/{id}`；借阅/归还同主页；返回按钮回主页。
- 管理端模板注入：`static/js/layout.js:1`  
  - 仅对 `/admin/` 路径执行：注入顶部栏与侧边导航（`groups`），头像点击弹出退出菜单。  
  - 退出调用：本地清理并跳转登录页 `static/js/layout.js:83`。

## 样式与设计
- `static/css/ui-tokens.css`：颜色、圆角、阴影、间距、变量定义。
- `static/css/ui-components.css`：统一组件样式（按钮、输入、表格、提示、应用栏、侧边导航）。
- `static/css/vant-lite.css`：Vant3 风格轻量实现（卡片、CellGroup、Toolbar 等）。

## 调用关系（典型场景）
- 新增图书（管理员）
  1. 页面 `admin/add-book.html` 组装 JSON（书名/作者/出版社/ISBN/价格/库存），调用 `POST /api/books`。
  2. `BookController.apiAddBook` 接受请求（`BookController.java:74`），填充默认值与校验后调用 `BookService.addBook`。
  3. `BookService.addBook` 通过 `BookDao.insert` 写入 `book_info`。
  4. 返回 `{"success":true}`，前端跳转列表页。
- 借阅（读者）
  1. 主页或详情页点击“借阅”→ `POST /api/lend?bookId=...`。
  2. `LendController.apiLend`（`LendController.java:72`）读取会话读者，校验库存→ 记录借阅、库存-1。
  3. 返回成功后，按钮切换为“归还”并刷新统计。
- 登录
  1. 登录页 `login.js:19` 发起 `POST /api/loginCheck`。
  2. `LoginController.loginCheck`（`LoginController.java:43`）判定管理员/读者，设置 Session 并返回 `stateCode`。
  3. 前端按角色跳转对应主页。

## 数据库约束与默认值
- 表 `book_info`（见 `library.sql:10`）要求多字段 NOT NULL：`publish`、`ISBN`、`language`、`price`、`pub_date`。  
  为避免 500，`BookController.apiAddBook` 在缺失时自动填充默认值并使用 `Long` 自增主键。

## 快速排错指引
- 新增图书 500：确认至少传 `name` 与 `author`；查看返回 JSON 的 `error` 字段。
- 借阅/归还 401：确认已登录且 Session 中存在 `readercard`。
- 模板错乱：确保 `layout.js` 只作用于 `/admin/`（`layout.js:3`）。

