package com.library.bean;

/**
 * MyBatis Plus 注解：
 * @TableId：标识数据库表的主键字段。
 * @TableName：指定该类对应的数据库表名。
 * IdType.INPUT：表示主键由用户手动输入（非自增或数据库生成）
 */

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

@TableName("admin") // 该类映射到数据库中的 admin 表。
public class Admin {

    @TableId(value = "admin_id", type = IdType.INPUT)
    private long admin_id;
    private String password;
    private String username;

    public long getAdminId() {
        return admin_id;
    }

    public void setAdminId(long admin_id) {
        this.admin_id = admin_id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

}
