# 📚 图书管理系统 - 访问指南

## 🚀 启动应用

确保Spring Boot应用正在运行：

```bash
cd LibrarySystem-master
mvn spring-boot:run
```

应用将在 `http://localhost:8080` 启动。

## 🌐 正确访问URL

### ❌ 错误访问方式（IDE直接打开）
```
http://localhost:63342/LibrarySystem-master/library/static/admin/main.html
```
这会通过IDE的内置服务器访问，无法加载后端数据。

### ✅ 正确访问方式（通过Spring Boot）

#### 1. 首页/登录页面
```
http://localhost:8080/
```
或
```
http://localhost:8080/index.html
```

#### 2. 管理员页面
```
http://localhost:8080/admin/main.html
```

#### 3. 读者页面
```
http://localhost:8080/reader/main.html
```

#### 4. 测试页面
```
http://localhost:8080/test.html
```

#### 5. 静态资源
```
http://localhost:8080/css/bootstrap.min.css
http://localhost:8080/js/jquery-3.7.1.min.js
http://localhost:8080/img/book1.jpg
```

## 🔧 Spring Boot静态资源映射

静态资源自动映射规则：

| 文件路径 | 访问URL |
|---------|---------|
| `src/main/resources/static/index.html` | `http://localhost:8080/index.html` |
| `src/main/resources/static/admin/main.html` | `http://localhost:8080/admin/main.html` |
| `src/main/resources/static/css/bootstrap.min.css` | `http://localhost:8080/css/bootstrap.min.css` |
| `src/main/resources/static/js/jquery-3.7.1.min.js` | `http://localhost:8080/js/jquery-3.7.1.min.js` |
| `src/main/resources/static/img/book1.jpg` | `http://localhost:8080/img/book1.jpg` |

## 📝 重要提醒

1. **必须通过Spring Boot应用访问**：不要直接在IDE中打开HTML文件
2. **端口必须是8080**：确保Spring Boot运行在正确端口
3. **应用必须正在运行**：如果停止了，需要重新启动
4. **静态资源路径**：所有资源路径都以 `/` 开头

## 🐛 常见问题

### Q: 访问页面显示404
A: 确保Spring Boot应用正在运行，访问URL格式正确

### Q: 静态资源加载失败
A: 检查 `src/main/resources/static/` 目录下的文件是否存在

### Q: 端口被占用
A: 停止其他占用8080端口的应用，或修改 `application.yml` 中的端口配置

## 🎯 快速测试

启动应用后，按以下顺序测试：

1. `http://localhost:8080/test.html` - 测试静态资源
2. `http://localhost:8080/` - 测试首页
3. `http://localhost:8080/admin/main.html` - 测试管理员页面
4. `http://localhost:8080/reader/main.html` - 测试读者页面


理解MPA（多页面应用）和SPA（单页面应用）的差异，对于设计现代Web应用的架构至关重要。为了让你能快速把握全貌，我们先通过一个表格来直观对比它们的核心特性，然后再深入探讨其技术实现和选型考量。

对比维度 单页面应用 (SPA) 多页面应用 (MPA)

核心架构 单个HTML页面，内容通过JavaScript动态替换 由多个独立的HTML页面组成

页面切换 无需重新加载整个页面，局部更新，体验流畅 整页刷新，浏览器会重新加载所有资源

前后端交互 前后端分离。前端负责展现和交互，通过API（如RESTful、GraphQL）与后端进行数据通信 前后端耦合。后端负责渲染页面，前端主要负责展示

开发复杂度 前端框架复杂，需处理路由、状态管理等，开发门槛较高 技术栈相对传统，更易于上手，页面间耦合度低

SEO（搜索引擎优化） 初始加载时内容为空，不利于搜索引擎抓取，需借助服务端渲染(SSR)等技术优化 服务器直接返回完整内容，便于搜索引擎索引，SEO友好

性能表现 首次加载较慢（需下载较大JS文件），但后续交互速度快，响应迅速 首次加载（针对每个页面）较快，但页面切换时有重复请求，可能感觉卡顿

适用场景 交互复杂的后台管理系统、在线办公软件、社交平台等 内容为导向的官网、博客、电商网站等

🔧 技术实现与演进

•   SPA的路由机制：由于所有内容都在一个页面内，SPA通过前端路由来模拟多页面的效果。主要有两种实现方式：

    ◦   Hash模式：利用URL中#号后的哈希片段作为路由路径。其优点是实现简单、兼容性好；缺点是URL中带有#，不够美观。

    ◦   History模式：利用HTML5的History API（pushState、replaceState）来操作浏览器的历史记录，可以生成更简洁的URL。缺点是需要服务器端配合，避免直接访问子路径时返回404错误。

