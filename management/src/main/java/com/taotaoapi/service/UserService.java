package com.taotaoapi.service;

import com.taotaoapi.home.article.ArticleList;
import com.taotaoapi.home.article.ArticleQuery;
import com.taotaoapi.home.article.Page;
import com.taotaoapi.home.user.UserDTO;
import com.taotaoapi.home.user.UserUpdateDTO;
import com.taotaoapi.mapper.ArticleMapper;
import com.taotaoapi.mapper.UserMapper;
import com.taotaoapi.home.user.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserMapper userMapper;
    private final ArticleMapper articleMapper;
    private final ArticleService articleService;
    private final UploadService uploadService;

    // 取得使用者發佈的文章
    public Page<ArticleList> getUserArticle(ArticleQuery query, String email) {
        Integer userId = userMapper.findByEmail(email).getId();
        query.setAuthorId(userId);
        List<ArticleList> list = articleMapper.selectArticleList(query, userId);
        articleService.enrichArticles(list);
        return articleService.buildPageResponse(list, query, userMapper.countUserArticles(userId)); // 記得要把這改成查詢作者文章數
    }

    // 取得個人主頁資訊
    public UserDTO getUserDashboard(String email) {
        // 取得使用者基礎資料 (假設透過 username 查)
        User user = userMapper.findByEmail(email);
        // 透過 userId 取得統計數據
        Integer userId = user.getId();
        Long postCount = userMapper.countUserArticles(userId);
        Long favoriteCount = articleMapper.countFavoriteArticles(userId);

        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setNickname(user.getNickname());
        dto.setIntro(user.getIntro());
        dto.setImageUrl(user.getImageUrl());
        dto.setPostCount(postCount);
        dto.setFavoriteCount(favoriteCount);

        return dto;
    }

    // 更新個人主頁資訊
    @Transactional
    public void updateUserDashboard(String email, UserUpdateDTO updateDTO) {
        // 查詢用戶
        User user = userMapper.findByEmail(email);
        // 判斷是否要更新頭貼，並記下舊的 URL
        String oldImageUrl = user.getImageUrl();
        boolean shouldUpdateImage = updateDTO.getImageUrl() != null
                && !updateDTO.getImageUrl().equals(oldImageUrl);

        // 更新記憶體中的User
        if (updateDTO.getNickname() != null) user.setNickname(updateDTO.getNickname());
        if (updateDTO.getIntro() != null) user.setIntro(updateDTO.getIntro());
        if (shouldUpdateImage) {
            user.setImageUrl(updateDTO.getImageUrl());
        }
        // 更新資料庫
        userMapper.updateUser(user);

        // 在確定更新新圖片，且確實有舊圖片存在時，才執行刪除
        if (shouldUpdateImage && oldImageUrl != null && !oldImageUrl.isBlank()) {
            try {
                uploadService.deleteByUrl(oldImageUrl);
            } catch (Exception e) {
                log.error("更新資料成功，但清理舊頭貼失敗: {}", oldImageUrl, e);
            }
        }
    }
}
