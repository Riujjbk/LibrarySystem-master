package com.library.controller;

import com.library.bean.ReaderCard;
import com.library.bean.Lend;
import com.library.service.BookService;
import com.library.service.LendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Controller
public class LendController {
    @Autowired
    private LendService lendService;

    @Autowired
    private BookService bookService;

    /**
     * 处理图书删除请求的控制器方法
     *
     * @param request           HTTP请求对象，用于获取请求参数
     * @param redirectAttributes 重定向属性对象，用于传递一次性闪存属性
     * @return 重定向到管理员图书管理页面的URL
     */
    @RequestMapping("/deletebook.html")
    public String deleteBook(HttpServletRequest request, RedirectAttributes redirectAttributes) {
        // 从请求参数中获取图书ID并转换为长整型
        long bookId = Long.parseLong(request.getParameter("bookId"));

        // 调用业务服务层方法执行图书删除操作
        if (bookService.deleteBook(bookId)) {
            // 删除成功时添加成功提示信息到重定向属性
            redirectAttributes.addFlashAttribute("succ", "图书删除成功！");
        } else {
            // 删除失败时添加错误提示信息到重定向属性
            redirectAttributes.addFlashAttribute("error", "图书删除失败！");
        }

        // 返回重定向响应，跳转到管理员图书管理页面
        return "redirect:/admin_books.html";
    }

    @RequestMapping("/lendlist.html")
    public String lendList(HttpServletRequest request) {
        return "redirect:/admin/main.html";
    }

    @RequestMapping("/mylend.html")
    public String myLend(HttpServletRequest request) {
        return "redirect:/reader/main.html";
    }

    /**
     * 处理借阅记录删除请求的控制器方法
     *
     * @param request           HTTP请求对象，用于获取请求参数中的借阅记录序列号
     * @param redirectAttributes 重定向属性对象，用于传递删除操作后的提示信息
     * @return 重定向到管理员主页面的URL
     */
    @RequestMapping("/deletelend.html")
    public String deleteLend(HttpServletRequest request, RedirectAttributes redirectAttributes) {
        // 从请求参数中获取借阅记录序列号并转换为长整型
        long serNum = Long.parseLong(request.getParameter("serNum"));

        // 调用业务服务层方法执行借阅记录删除操作
        if (lendService.deleteLend(serNum)) {
            // 删除成功时添加成功提示信息到重定向属性
            redirectAttributes.addFlashAttribute("succ", "记录删除成功！");
        } else {
            // 删除失败时添加错误提示信息到重定向属性
            redirectAttributes.addFlashAttribute("error", "记录删除失败！");
        }

        // 返回重定向响应，跳转到管理员主页面
        return "redirect:/admin/main.html";
    }

    @RequestMapping(value = "/api/lends", method = RequestMethod.GET)
    public @ResponseBody List<Lend> apiLendList() {
        return lendService.lendList();
    }

    /**
     * 处理获取当前读者借阅记录的API请求
     *
     * @param request HTTP请求对象，用于获取用户会话中的读者卡信息
     * @return 当前登录读者的借阅记录列表，若未登录则返回空列表
     */
    @RequestMapping(value = "/api/my-lends", method = RequestMethod.GET)
    public @ResponseBody List<Lend> apiMyLends(HttpServletRequest request) {
        // 从会话中获取读者卡对象
        ReaderCard rc = (ReaderCard) request.getSession().getAttribute("readercard");

        // 如果读者卡不存在（未登录），返回空列表
        if (rc == null)
            return java.util.Collections.emptyList();

        // 调用服务层方法，根据读者ID获取借阅记录列表
        return lendService.myLendList(rc.getReaderId());
    }

    /**
     * 处理图书借阅的API请求
     *
     * @param request  HTTP请求对象，用于获取用户会话信息
     * @param bookId   需要借阅的图书ID
     * @return 响应实体对象，包含操作结果和状态码
     */
    @RequestMapping(value = "/api/lend", method = RequestMethod.POST)
    public @ResponseBody ResponseEntity<java.util.Map<String, Object>> apiLend(HttpServletRequest request,
            @RequestParam("bookId") long bookId) {
        // 从会话中获取读者卡对象
        ReaderCard rc = (ReaderCard) request.getSession().getAttribute("readercard");
        // 构建响应结果容器
        java.util.HashMap<String, Object> res = new java.util.HashMap<>();

        // 验证用户登录状态
        if (rc == null) {
            res.put("success", false);
            res.put("message", "未登录或会话失效");
            return new ResponseEntity<>(res, HttpStatus.UNAUTHORIZED);
        }

        // 获取图书详细信息
        com.library.bean.Book book = bookService.getBook(bookId);
        // 验证图书是否存在
        if (book == null) {
            res.put("success", false);
            res.put("message", "图书不存在");
            return new ResponseEntity<>(res, HttpStatus.BAD_REQUEST);
        }

        // 检查图书库存是否充足
        if (book.getNumber() <= 0) {
            res.put("success", false);
            res.put("message", "库存不足");
            return new ResponseEntity<>(res, HttpStatus.BAD_REQUEST);
        }

        // 执行借阅操作并处理异常
        boolean ok = false;
        String errMsg = null;
        try {
            // 调用服务层借阅方法
            ok = lendService.lendBook(bookId, rc.getReaderId());
        } catch (Exception e) {
            ok = false;
            // 捕获并记录异常信息
            errMsg = e.getMessage();
        }

        // 构建最终响应结果
        res.put("success", ok);
        res.put("message", ok ? "借阅成功" :
            (errMsg != null && !errMsg.isEmpty() ? errMsg : "借阅失败，请稍后重试"));
        return new ResponseEntity<>(res, ok ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }

    /**
     * 处理图书归还的API请求
     *
     * @param request  HTTP请求对象，用于获取用户会话中的读者卡信息
     * @param bookId   需要归还的图书ID，通过请求参数传递
     * @return 响应实体对象，包含操作结果的状态码和JSON格式的响应数据：
     *         - success: 操作结果布尔值
     *         - message: 操作结果描述信息
     */
    @RequestMapping(value = "/api/return", method = RequestMethod.POST)
    public @ResponseBody ResponseEntity<java.util.Map<String, Object>> apiReturn(HttpServletRequest request,
            @RequestParam("bookId") long bookId) {
        // 从会话中获取读者卡对象，用于验证用户身份和获取借阅人ID
        ReaderCard rc = (ReaderCard) request.getSession().getAttribute("readercard");
        // 创建响应结果容器，存储操作结果信息
        java.util.HashMap<String, Object> res = new java.util.HashMap<>();

        // 验证用户登录状态
        if (rc == null) {
            res.put("success", false);
            res.put("message", "未登录或会话失效");
            return new ResponseEntity<>(res, HttpStatus.UNAUTHORIZED);
        }

        // 初始化操作结果标志和异常信息变量
        boolean ok = false;
        String errMsg = null;

        // 执行归还操作并捕获异常
        try {
            // 调用服务层方法处理图书归还逻辑
            // 参数：图书ID和读者ID
            ok = lendService.returnBook(bookId, rc.getReaderId());
        } catch (Exception e) {
            ok = false;
            // 记录异常信息用于后续返回
            errMsg = e.getMessage();
        }

        // 构建响应结果
        res.put("success", ok);
        res.put("message", ok ? "归还成功" :
            (errMsg != null && !errMsg.isEmpty() ? errMsg : "归还失败，未找到待归还记录"));

        // 返回对应状态码的响应实体
        // 成功返回200状态码，失败返回400状态码
        return new ResponseEntity<>(res, ok ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }

}
