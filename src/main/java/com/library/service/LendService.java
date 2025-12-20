package com.library.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.library.bean.Lend;
import com.library.dao.BookDao;
import com.library.dao.LendDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class LendService {
    @Autowired
    private LendDao lendDao;
    @Autowired
    private BookDao bookDao;

    /**
     * 处理书籍归还操作，更新借阅记录并增加书籍库存。
     * 该方法在事务中执行，若任一操作失败则回滚。
     * @param bookId 书籍ID
     * @param readerId 读者ID
     * @return 是否归还成功
     * @throws RuntimeException 若增加库存失败
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean returnBook(long bookId, long readerId) {
        // 更新借阅记录状态（如标记为已归还）
        int mark = lendDao.returnBookOne(bookId, readerId);
        if (mark <= 0)
            return false;
        // 增加对应书籍库存数量
        int inc = lendDao.returnBookTwo(bookId);
        if (inc <= 0)
            throw new RuntimeException("increase stock failed");
        return true;
    }

    /**
     * 处理书籍借出操作，减少书籍库存并创建借阅记录。
     * 该方法在事务中执行，若任一操作失败则回滚。
     * @param bookId 书籍ID
     * @param readerId 读者ID
     * @return 是否借出成功
     * @throws RuntimeException 若插入借阅记录失败
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean lendBook(long bookId, long readerId) {
        // 获取书籍当前库存数量
        Integer stock = bookDao.getStock(bookId);
        // 检查库存是否充足
        if (stock == null || stock <= 0)
            return false;

        // 执行库存减少操作（借出书籍）
        int dec = lendDao.lendBookTwo(bookId);
        if (dec <= 0)
            return false;

        // 创建新的借阅记录
        int ins = lendDao.lendBookOne(bookId, readerId);
        if (ins <= 0)
            throw new RuntimeException("insert lend record failed");
        return true;
    }

    public List<Lend> lendList() {
        return lendDao.lendList();
    }

    public List<Lend> myLendList(long readerId) {
        QueryWrapper<Lend> wrapper = new QueryWrapper<>();
        wrapper.eq("reader_id", readerId);
        return lendDao.selectList(wrapper);
    }

    public boolean deleteLend(long serNum) {
        return lendDao.deleteById(serNum) > 0;
    }

}