•   架构的演进与融合：为了兼顾SPA的流畅体验和MPA的首屏加载与SEO优势，出现了一些混合架构：

    ◦   服务端渲染（SSR）：首次访问时，由服务器生成完整的HTML页面返回给浏览器，提升首屏速度和SEO。之后，页面接管交互逻辑，变为一个SPA。Next.js (React) 和 Nuxt.js (Vue) 是代表性框架。

    ◦   静态站点生成（SSG）：在构建时预生成静态HTML文件，适用于内容变化不频繁的网站，能获得极快的加载速度和顶级SEO。

    ◦   Islands架构：这是一种较新的架构模式，默认将页面大部分作为静态HTML，而将交互部分（如一个评论组件）定义为独立的“孤岛”（Island）。这些“孤岛”会被分别打包和激活（Hydrate），从而极大减少客户端需要执行的JavaScript代码量，实现极致的性能。

💡 如何选择适合的架构

选择SPA还是MPA，最终取决于你的项目核心目标。

•   优先选择 SPA 的情况：

    ◦   项目核心是丰富的用户交互，追求类似桌面应用的流畅体验。

    ◦   典型场景：后台管理系统、在线文档编辑器、即时通讯应用、大型社交平台。

    ◦   开发团队熟悉现代前端框架（如React, Vue, Angular），并能应对随之而来的状态管理、打包优化等复杂性。

•   优先选择 MPA 的情况：

    ◦   项目以内容展示为核心，搜索引擎优化（SEO） 是首要任务。

    ◦   典型场景：企业官网、博客、新闻站点、电商商品列表页。

    ◦   希望快速上线，开发流程简单，或页面之间功能相对独立。

•   考虑混合架构（SSR/SSG）：

    ◦   当你需要SPA的体验，但又无法牺牲首屏速度和SEO时（例如内容型博客但又有复杂交互的管理后台）。

    ◦   技术团队有能力和精力去应对更复杂的构建和部署流程。


理解Java Web中的重定向（Redirect）对构建流畅的Web应用很重要。下面这个表格能帮你快速把握重定向及其兄弟技术“转发”的核心区别。

特性维度 重定向 (Redirect) 请求转发 (Forward)

工作原理 服务器返回特定状态码和新URL，客户端（浏览器）发起新请求 服务器内部将请求转发给另一资源，对客户端透明

请求次数 至少两次 一次

浏览器地址栏 变化，显示新URL 不变，仍显示原始URL

数据共享 不能共享初始请求的request数据（因为是两次独立请求） 可以共享同一request对象的数据

跳转范围 可跳转到任意URL（应用内、外部站点均可） 仅限同一Web应用内部

典型应用场景 用户登录后跳转、防止表单重复提交、旧URL迁移 组件化开发、服务器内部流程交接

🔧 实现重定向的几种方式

在Java Web开发中，有多种方法可以实现重定向。

1.  使用 HttpServletResponse 的 sendRedirect 方法
    这是最基础、最常用的方式，通常在Servlet中使用。
    // 在Servlet的doGet或doPost方法中
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
    // 重定向到另一个URL
    response.sendRedirect("https://www.example.com/newpage");
    // 重定向到应用内的另一个资源，路径相对于应用根目录
    // response.sendRedirect("/yourapp/success.jsp");
    }

    调用sendRedirect方法后，服务器会返回一个302（临时重定向）状态码和Location响应头，浏览器据此发起新请求。

2.  在Spring MVC中实现重定向
    在Spring MVC框架中，实现重定向更加简洁。
    ◦   使用 redirect: 前缀：在控制器方法中，返回的视图名前加上redirect:即可。
    @Controller
    public class MyController {
    @RequestMapping("/login")
    public String handleLogin() {
    // 处理登录逻辑...
    return "redirect:/dashboard"; // 重定向到/dashboard路径
    }
    }

    ◦   使用 RedirectView：你也可以直接返回一个RedirectView对象。
    @GetMapping("/somepage")
    public RedirectView handleRequest() {
    return new RedirectView("https://www.example.com");
    }

        Spring MVC的重定向方式非常直观，是当前开发的主流。

