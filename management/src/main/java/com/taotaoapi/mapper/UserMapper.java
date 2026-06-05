package com.taotaoapi.mapper;

import com.taotaoapi.user.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {
    void insertUser(User user);
    User findByEmail(String email);
}