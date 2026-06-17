package com.taotaoapi.service;

import com.taotaoapi.exception.BusinessException;
import com.taotaoapi.home.Image;
import com.taotaoapi.home.article.*;
import com.taotaoapi.mapper.ArticleMapper;
import com.taotaoapi.mapper.UserMapper;
import com.taotaoapi.user.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ArticleService {
    @Autowired
    private StringRedisTemplate redisTemplate;
    private final UserMapper userMapper;
    private final ArticleMapper articleMapper;

    public Page<ArticleList> getArticleList(
            ArticleQuery query,
            String email
    ) {

        User user = userMapper.findByEmail(email);
        Long userId = Long.valueOf(user.getId());

        List<ArticleList> list =
                articleMapper.selectArticleList(query, userId);

        // cursor 核心：判斷下一頁
        boolean hasMore = false;
        Long lastId = null;

        if (!list.isEmpty()) {
            lastId = list.get(list.size() - 1).getId();
            hasMore = list.size() == query.getSize();
        }

        Page<ArticleList> vo = new Page<>();
        vo.setList(list);
        vo.setHasMore(hasMore);
        Long total = articleMapper.countArticles(query);
        vo.setTotal(total);

        // 回傳 cursor
        vo.setLastId(lastId);

        return vo;
    }

    @Transactional(rollbackFor = Exception.class)
    public void postArticle(ArticleRequest req, String userEmail) {

        User user = userMapper.findByEmail(userEmail);
        if (user == null) {
            throw new BusinessException(400, "使用者不存在，無法發布貼文");
        }

        // 存 article
        Article article = new Article();
        article.setUserId(Long.valueOf(user.getId()));
        article.setCountryId(req.getCountryId());
        article.setCategoryId(req.getCategoryId());
        article.setTitle(req.getTitle());
        article.setContent(req.getContent());
        articleMapper.insertArticle(article);
        Long articleId = article.getId();

        // 存 tags
        if (req.getTags() != null) {
            for (Long tagId : req.getTags()) {
                articleMapper.insertArticleTag(articleId, tagId);
            }
        }

        // 3. 存 images
        if (req.getImageUrls() != null && !req.getImageUrls().isEmpty()) {

            for (int i = 0; i < req.getImageUrls().size(); i++) {
                Image image = new Image();
                image.setArticleId(articleId);
                image.setImageUrl(req.getImageUrls().get(i));
                image.setSortOrder(i);
                image.setIsCover(i == 0);

                articleMapper.insertArticleImage(image);
            }
        }
    }

    // 新增、取消點讚
    // 專門負責攔截 Redis 連擊
    public String postArticleLike(Long articleId, String email) {
        String lockKey = "lock:like:" + email + ":" + articleId;

        Boolean isLock = redisTemplate.opsForValue().setIfAbsent(lockKey, "1", Duration.ofMillis(300));

        if (Boolean.FALSE.equals(isLock)) {
            User user = userMapper.findByEmail(email);
            int count = articleMapper.checkExists(Long.valueOf(user.getId()), articleId);

            return count > 0 ? "取消點讚成功" : "點讚成功";
        }
        // 鎖拿到了，再呼叫真正內層的事務方法去操作資料庫
        return this.executeLikeTransaction(articleId, email);
    }
    @Transactional
    public String executeLikeTransaction(Long articleId, String email) {
        User user = userMapper.findByEmail(email);
        Long userId = Long.valueOf(user.getId());

        int count = articleMapper.checkExists(userId, articleId);

        if (count > 0) {
            articleMapper.deleteLike(userId, articleId);
            return "取消點讚成功";
        } else {
            try {
                articleMapper.insertLike(userId, articleId);
                return "點讚成功";
            } catch (DuplicateKeyException e) {
                // 雙重保險：萬一 Redis 鎖極端情況下失效，這裡兜底拋出異常，不噴 500
                throw new BusinessException(400, "你已經點讚過囉！");
            }
        }
    }
}
