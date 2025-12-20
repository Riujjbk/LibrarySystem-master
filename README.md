# 图书馆管理系统

### 概述
基于Spring Boot + MyBatis Plus的现代化图书馆管理系统，完全重写前端使用HTML5 + CSS3 + Bootstrap 5。主要功能包括：图书查询、图书管理、图书编辑、读者管理、图书的借阅与归还以及借还日志记录等。

### ✨ 新特性
- 🚀 **Spring Boot 2.7**: 现代化框架，自动配置
- 🎨 **Bootstrap 5**: 最新UI框架，响应式设计
- 📱 **移动端友好**: 支持各种设备访问
- ⚡ **高性能**: CDN加速，纯静态资源
- 🔄 **MyBatis Plus**: 增强ORM，简化开发

### 环境配置
#### 开发环境
- **操作系统**: Windows 10+
- **Java**: JDK 1.8+
- **数据库**: MySQL 5.7+
- **构建工具**: Maven 3.6+

#### 运行配置
1. **数据库准备**
   ```sql
   -- 创建数据库
   CREATE DATABASE library CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

   -- 导入数据
   -- 执行library.sql文件
   ```

2. **配置数据库连接**
   编辑 `src/main/resources/application.yml`:
   ```yaml
   spring:
     datasource:
       username: root  # 修改为你的数据库用户名
       password: 123456  # 修改为你的数据库密码
   ```

3. **启动应用**
   ```bash
   mvn spring-boot:run
   ```

4. **访问系统**
   - 主页：http://localhost:8080/
   - 测试页面：http://localhost:8080/test.html
   - 基础测试：http://localhost:8080/basic.html
   - 兼容性测试：http://localhost:8080/compat.html

### 🚨 常见问题

#### 页面空白或报错？
1. 确认应用正在运行：`mvn spring-boot:run`
2. 访问测试页面：`http://localhost:8080/basic.html`
3. 查看详细诊断：`TROUBLESHOOTING.md`

#### Maven启动失败？
```bash
# 使用启动脚本
start.bat

# 或手动启动
mvn clean compile
mvn spring-boot:run
```

#### 数据库连接问题？
检查 `application.yml` 中的数据库配置是否正确。

### 🎨 前端技术栈
- **UI框架**: Bootstrap 5.3.2
- **图标库**: Bootstrap Icons 1.11.1
- **JavaScript**: jQuery 3.7.1 + 原生ES6+
- **样式**: CSS3 + Flexbox/Grid
- **响应式**: 移动端优先

### 概念设计
用户分为两类：读者、图书馆管理员。图书馆管理员可以修改读者信息，修改书目信息，查看所有借还日志等；读者仅可以修改个人信息、借阅或归还书籍和查看自己的借还日志。
<img src="./preview/1.png" style="width: 50%"><img src="./preview/2.png" style="width: 50%;float: right">

#### 数据库E-R图
<img src="./preview/3.png">

### 👥 开发团队
项目团队分工与职责详情请见 [TEAM.md](./TEAM.md)。

### 逻辑设计
共有6个表：

#### 1. 图书书目表book_info
| 名           | 类型    | 长度 | 小数点 | NULL | 用途     | 键   |
| :----------- | :------ | ---- | ------ | ---- | -------- | ---- |
| book_id      | bigint  | 20   | 0      | 否   | 图书号   | ✔    |
| name         | varchar | 20   | 0      | 否   | 书名     |      |
| author       | varchar | 15   | 0      | 否   | 作者     |      |
| publish      | varchar | 20   | 0      | 否   | 出版社   |      |
| ISBN         | varchar | 15   | 0      | 否   | 标准书号 |      |
| introduction | text    | 0    | 0      | 是   | 简介     |      |
| language     | varchar | 4    | 0      | 否   | 语言     |      |
| price        | decimal | 10   | 2      | 否   | 价格     |      |
| pub_date     | date    | 0    | 0      | 否   | 出版时间 |      |
| class_id     | int     | 11   | 0      | 是   | 分类号   |      |
| number       | int     | 11   | 0      | 是   | 剩余数量 |      |

#### 2. 数据库管理员表admin
| 名       | 类型    | 长度 | 小数点 | NULL | 用途   | 键   |
| :------- | :------ | ---- | ------ | ---- | ------ | ---- |
| admin_id | bigint  | 20   | 0      | 否   | 账号   | ✔    |
| password | varchar | 15   | 0      | 否   | 密码   |      |
| username | varchar | 15   | 0      | 是   | 用户名 |      |

#### 3. 图书分类表class_info
| 名         | 类型    | 长度 | 小数点 | NULL | 用途   | 键   |
| :--------- | :------ | ---- | ------ | ---- | ------ | ---- |
| class_id   | int     | 11   | 0      | 否   | 类别号 | ✔    |
| class_name | varchar | 15   | 0      | 否   | 类别名 |      |

#### 4. 借阅信息表lend_list
| 名        | 类型   | 长度 | 小数点 | NULL | 用途     | 键   |
| :-------- | :----- | ---- | ------ | ---- | -------- | ---- |
| ser_num   | bigint | 20   | 0      | 否   | 流水号   | ✔    |
| book_id   | bigint | 20   | 0      | 否   | 图书号   |      |
| reader_id | bigint | 20   | 0      | 否   | 读者证号 |      |
| lend_date | date   | 0    | 0      | 是   | 借出日期 |      |
| back_date | date   | 0    | 0      | 是   | 归还日期 |      |

#### 5. 借阅卡信息表reader_card
| 名        | 类型    | 长度 | 小数点 | NULL | 用途     | 键   |
| :-------- | :------ | ---- | ------ | ---- | -------- | ---- |
| reader_id | bigint  | 20   | 0      | 否   | 读者证号 | ✔    |
| password  | varchar | 15   | 0      | 否   | 密码     |      |
| username  | varchar | 15   | 0      | 是   | 用户名   |      |

#### 6. 读者信息表reader_info
| 名        | 类型    | 长度 | 小数点 | NULL | 用途     | 键   |
| :-------- | :------ | ---- | ------ | ---- | -------- | ---- |
| reader_id | bigint  | 20   | 0      | 否   | 读者证号 | ✔    |
| name      | varchar | 10   | 0      | 否   | 姓名     |      |
| sex       | varchar | 2    | 0      | 否   | 性别     |      |
| birth     | date    | 0    | 0      | 否   | 生日     |      |
| address   | varchar | 50   | 0      | 否   | 地址     |      |
| phone     | varchar | 15   | 0      | 否   | 电话     |      |


##### 3.2 个人信息查看，可以修个个人信息
<img src="./preview/11.png">

##### 3.3 个人借阅情况查看
<img src="./preview/12.png">


