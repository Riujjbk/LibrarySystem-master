package com.library.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.library.bean.Book;
import com.library.dao.BookDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookService {
    @Autowired
    private BookDao bookDao;

    /**
     * 分页查询书籍信息（支持书名或作者模糊搜索）
     * @param page 当前页码（从1开始计数）
     * @param size 每页显示记录数
     * @param searchWord 搜索关键词（支持书名或作者模糊匹配）
     * @return 包含分页信息和数据的Page对象
     */
    public Page<Book> pageBooks(int page, int size, String searchWord) {
        QueryWrapper<Book> wrapper = new QueryWrapper<>();
        // 构建动态查询条件：当存在搜索词时添加模糊匹配条件
        if (searchWord != null && !searchWord.trim().isEmpty()) {
            // 生成SQL条件：(name LIKE ? OR author LIKE ?)
            // 注意：.or()方法会将两个like条件置于同一优先级层级
            wrapper.like("name", searchWord).or().like("author", searchWord);
        }
        // 使用MyBatis Plus分页插件进行查询
        // new Page<>(page, size) 创建分页对象，自动处理分页参数
        return bookDao.selectPage(new Page<>(page, size), wrapper);
    }

    /**
     * 根据搜索词查询书籍信息（书名或作者模糊匹配）
     * @param searchWord 搜索关键词（支持模糊查询）
     * @return 符合条件的书籍列表
     */
    public List<Book> queryBook(String searchWord) {
        // 构造模糊查询参数（虽然创建了search变量但实际未使用，仍直接使用原始searchWord）
        String search = "%" + searchWord + "%";
        // 创建查询条件构造器
        QueryWrapper<Book> wrapper = new QueryWrapper<>();
        // 设置查询条件：书名或作者包含搜索词的模糊查询
        // 注意：此处使用.or()会导致两个like条件处于同一层级，实际SQL效果为 (name LIKE ? OR author LIKE ?)
        wrapper.like("name", searchWord).or().like("author", searchWord);
        // 执行查询并返回结果
        return bookDao.selectList(wrapper);
    }

    public List<Book> getAllBooks() {
        return bookDao.selectList(null);
    }

    public boolean matchBook(String searchWord) {
        return queryBook(searchWord).size() > 0;
    }

    public boolean addBook(Book book) {
        return bookDao.insert(book) > 0;
    }

    public Book getBook(Long bookId) {
        return bookDao.selectById(bookId);
    }

    public boolean editBook(Book book) {
        return bookDao.updateById(book) > 0;
    }

    public boolean deleteBook(Long bookId) {
        return bookDao.deleteById(bookId) > 0;
    }

}
