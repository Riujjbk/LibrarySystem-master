package com.library.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.library.bean.ReaderInfo;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ReaderInfoDao extends BaseMapper<ReaderInfo> {

    @Select("select * from library.reader_info")
    List<ReaderInfo> getAllReaderInfo();

    @Select("select * from library.reader_info where reader_id = #{reader_id}")
    ReaderInfo findReaderInfoByReaderId(long reader_id);

    @Options(useGeneratedKeys = true, keyProperty = "reader_id")
    @Insert("insert into library.reader_info(name, sex, birth, address, phone) values(#{name}, #{sex}, #{birth}, #{address}, #{phone})")
    int addReaderInfo(ReaderInfo readerInfo);

}
