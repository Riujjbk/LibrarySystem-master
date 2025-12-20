package com.library.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.library.bean.ReaderCard;
import com.library.bean.ReaderInfo;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ReaderCardDao extends BaseMapper<ReaderCard> {

    @Select("select count(*) from library.reader_card where reader_id = #{reader_id} and password = #{password}")
    int getIdMatchCount(@Param("reader_id") long reader_id, @Param("password") String password);

    @Select("select count(*) from library.reader_card where username = #{username} and password = #{password}")
    int getUsernameMatchCount(@Param("username") String username, @Param("password") String password);

    @Select("select * from library.reader_card where reader_id = #{reader_id}")
    ReaderCard findReaderByReaderId(@Param("reader_id") long reader_id);

    @Select("select * from library.reader_card where username = #{username}")
    ReaderCard findReaderByUsername(@Param("username") String username);

    @Update("update library.reader_card set password = #{password} where reader_id = #{reader_id}")
    int resetPassword(@Param("reader_id") long reader_id, @Param("password") String password);

    @Insert("insert into library.reader_card(reader_id, username, password) values(#{reader_id}, #{username}, #{password})")
    int addReaderCard(@Param("reader_id") long reader_id, @Param("username") String username,
            @Param("password") String password);

    int deleteReaderCard(long reader_id);

    @Select("select password from library.reader_card where reader_id = #{reader_id}")
    String getPassword(@Param("reader_id") long reader_id);

}
