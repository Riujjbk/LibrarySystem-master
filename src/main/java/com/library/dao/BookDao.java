package com.library.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.library.bean.Book;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface BookDao extends BaseMapper<Book> {
    @Select("select count(*) from library.book_info where name like #{searchWord} or author like #{searchWord}")
    int matchBook(String searchWord);

    @Select("select * from library.book_info where name like #{searchWord} or author like #{searchWord}")
    List<Book> queryBook(String searchWord);

    @Select("select * from library.book_info")
    List<Book> getAllBooks();

    @Select("select number from library.book_info where book_id = #{book_id}")
    Integer getStock(@Param("book_id") long book_id);

}
