package com.taotaoapi.mapper;

import com.taotaoapi.home.user.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {
    // 新增使用者資料
    void insertUser(User user);
    // 查詢使用者電子郵件
    User findByEmail(String email);

    // 計算使用者文章數量
    Long countUserArticles(Integer userId);

    // 更新個人主頁資訊
    void updateUser(User user);
}