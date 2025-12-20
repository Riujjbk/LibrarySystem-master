# 图书馆管理系统 - 软件工程分析报告

## 1. 系统概述
本报告基于图书馆管理系统（LibrarySystem）的源代码，从软件工程的角度对其核心模块进行分析。主要涵盖模块间的耦合度、模块内的内聚度以及针对关键功能的黑盒测试设计。

---

## 2. 模块耦合与内聚分析

### 2.1 用户认证模块 (User Authentication)
**涉及组件**: `LoginController`, `LoginService`, `login.js`

*   **耦合性 (Coupling): 控制耦合 (Control Coupling)**
    *   **分析**: 后端 `LoginController` 的 `loginCheck` 方法返回 `stateCode` ("0", "1", "2")。前端 `login.js` 接收到这个状态码后，使用 `if/else` 逻辑来决定跳转到管理员主页还是读者主页。
    *   **评价**: 后端数据直接控制了前端的执行流程，属于控制耦合。虽然实现了功能，但前后端依赖较强。
    *   **代码证据**:
        ```java
        // LoginController.java
        if (isAdmin) { res.put("stateCode", "1"); }
        else if (isReader) { res.put("stateCode", "2"); }
        else { res.put("stateCode", "0"); }
        ```

*   **内聚性 (Cohesion): 逻辑内聚 (Logical Cohesion)**
    *   **分析**: `loginCheck` 方法同时处理了管理员登录和读者登录两种逻辑。虽然都是"登录"，但处理的对象和后续建立的 Session 类型（`admin` vs `readercard`）不同。
    *   **评价**: 将相似功能的逻辑放在一个模块中，属于逻辑内聚。

### 2.2 图书管理模块 (Book Management)
**涉及组件**: `BookController`, `BookService`, `BookDao`

*   **耦合性 (Coupling): 数据耦合 (Data Coupling)**
    *   **分析**: `BookController` 调用 `BookService` 时，仅传递必要的简单数据（如 `searchWord` 字符串）或单一的数据对象（如 `Book` 实体）。
    *   **评价**: 这是最理想的耦合形式，模块间通过参数传递数据，接口清晰，依赖性低。

*   **内聚性 (Cohesion): 功能内聚 (Functional Cohesion)**
    *   **分析**: `BookService` 中的每个方法都只完成一个单一的、定义明确的任务。例如 `addBook` 只负责添加，`deleteBook` 只负责删除。
    *   **评价**: 这是最高级别的内聚，模块功能单一且纯粹，易于维护和复用。
    *   **代码证据**:
        ```java
        // BookService.java
        public boolean addBook(Book book) { return bookDao.insert(book) > 0; }
        public boolean deleteBook(Long bookId) { return bookDao.deleteById(bookId) > 0; }
        ```

### 2.3 借阅管理模块 (Lend/Return Management)
**涉及组件**: `LendController`, `LendService`, `BookService`

*   **耦合性 (Coupling): 公共耦合 (Common Coupling) / 外部耦合**
    *   **分析**: 借阅操作 (`lendBook`) 不仅操作借阅记录表，还隐式依赖于图书表的库存状态。`LendService` 和 `BookService` 实际上共享了数据库中的图书库存数据（`book_info` 表）。
    *   **评价**: 多个模块操作同一份全局数据（数据库记录），当库存逻辑变更时，可能影响多个服务。

*   **内聚性 (Cohesion): 通信内聚 (Communication Cohesion)**
    *   **分析**: 在 `LendService` 的 `lendBook` 方法中，使用了同一个输入数据（`bookId`）来执行两个动作：1. 扣减图书库存；2. 插入借阅记录。
    *   **评价**: 这些动作因为操作相同的数据而组合在一起，属于通信内聚。
    *   **代码证据**:
        ```java
        // LendDao.java SQL操作
        @Update("update library.book_info set number = number - 1 ...")
        @Insert("insert into library.lend_list ...")
        ```

---

## 3. 黑盒测试设计 (Black Box Testing)

黑盒测试关注软件的功能需求，不考虑内部逻辑结构。以下针对核心功能设计测试用例。

### 3.1 登录功能测试
**测试目标**: 验证系统能否正确区分管理员、读者及非法用户。

| 用例ID | 输入数据 (用户名/密码) | 预期结果 | 等价类划分 |
| :--- | :--- | :--- | :--- |
| TC_LOG_01 | `admin` / `123456` | 登录成功，跳转至 `/admin/main.html` | 有效等价类 (管理员) |
| TC_LOG_02 | `10000` / `123456` | 登录成功，跳转至 `/reader/main.html` | 有效等价类 (读者) |
| TC_LOG_03 | `admin` / `wrong` | 登录失败，提示"账号或密码错误" | 无效等价类 |
| TC_LOG_04 | (空) / (空) | 提示"请输入用户名/密码" | 边界值 (空输入) |

### 3.2 图书借阅功能测试
**测试目标**: 验证借阅逻辑及库存控制。

| 用例ID | 前置条件 | 输入数据 | 预期结果 | 测试类型 |
| :--- | :--- | :--- | :--- | :--- |
| TC_LEND_01 | 库存 > 0，用户未借阅 | `bookId=1` | 借阅成功，库存-1，新增借阅记录 | 正常流程 |
| TC_LEND_02 | 库存 = 0 | `bookId=2` | 借阅失败，提示"库存不足" | 边界值分析 |
| TC_LEND_03 | 用户未登录 | `bookId=1` | 请求被拒绝 (401 Unauthorized) | 错误猜测 |
| TC_LEND_04 | 书籍ID不存在 | `bookId=9999` | 借阅失败，提示"图书不存在" | 异常流程 |

### 3.3 图书查询功能测试
**测试目标**: 验证模糊搜索功能的正确性。

| 用例ID | 输入数据 (SearchWord) | 预期结果 | 覆盖场景 |
| :--- | :--- | :--- | :--- |
| TC_SRCH_01 | `三体` | 返回书名为《三体》的书籍 | 精确匹配 |
| TC_SRCH_02 | `刘` | 返回作者含"刘"的所有书籍 | 模糊匹配 (作者) |
| TC_SRCH_03 | `体` | 返回书名含"体"的所有书籍 | 模糊匹配 (书名) |
| TC_SRCH_04 | `XYZ` (不存在) | 返回空列表 | 无结果场景 |

---

## 4. 总结
通过分析可见，系统整体采用了经典的分层架构（Controller-Service-Dao），大部分模块（如图书管理）保持了良好的**功能内聚**和**数据耦合**，结构清晰。但在登录模块存在一定的**控制耦合**，且借阅模块通过数据库存在**公共耦合**，这在单体应用中是常见的权衡结果。黑盒测试覆盖了核心业务流程和边界条件，确保了系统的基本可用性。
