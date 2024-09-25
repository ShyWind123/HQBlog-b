package com.shywind.hqblog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shywind.hqblog.Entity.UserInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface LoginMapper extends BaseMapper<UserInfo> {
    @Select("SELECT * FROM user WHERE email = #{email}")
    List<UserInfo> getUserByEmail(@Param("email") String email);

    @Select("INSERT INTO user(username, email, password) VALUES (#{username}, #{email}, #{password})")
    void insertUser(String username, String email, String password);
}
