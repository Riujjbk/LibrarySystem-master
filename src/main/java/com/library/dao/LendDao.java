package com.library.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.library.bean.Lend;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface LendDao extends BaseMapper<Lend> {

    @Update("update library.lend_list set back_date = curdate() where book_id = #{book_id} and reader_id = #{reader_id} and back_date is null limit 1")
    int returnBookOne(@Param("book_id") long book_id, @Param("reader_id") long reader_id);

    @Update("update library.book_info set number = number + 1 where book_id = #{book_id}")
    int returnBookTwo(@Param("book_id") long book_id);

    @Insert("insert into library.lend_list(book_id, reader_id, lend_date) values(#{book_id}, #{reader_id}, curdate())")
    int lendBookOne(@Param("book_id") long book_id, @Param("reader_id") long reader_id);

    @Update("update library.book_info set number = number - 1 where book_id = #{book_id} and number > 0")
    int lendBookTwo(@Param("book_id") long book_id);

    @Select("select l.*, b.name as bookName, r.name as readerName from library.lend_list l left join library.book_info b on l.book_id = b.book_id left join library.reader_info r on l.reader_id = r.reader_id")
    List<Lend> lendList();

    @Select("select l.*, b.name as bookName from library.lend_list l left join library.book_info b on l.book_id = b.book_id where l.reader_id = #{reader_id}")
    List<Lend> myLendList(@Param("reader_id") long reader_id);

}
