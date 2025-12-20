package com.library.controller;

import com.library.bean.Book;
import com.library.bean.Lend;
import com.library.bean.ReaderCard;
import com.library.service.BookService;
import com.library.service.LendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
public class BookController {
    @Autowired
    private BookService bookService;
    @Autowired
    private LendService lendService;

    /**
     * 将字符串格式的日期转换为Date对象
     *
     * @param pubstr 需要转换的日期字符串，预期格式为"yyyy-MM-dd"
     * @return 成功解析则返回对应的Date对象；解析失败时返回当前系统时间，并打印异常信息到控制台
     * @note 该方法主要用于处理图书出版日期的字符串转换，当输入格式不正确时会自动使用当前日期作为默认值
     */
    private Date getDate(String pubstr) {
        try {
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            return df.parse(pubstr);
        } catch (ParseException e) {
            e.printStackTrace();
            return new Date();
        }
    }

    /**
     * 处理图书查询请求
     *
     * @param searchWord 用户输入的图书搜索关键词（当前实现未实际使用该参数）
     * @return 始终重定向至管理员首页（可能为占位方法，需确认实际业务逻辑）
     *         注意：当前方法未实现实际的图书查询功能，仅作页面跳转
     */
    @RequestMapping("/querybook.html")
    public String queryBookDo(String searchWord) {
        return "redirect:/admin/main.html";
    }

    /**
     * 处理读者端图书查询请求
     *
     * @param searchWord 用户输入的图书搜索关键词（当前实现未实际使用该参数）
     * @return 始终返回重定向至读者首页的响应
     *         注意：当前方法未实现实际的图书查询功能，仅作页面跳转
     *         需确认实际业务逻辑是否需要保留此行为
     */
    @RequestMapping("/reader_querybook_do.html")
    public String readerQueryBookDo(String searchWord) {
        return "redirect:/reader/main.html";
    }

    /**
     * 处理管理员图书管理页面请求
     *
     * @return 返回重定向至管理员首页的响应
     *         注意：当前方法仅实现页面跳转功能，未处理具体的图书管理业务逻辑
     *         需确认是否需要保留此行为或补充实际的图书管理页面处理逻辑
     */
    @RequestMapping("/admin_books.html")
    public String adminBooks() {
        return "redirect:/admin/main.html";
    }

