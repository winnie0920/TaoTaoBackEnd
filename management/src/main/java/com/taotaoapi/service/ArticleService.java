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
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ArticleService {
    @Autowired
    private StringRedisTemplate redisTemplate;
    private final UserMapper userMapper;
    private final ArticleMapper articleMapper;

    // 取得文章
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

    // 新增、取消按讚
    public ArticleStatus postArticleLike(Long articleId, String email) {
        // 建立鎖的唯一識別 Key
        String lockKey = "lock:like:" + email + ":" + articleId;
        // 使用 Redis，避免因系統當機導致鎖死，若取得失敗代表請求頻繁中
        Boolean isLock = redisTemplate.opsForValue().setIfAbsent(lockKey, "1", Duration.ofMillis(300));
        User user = userMapper.findByEmail(email);
        Long userId = Long.valueOf(user.getId());
        // 表示該用戶有重複請求正在處理，避免重複寫入
        if (Boolean.FALSE.equals(isLock)) {
            return articleMapper.selectArticleStatus(articleId, userId);
        }
        try {
            return this.postLikeTransaction(articleId, userId);
        } finally {
            redisTemplate.delete(lockKey);
        }
    }

    @Transactional
    public ArticleStatus postLikeTransaction(Long articleId, Long userId) {
        // 檢查用戶是否按讚
        int count = articleMapper.checkExists(userId, articleId);
        // 該用戶按過讚
        if (count > 0) {
            articleMapper.deleteLike(userId, articleId);
        } else {
            // 該用戶未按過讚
            articleMapper.insertLike(userId, articleId);
        }
        // 回傳按讚資訊
        return articleMapper.selectArticleStatus(articleId, userId);
    }

    // 取得收藏文章
    public Page<ArticleList> getFavoriteArticleList(ArticleQuery query, String email) {
        User user = userMapper.findByEmail(email);
        Long userId = Long.valueOf(user.getId());

        // 1. 取得收藏列表
        List<ArticleList> list = articleMapper.selectFavoriteArticles(query, userId);

        // 2. 補上圖片與標籤 (這部分你原本應該有在 Service 做這件事)
        // 記得循環 list，呼叫 selectImagesByArticleId 和 selectTagsByArticleId 補齊資料
        list.forEach(article -> {
            article.setImages(articleMapper.selectImagesByArticleId(article.getId()));
            article.setTags(articleMapper.selectTagsByArticleId(article.getId()));
        });

        // 3. 分頁與 Cursor 判斷
        boolean hasMore = (list.size() == query.getSize());
        Long lastId = list.isEmpty() ? null : list.get(list.size() - 1).getId();
        Long total = articleMapper.countFavoriteArticles(userId);

        Page<ArticleList> vo = new Page<>();
        vo.setList(list);
        vo.setHasMore(hasMore);
        vo.setTotal(total);
        vo.setLastId(lastId);

        return vo;
    }

    // 新增、取消收藏
    public ArticleStatus postArticleFavorite(Long articleId, String email) {
        // 鎖 Key 加上 favorite 標識，區隔點讚操作
        String lockKey = "lock:favorite:" + email + ":" + articleId;
        Boolean isLock = redisTemplate.opsForValue().setIfAbsent(lockKey, "1", Duration.ofMillis(300));
        User user = userMapper.findByEmail(email);
        Long userId = Long.valueOf(user.getId());
        if (Boolean.FALSE.equals(isLock)) {
            return articleMapper.selectArticleStatus(articleId, userId);
        }
        try {
            return this.executeFavoriteTransaction(articleId, userId);
        } finally {
            redisTemplate.delete(lockKey);
        }
    }

    @Transactional
    public ArticleStatus executeFavoriteTransaction(Long articleId, Long userId) {
        int count = articleMapper.checkFavoriteExists(userId, articleId);
        if (count > 0) {
            articleMapper.deleteFavorite(userId, articleId);
        } else {
            articleMapper.insertFavorite(userId, articleId);
        }
        return articleMapper.selectArticleStatus(articleId, userId);
    }

    // 取得留言
    public List<Comment> getComments(Long articleId, String email) {
        User user = userMapper.findByEmail(email);
        Integer currentUserId = user.getId();
        return articleMapper.selectComments(articleId, currentUserId);
    }


    // 新增留言
    public Integer postComment(Long articleId, String email, String content) {
        User user = userMapper.findByEmail(email);
        Comment comment = new Comment();
        comment.setArticleId(articleId);
        comment.setUserId(user.getId());
        comment.setContent(content);
        articleMapper.insertComment(comment);
        return articleMapper.countCommentsByArticleId(articleId);
    }

    // 刪除留言
    @Transactional
    public Integer deleteComment(Long id, String email) {
        User user = userMapper.findByEmail(email);
        Comment oldComment = articleMapper.selectCommentById(id, user.getId());
        if (oldComment != null && oldComment.getUserId().equals(user.getId())) {
            // 先刪除該留言的所有「讚」記錄
            articleMapper.deleteCommentLikesByCommentId(id);
            // 再刪除留言本身
            articleMapper.deleteComment(id);
            // 回傳最新的留言總數
            return articleMapper.countCommentsByArticleId(oldComment.getArticleId());
        } else {
            throw new RuntimeException("留言不存在或無權限修改");
        }
    }

    // 新增、取消留言讚
    public Integer postCommentLike(Long commentId, String email) {
        User user = userMapper.findByEmail(email);
        Integer userId = user.getId();

        // Redis 分散式鎖，避免短時間內重複點擊
        String lockKey = "lock:commentLike:" + userId + ":" + commentId;
        Boolean isLock = redisTemplate.opsForValue().setIfAbsent(lockKey, "1", Duration.ofMillis(500));

        if (Boolean.FALSE.equals(isLock)) {
            // 若鎖定中，直接回傳當前讚數 (防止重複提交)
            return articleMapper.countCommentLikes(commentId);
        }
        try {
            return this.executeCommentLikeTransaction(commentId, userId);
        } finally {
            redisTemplate.delete(lockKey);
        }
    }

    @Transactional
    public Integer executeCommentLikeTransaction(Long commentId, Integer userId) {
        // 檢查是否已按讚
        int count = articleMapper.checkCommentLikeExists(commentId, userId);

        if (count > 0) {
            articleMapper.deleteCommentLike(commentId, userId);
        } else {
            articleMapper.insertCommentLike(commentId, userId);
        }
        return articleMapper.countCommentLikes(commentId);
    }
}
