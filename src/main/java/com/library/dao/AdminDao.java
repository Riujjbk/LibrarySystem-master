package com.library.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.library.bean.Admin;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface AdminDao extends BaseMapper<Admin> {

    @Select("select count(*) from library.admin where admin_id = #{admin_id} and password = #{password}")
    int getMatchCount(@Param("admin_id") long admin_id, @Param("password") String password);

    @Select("select count(*) from library.admin where username = #{username} and password = #{password}")
    int getMatchByUsername(@Param("username") String username, @Param("password") String password);

    @Update("update library.admin set password = #{password} where admin_id = #{admin_id}")
    int resetPassword(@Param("admin_id") long admin_id, @Param("password") String password);

    @Select("select password from library.admin where admin_id = #{admin_id}")
    String getPassword(@Param("admin_id") long admin_id);

    @Select("select username from library.admin where admin_id = #{admin_id}")
    String getUsername(@Param("admin_id") long admin_id);

    @Select("select admin_id from library.admin where username = #{username}")
    Long getAdminIdByUsername(@Param("username") String username);

}