    /**
     * 分页查询图书信息的API接口
     *
     * @param page 页码参数，默认值为1，表示当前请求的页数
     * @param size 每页大小，默认值为10，控制每页返回的图书数量
     * @param q    查询关键字（可选），用于实现图书的模糊搜索功能
     * @return 返回包含分页数据的Map对象，包含以下字段：
     *         - records: 当前页的图书列表数据
     *         - total: 总记录数
     *         - pages: 总页数
     *         - current: 当前页码
     *         - size: 每页大小
     *
     *         该方法通过调用bookService.pageBooks()获取分页数据，
     *         使用MyBatis Plus的Page对象进行分页处理，并将结果转换为前端需要的JSON格式
     */
    @RequestMapping(value = "/api/books", method = RequestMethod.GET)
    public @ResponseBody java.util.Map<String, Object> apiListBooks(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "q", required = false) String q) {
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<Book> p = bookService.pageBooks(page, size, q);
        java.util.HashMap<String, Object> res = new java.util.HashMap<>();
        res.put("records", p.getRecords());
        res.put("total", p.getTotal());
        res.put("pages", p.getPages());
        res.put("current", p.getCurrent());
        res.put("size", p.getSize());
        return res;
    }

    /**
     * 根据图书ID查询图书详细信息的API接口
     *
     * @param id 图书的唯一标识符，从URL路径参数中获取
     * @return 返回对应的Book对象，包含完整的图书信息
     *         若未找到对应ID的图书则返回null
     *         该接口主要用于前端通过ID精确查询图书详情
     */
    @RequestMapping(value = "/api/books/{id}", method = RequestMethod.GET)
    public @ResponseBody Book apiGetBook(@PathVariable("id") Long id) {
        return bookService.getBook(id);
    }

    /**
     * 处理添加图书的API请求
     *
     * @param book 包含图书信息的请求体对象，需包含书名、作者等必填字段
     * @return 返回操作结果的Map对象，包含以下字段：
     *         - success: 操作是否成功的布尔值
     *         - error: 操作失败时的错误信息（可选）
     *
     *         该方法执行以下操作：
     *         1. 校验必填字段（书名、作者不能为空）
     *         2. 为可选字段设置默认值（出版社/ISBN/语言/价格等）
     *         3. 调用bookService.addBook()执行实际添加操作
     *         4. 返回包含操作结果的JSON响应
     *
     *         注意：所有可选字段在未提供时会使用预设默认值：
     *         - 出版社 -> "未知出版社"
     *         - ISBN -> "0000000000000"
     *         - 语言 -> "zh"
     *         - 价格 -> 0元
     *         - 出版日期 -> 当前系统时间
     *         - 分类ID -> 1
     *         - 库存数量 -> 0
     */
    @RequestMapping(value = "/api/books", method = RequestMethod.POST)
    public @ResponseBody java.util.Map<String, Object> apiAddBook(@RequestBody Book book) {
        java.util.HashMap<String, Object> res = new java.util.HashMap<>();
        try {
            // 必填字段校验
            if (book.getName() == null || book.getName().trim().isEmpty()) {
                res.put("success", false);
                res.put("error", "书名不能为空");
                return res;
            }
            if (book.getAuthor() == null || book.getAuthor().trim().isEmpty()) {
                res.put("success", false);
                res.put("error", "作者不能为空");
                return res;
            }

            // 可选字段默认值处理
            if (book.getPublish() == null || book.getPublish().trim().isEmpty()) {
                book.setPublish("未知出版社");
            }
            if (book.getIsbn() == null || book.getIsbn().trim().isEmpty()) {
                book.setIsbn("0000000000000");
            }
            if (book.getLanguage() == null || book.getLanguage().trim().isEmpty()) {
                book.setLanguage("zh");
            }
            if (book.getPrice() == null) {
                book.setPrice(new java.math.BigDecimal("0"));
            }
            if (book.getPubdate() == null) {
                book.setPubdate(new java.util.Date());
            }
            if (book.getClassId() <= 0) {
                book.setClassId(1);
            }
            if (book.getNumber() < 0) {
                book.setNumber(0);
            }

            // 执行添加操作并返回结果
            boolean ok = bookService.addBook(book);
            res.put("success", ok);
        } catch (Exception e) {
            // 异常处理：返回失败状态及错误信息
            res.put("success", false);
            res.put("error", e.getMessage());
        }
        return res;
    }

    /**
     * 处理更新图书信息的API请求
     *
     * @param id   从URL路径参数中获取的图书唯一标识符
     * @param book 包含更新后的图书信息的请求体对象
     * @return 返回操作结果的Map对象，包含以下字段：
     *         - success: 操作是否成功的布尔值
     *
     *         该方法执行以下操作：
     *         1. 将路径参数id设置到book对象的bookId字段中
     *         2. 调用bookService.editBook()执行实际更新操作
     *         3. 返回包含操作结果的JSON响应
     */
    @RequestMapping(value = "/api/books/{id}", method = RequestMethod.PUT)
    public @ResponseBody java.util.Map<String, Object> apiEditBook(@PathVariable("id") Long id,
            @RequestBody Book book) {
        java.util.HashMap<String, Object> res = new java.util.HashMap<>();
        if (book.getName() == null || book.getName().trim().isEmpty()
                || book.getAuthor() == null || book.getAuthor().trim().isEmpty()) {
            res.put("success", false);
            res.put("error", "书名和作者不能为空");
            return res;
        }
        book.setBookId(id);
        boolean ok = bookService.editBook(book);
        res.put("success", ok);
        return res;
    }

    /**
     * 处理删除图书的API请求
     *
     * @param id 从URL路径参数中获取的图书唯一标识符
     * @return 返回操作结果的Map对象，包含以下字段：
     *         - success: 操作是否成功的布尔值
     *         该方法执行以下操作：
     *         1. 调用bookService.deleteBook()执行实际删除操作
     *         2. 返回包含操作结果的JSON响应
     *         注意：实际删除逻辑由bookService实现，需确保：
     *         - 目标图书存在时才能成功删除
     *         - 处理可能存在的关联数据约束（如借阅记录）
     */
    @RequestMapping(value = "/api/books/{id}", method = RequestMethod.DELETE)
    public @ResponseBody java.util.Map<String, Object> apiDeleteBook(@PathVariable("id") Long id) {
        boolean ok = bookService.deleteBook(id);
        java.util.HashMap<String, Object> res = new java.util.HashMap<>();
        res.put("success", ok);
        return res;
    }

    /**
     * 处理图书添加页面请求
     *
     * @return 返回重定向至管理员首页的响应
     *         注意：当前方法仅实现页面跳转功能，未处理具体的图书添加业务逻辑
     *         可能需要配合/book_add_do.html接口使用完成完整添加流程
     */
    @RequestMapping("/book_add.html")
    public String addBook() {
        return "redirect:/admin/main.html";
    }

    /**
     * 处理图书添加表单提交请求
     *
     * @param pubstr             从请求参数中获取的出版日期字符串，格式应为"yyyy-MM-dd"
     * @param book               包含图书表单数据的请求参数对象，包含书名、作者等必要字段
     * @param redirectAttributes 用于在重定向时携带提示信息的对象
     * @return 返回重定向至管理员首页的响应，通过flash属性传递操作结果提示
     *
     *         该方法执行以下操作：
     *         1. 将出版日期字符串转换为Date对象并设置到book对象
     *         2. 调用bookService.addBook()执行实际添加操作
     *         3. 根据操作结果设置不同的提示信息：
     *         - 成功时设置"图书添加成功！"
     *         - 失败时设置"图书添加失败！"
     *         4. 最终重定向至管理员首页
     *
     *         注意：该方法与/book_add.html页面配合使用，处理实际的图书添加业务逻辑
     *         使用RedirectAttributes保证重定向时提示信息的传递
     */
    @RequestMapping("/book_add_do.html")
    public String addBookDo(@RequestParam(value = "pubstr") String pubstr, Book book,
            RedirectAttributes redirectAttributes) {
        book.setPubdate(getDate(pubstr)); // 调用私有方法将日期字符串转换为Date对象
        if (bookService.addBook(book)) { // 调用服务层方法执行添加操作
            redirectAttributes.addFlashAttribute("succ", "图书添加成功！");
        } else {
            redirectAttributes.addFlashAttribute("succ", "图书添加失败！");
        }
        return "redirect:/admin/main.html"; // 重定向至管理员首页
    }

    /**
     * 处理图书修改页面请求
     *
     * @param request HTTP请求对象（当前实现未实际使用该参数）
     * @return 始终返回重定向至管理员首页的响应
     *         注意：当前方法仅实现页面跳转功能，未处理具体的图书修改业务逻辑
     *         可能需要配合/book_edit_do.html接口使用完成完整修改流程
     *         需确认是否需要保留此行为或补充实际的图书编辑页面处理逻辑
     */
    @RequestMapping("/updatebook.html")
    public String bookEdit(HttpServletRequest request) {
        return "redirect:/admin/main.html";
    }

    /**
     * 处理图书修改表单提交请求
     *
     * @param pubstr             从请求参数中获取的出版日期字符串，格式应为"yyyy-MM-dd"
     * @param book               包含更新后的图书表单数据的对象，包含书名、作者等必要字段
     * @param redirectAttributes 用于在重定向时携带操作结果提示信息的对象
     * @return 返回重定向至管理员首页的响应，通过flash属性传递操作结果
     *
     *         该方法执行以下操作：
     *         1. 调用私有方法getDate()将日期字符串转换为Date对象并设置到book对象
     *         2. 调用bookService.editBook()执行实际更新操作
     *         3. 根据操作结果设置不同的提示信息：
     *         - 成功时设置"图书修改成功！"到succ属性
     *         - 失败时设置"图书修改失败！"到error属性
     *         4. 最终重定向至管理员首页
     *
     *         注意：该方法与/updatebook.html页面配合使用，处理实际的图书修改业务逻辑
     *         使用RedirectAttributes保证重定向时提示信息的传递
     *         如果日期解析失败，getDate()会使用当前系统时间作为默认值
     */
    @RequestMapping("/book_edit_do.html")
    public String bookEditDo(@RequestParam(value = "pubstr") String pubstr, Book book,
            RedirectAttributes redirectAttributes) {
        book.setPubdate(getDate(pubstr)); // 调用私有方法将日期字符串转换为Date对象
        if (bookService.editBook(book)) {
            redirectAttributes.addFlashAttribute("succ", "图书修改成功！");
        } else {
            redirectAttributes.addFlashAttribute("error", "图书修改失败！");
        }
        return "redirect:/admin/main.html";
    }

    /**
     * 处理管理员图书详情页面请求
     *
     * @param request HTTP请求对象（当前实现未实际使用该参数）
     * @return 返回重定向至管理员首页的响应
     *         注意：当前方法仅实现页面跳转功能，未处理具体的图书详情展示逻辑
     *         需确认是否需要补充实际的图书详情页面处理逻辑
     */
    @RequestMapping("/admin_book_detail.html")
    public String adminBookDetail(HttpServletRequest request) {
        return "redirect:/admin/main.html";
    }

    @RequestMapping("/reader_book_detail.html")
    public String readerBookDetail(HttpServletRequest request) {
        return "redirect:/reader/main.html";
    }

    @RequestMapping("/admin_header.html")
    public String admin_header() {
        return "redirect:/admin/main.html";
    }

    @RequestMapping("/reader_header.html")
    public String reader_header() {
        return "redirect:/reader/main.html";
    }

    @RequestMapping("/reader_books.html")
    public String readerBooks(HttpServletRequest request) {
        return "redirect:/reader/main.html";
    }
}
