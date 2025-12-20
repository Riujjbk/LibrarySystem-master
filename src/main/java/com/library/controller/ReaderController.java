package com.library.controller;

import com.library.bean.ReaderCard;
import com.library.bean.ReaderInfo;
import com.library.service.LoginService;
import com.library.service.ReaderCardService;
import com.library.service.ReaderInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
public class ReaderController {
    @Autowired
    private ReaderInfoService readerInfoService;

    @Autowired
    private LoginService loginService;

    @Autowired
    private ReaderCardService readerCardService;

    /**
     * 构建ReaderInfo对象
     * @param readerId 读者ID，0表示新建读者
     * @param name 读者姓名
     * @param sex 性别（男/女）
     * @param birth 出生日期字符串（yyyy-MM-dd格式）
     * @param address 联系地址
     * @param phone 联系电话
     * @return 初始化后的ReaderInfo对象
     */
    private ReaderInfo getReaderInfo(long readerId, String name, String sex, String birth, String address,
            String phone) {
        ReaderInfo readerInfo = new ReaderInfo();
        Date date = new Date();
        try {
            // 使用yyyy-MM-dd格式解析出生日期字符串
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            // 如果日期格式不正确会抛出ParseException，此处简单打印异常
            date = df.parse(birth);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        // 依次设置读者信息属性
        readerInfo.setAddress(address);
        readerInfo.setName(name);
        readerInfo.setReaderId(readerId);
        readerInfo.setPhone(phone);
        readerInfo.setSex(sex);
        readerInfo.setBirth(date);
        return readerInfo;
    }

    @RequestMapping("allreaders.html")
    public String allBooks() {
        return "redirect:/admin/main.html";
    }

    /**
     * 处理读者删除请求的控制器方法
     * @param request HTTP请求对象，用于获取请求参数
     * @param redirectAttributes 重定向属性对象，用于在重定向时传递临时属性
     * @return 重定向到管理员主页面的视图名称
     */
    @RequestMapping("reader_delete.html")
    public String readerDelete(HttpServletRequest request, RedirectAttributes redirectAttributes) {
        // 从请求参数中获取并转换读者ID
        long readerId = Long.parseLong(request.getParameter("readerId"));

        // 调用服务层方法删除读者信息和借阅卡信息
        if (readerInfoService.deleteReaderInfo(readerId) && readerCardService.deleteReaderCard(readerId)) {
            // 删除成功时添加成功消息到重定向属性
            redirectAttributes.addFlashAttribute("succ", "删除成功！");
        } else {
            // 删除失败时添加错误消息到重定向属性
            redirectAttributes.addFlashAttribute("error", "删除失败！");
        }

        // 返回重定向到管理员主页面的指令
        return "redirect:/admin/main.html";
    }

    @RequestMapping("/reader_info.html")
    public String toReaderInfo(HttpServletRequest request) {
        return "redirect:/reader/main.html";
    }

    @RequestMapping("reader_edit.html")
    public String readerInfoEdit(HttpServletRequest request) {
        return "redirect:/admin/main.html";
    }

    /**
     * 处理读者信息编辑提交请求的控制器方法
     * @param request HTTP请求对象，用于获取读者ID参数
     * @param name 读者姓名参数
     * @param sex 性别参数（男/女）
     * @param birth 出生日期字符串（yyyy-MM-dd格式）
     * @param address 联系地址参数
     * @param phone 联系电话参数
     * @param redirectAttributes 重定向属性对象，用于传递操作结果消息
     * @return 重定向到管理员主页面的视图名称
     */
    @RequestMapping("reader_edit_do.html")
    public String readerInfoEditDo(HttpServletRequest request, String name, String sex, String birth, String address,
            String phone, RedirectAttributes redirectAttributes) {
        // 从请求参数中获取并转换读者ID
        long readerId = Long.parseLong(request.getParameter("readerId"));

        // 使用辅助方法构建包含更新信息的ReaderInfo对象
        ReaderInfo readerInfo = getReaderInfo(readerId, name, sex, birth, address, phone);

        // 同时更新读者基本信息和借阅卡信息
        if (readerInfoService.editReaderInfo(readerInfo) && readerInfoService.editReaderCard(readerInfo)) {
            // 更新成功时添加成功消息到重定向属性
            redirectAttributes.addFlashAttribute("succ", "读者信息修改成功！");
        } else {
            // 更新失败时添加错误消息到重定向属性
            redirectAttributes.addFlashAttribute("error", "读者信息修改失败！");
        }

        // 返回重定向到管理员主页面的指令
        return "redirect:/admin/main.html";
    }

    @RequestMapping("reader_add.html")
    public String readerInfoAdd() {
        return "redirect:/admin/main.html";
    }

    /**
     * 处理添加读者信息提交请求的控制器方法
     * @param name 读者姓名参数
     * @param sex 性别参数（男/女）
     * @param birth 出生日期字符串（yyyy-MM-dd格式）
     * @param address 联系地址参数
     * @param phone 联系电话参数
     * @param password 借阅卡初始密码参数
     * @param redirectAttributes 重定向属性对象，用于传递操作结果消息
     * @return 重定向到管理员主页面的视图名称
     */
    @RequestMapping("reader_add_do.html")
    public String readerInfoAddDo(String name, String sex, String birth, String address, String phone, String password,
            RedirectAttributes redirectAttributes) {
        // 创建新的ReaderInfo对象（readerId设为0表示新建读者）
        ReaderInfo readerInfo = getReaderInfo(0, name, sex, birth, address, phone);

        // 检查插入是否成功（可选）
        boolean isSuccess = readerInfoService.addReaderInfo(readerInfo);
        // 获取数据库生成的readerId（通常由数据库自增主键生成）
        long readerId = readerInfo.getReaderId(); // 从对象中获取生成的 readerId
        // 更新readerInfo对象的readerId属性（确保对象状态同步）
        readerInfo.setReaderId(readerId);

        // 依次执行读者信息和借阅卡信息的添加操作
        if (readerId > 0 && readerCardService.addReaderCard(readerInfo, password)) {
            // 添加成功时添加成功消息到重定向属性
            redirectAttributes.addFlashAttribute("succ", "添加读者信息成功！");
        } else {
            // 添加失败时添加错误消息到重定向属性
            redirectAttributes.addFlashAttribute("succ", "添加读者信息失败！");
        }
        return "redirect:/admin/main.html";
    }

    @RequestMapping("reader_info_edit.html")
    public String readerInfoEditReader(HttpServletRequest request) {
        return "redirect:/reader/main.html";
    }

    /**
     * 处理普通读者信息编辑提交请求的控制器方法
     * @param request HTTP请求对象，用于获取当前会话中的借阅卡信息
     * @param name 读者姓名参数
     * @param sex 性别参数（男/女）
     * @param birth 出生日期字符串（yyyy-MM-dd格式）
     * @param address 联系地址参数
     * @param phone 联系电话参数
     * @param redirectAttributes 重定向属性对象，用于传递操作结果消息
     * @return 重定向到读者主页面的视图名称
     */
    @RequestMapping("reader_edit_do_r.html")
    public String readerInfoEditDoReader(HttpServletRequest request, String name, String sex, String birth,
            String address, String phone, RedirectAttributes redirectAttributes) {
        // 从当前会话中获取借阅卡对象（包含当前登录读者的ID）
        ReaderCard readerCard = (ReaderCard) request.getSession().getAttribute("readercard");
        // 构建包含更新后的读者信息的ReaderInfo对象
        ReaderInfo readerInfo = getReaderInfo(readerCard.getReaderId(), name, sex, birth, address, phone);

        // 调用服务层方法更新读者基本信息和借阅卡信息
        if (readerInfoService.editReaderInfo(readerInfo) && readerInfoService.editReaderCard(readerInfo)) {
            // 更新成功后重新查询最新的借阅卡信息并更新会话
            ReaderCard readerCardNew = loginService.findReaderCardByReaderId(readerCard.getReaderId());
            request.getSession().setAttribute("readercard", readerCardNew);
            redirectAttributes.addFlashAttribute("succ", "信息修改成功！");
        } else {
            // 更新失败时添加错误提示
            redirectAttributes.addFlashAttribute("error", "信息修改失败！");
        }
        return "redirect:/reader/main.html";
    }

    @RequestMapping(value = "/api/readers", method = RequestMethod.GET)
    public @ResponseBody List<ReaderInfo> apiListReaders() {
        return readerInfoService.readerInfos();
    }

    @RequestMapping(value = "/api/readers/{id}", method = RequestMethod.GET)
    public @ResponseBody ReaderInfo apiGetReader(@PathVariable("id") long id) {
        return readerInfoService.getReaderInfo(id);
    }

    @RequestMapping(value = "/api/readers", method = RequestMethod.POST)
    public @ResponseBody java.util.Map<String, Object> apiAddReader(@RequestBody java.util.Map<String, String> body) {
        String name = body.getOrDefault("name", "").trim();
        String sex = body.getOrDefault("sex", "").trim();
        String birth = body.getOrDefault("birth", "").trim();
        String address = body.getOrDefault("address", "").trim();
        String phone = body.getOrDefault("phone", "").trim();
        String password = body.getOrDefault("password", "").trim();
        String username = body.getOrDefault("username", name).trim();
        java.util.HashMap<String, Object> res = new java.util.HashMap<>();
        if (username.isEmpty() || name.isEmpty() || password.isEmpty()) {
            res.put("success", false);
            res.put("message", "用户名、姓名、密码不能为空");
            return res;
        }
        ReaderInfo readerInfo = getReaderInfo(0, name, sex, birth, address, phone);
        boolean ok = readerInfoService.addReaderInfo(readerInfo);
        long readerId = readerInfo.getReaderId();
        boolean ok2 = readerId > 0 && readerCardService.addReaderCard(readerId, username, password);
        res.put("success", ok && ok2);
        res.put("readerId", readerId);
        return res;
    }

    @RequestMapping(value = "/api/readers/{id}", method = RequestMethod.PUT)
    public @ResponseBody java.util.Map<String, Object> apiEditReader(@PathVariable("id") long id,
            @RequestBody java.util.Map<String, String> body) {
        String name = body.getOrDefault("name", "").trim();
        String sex = body.getOrDefault("sex", "").trim();
        String birth = body.getOrDefault("birth", "").trim();
        String address = body.getOrDefault("address", "").trim();
        String phone = body.getOrDefault("phone", "").trim();
        java.util.HashMap<String, Object> res = new java.util.HashMap<>();
        if (name.isEmpty()) {
            res.put("success", false);
            return res;
        }
        ReaderInfo readerInfo = getReaderInfo(id, name, sex, birth, address, phone);
        boolean ok = readerInfoService.editReaderInfo(readerInfo);
        res.put("success", ok);
        return res;
    }

    @RequestMapping(value = "/api/readers/{id}", method = RequestMethod.DELETE)
    public @ResponseBody java.util.Map<String, Object> apiDeleteReader(@PathVariable("id") long id) {
        boolean ok = readerInfoService.deleteReaderInfo(id) && readerCardService.deleteReaderCard(id);
        java.util.HashMap<String, Object> res = new java.util.HashMap<>();
        res.put("success", ok);
        return res;
    }
}
