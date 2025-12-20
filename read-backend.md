# 后端 read.md

## 概览
- 技术栈：Spring Boot 2.7.x、MyBatis-Plus、MySQL、Maven
- 端口：`8080`
- 会话：登录成功后在 Session 中存储用户信息（管理员或读者），部分接口依赖会话（如 `/api/my-lends`）

## 环境准备
- 数据库：导入 `library.sql`
  - 库名：`library`
  - 表：`admin`、`book_info`、`reader_info`、`reader_card`、`lend_list`
- 配置数据源（示例）
  - 文件：`src/main/resources/application.properties`
  - 关键项：
    - `spring.datasource.url=jdbc:mysql://localhost:3306/library?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai`
    - `spring.datasource.username=your_user`
    - `spring.datasource.password=your_password`
    - `spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver`

## 启动项目
- 命令：`mvn -DskipTests spring-boot:run`
- 欢迎页映射到 `static/index.html`，访问 `http://localhost:8080/`

## 数据模型
- `com.library.bean.Book` 映射表 `book_info`
  - 主键：`book_id`（`Long`，自增）
  - 约束：`name`、`author`、`publish`、`ISBN`、`language`、`price`、`pub_date` 均为 NOT NULL
  - 默认策略（新增时）：
    - `publish` 缺失 → `未知出版社`
    - `isbn` 缺失 → `0000000000000`
    - `language` 缺失 → `zh`
    - `price` 缺失 → `0`
    - `pub_date` 缺失 → 当前日期
    - `class_id` ≤ 0 → `1`
    - `number` < 0 → `0`

## 主要模块与接口
- LoginController
  - `POST /api/loginCheck`：登录（支持管理员与读者用户名），成功后建立会话并返回状态码
  - `GET /logout.html`：退出登录，清理会话并跳转登录页
- BookController
  - `GET /api/books?page&size&q`：分页列表（模糊匹配书名/作者）
  - `GET /api/books/{id}`：获取详情
  - `POST /api/books`：新增图书（应用默认值策略与基本校验）
  - `PUT /api/books/{id}`：编辑图书
  - `DELETE /api/books/{id}`：删除图书
- ReaderController
  - `GET /api/readers`、`GET /api/readers/{id}`
  - `POST /api/readers`：新增读者信息，并创建读者卡（用户名与初始密码）
  - `PUT /api/readers/{id}`、`DELETE /api/readers/{id}`
- LendController
  - `GET /api/lends`：所有借阅记录
  - `GET /api/my-lends`：当前读者的借阅记录（需会话）
  - `POST /api/lend?bookId`：借阅（更新库存，写借阅表）
  - `POST /api/return?bookId`：归还（标注归还时间，库存+1）

## DAO 与 MyBatis-Plus
- `BookDao extends BaseMapper<Book>`：分页与 CRUD 由 MyBatis-Plus 提供
- 自定义查询示例：
  - `queryBook`/`matchBook`（`like` name/author）
  - `LendDao`：借阅/归还更新库存与记录

## 交互流程说明
- 登录
  - 管理员：`admin` / `123456`（见 `library.sql`）
  - 读者：如 `张华` / `123456`（reader_card 表中用户名）
- 管理端
  - 新增图书不再要求前端传 `classId`，后端采用默认分类并填充缺失字段
  - 列表支持搜索与编辑删除
- 读者端
  - 主页展示卡片列表，可借阅/归还
  - 详情页简约布局，展示关键字段与简介

## 常见问题与排查
- 新增图书 500
  - 检查至少传入 `name`、`author`
  - 如仍失败，查看响应 JSON 的 `error` 信息
- 借阅/归还 401
  - 确认已登录且会话有效；`/api/my-lends` 未授权会返回 401

## 目录结构（关键部分）
- 后端 Java：`src/main/java/com/library/...`
  - `controller/` 控制器
  - `service/` 业务逻辑
  - `dao/` 数据访问
  - `bean/` 实体映射
- 前端静态资源：`src/main/resources/static/...`
  - `admin/` 管理端页面
  - `reader/` 读者端页面
  - `css/` 样式、`js/` 脚本、`img/` 图片