3.  手动设置状态码和Header（了解即可）
    重定向的本质是设置HTTP状态码和Location头，你也可以手动完成。
    // 设置状态码为302（临时移动）
    response.setStatus(HttpServletResponse.SC_FOUND);
    // 设置重定向的目标URL
    response.setHeader("Location", "https://www.example.com");

    这种方式更底层，通常不需要直接使用，sendRedirect已经帮我们封装好了。

⚠️ 核心技巧与常见陷阱

了解基础用法后，一些技巧和陷阱能帮你更好地运用重定向。

•   传递数据的技巧：由于重定向会丢失初始请求的request属性，如果需要传递数据，可以：

    ◦   追加查询字符串：response.sendRedirect("/success?message=LoginOK")，然后在目标页面通过request.getParameter("message")获取。

    ◦   使用Session：将数据存入HttpSession，在目标页面取出后再从Session中移除，避免冗余。
        request.getSession().setAttribute("tempMessage", "操作成功！");
        response.sendRedirect("/result.jsp");


•   路径问题：使用相对路径和绝对路径时需注意上下文路径。最稳妥的方式是使用以/开头的路径，它相对于当前Web应用的根目录（context path）。在sendRedirect中，以/开头路径表示从当前应用根目录开始。

•   警惕重定向循环：务必避免A重定向到B，B又重定向回A的情况。这会导致浏览器报错。在编写登录检查等过滤器或拦截器时尤其要注意逻辑判断。

•   安全性考虑：如果重定向的目标URL来自用户输入，必须进行严格校验，防止重定向攻击（攻击者诱导用户跳转到恶意网站）。
String target = request.getParameter("redirectTo");
// 简单的白名单校验示例
if (target != null && target.startsWith("/yourapp/")) {
response.sendRedirect(target);
} else {
// 跳转到默认安全页面
response.sendRedirect("/yourapp/home");
}


💎 总结

简单来说，重定向是服务器对浏览器说：“你要的内容不在这儿，去另一个地方找吧。” 它通过两次HTTP请求完成跳转，会改变浏览器地址栏的URL。


**是什么**
- 全局设计令牌是把颜色、间距、圆角、阴影、字号等视觉值，统一定义成一组可复用的“变量”
- 项目里通过 CSS 变量实现，文件路径：`src/main/resources/static/css/ui-tokens.css`
- 所有页面和组件只引用令牌，不直接写具体数值，从而保持风格一致、方便整体换肤

**为什么用**
- 一致性：按钮、输入、卡片等用同一套色值与尺寸，视觉统一
- 可维护：改一个令牌即可全站生效（比如把主色从蓝调成紫）
- 可扩展：易做主题切换（浅色/深色）、品牌定制、节日皮肤

**怎么用**
- 引入令牌文件：页面 `<head>` 已包含 `link href="/css/ui-tokens.css"`
- 在组件样式里引用令牌（已写在 `ui-components.css`），例如：
  - 主按钮背景：`background-color: var(--primary-500)`
  - 输入框圆角：`border-radius: var(--radius-sm)`
  - 面板阴影：`box-shadow: var(--shadow-1)`
- 在你写的自定义样式中也可以直接使用这些变量：
  - 例如卡片标题颜色：`color: var(--text-primary)`
  - 卡片内边距：`padding: var(--space-lg)`

**示例**
- 使用主色与圆角令牌为自定义按钮上色：
  - `.my-btn { background: var(--primary-500); border-radius: var(--radius-md); }`
- 给盒子加统一阴影和边框：
  - `.my-box { box-shadow: var(--shadow-2); border: 1px solid var(--neutral-300); }`

**主题切换思路**
- 令牌文件通常在 `:root` 下定义一套默认值；如果需要暗色主题，可在 `.theme-dark` 或 `@media (prefers-color-scheme: dark)` 重写同名变量
- 页面切换主题时，只需给 `html` 或 `body` 加上对应类名即可，组件会自动跟随

**与组件的关系**
- `ui-components.css` 把这些令牌应用到实际组件（按钮、输入、表格、提示、应用栏、侧边导航）
- 你编写新组件时，应优先用令牌而不是具体数值，这样新组件能自动适配全局风格

**快速记忆**
- 颜色：`--primary-*` 主色，`--success-*` 成功，`--warning-*` 警告，`--error-*` 错误，`--neutral-*` 中性色
- 文本：`--text-primary` 主文本，`--text-secondary` 次文本
- 间距：`--space-sm/md/lg/xl` 等
- 圆角：`--radius-sm/md/lg/xl`
- 阴影：`--shadow-1/2/3`

