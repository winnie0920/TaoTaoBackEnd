package com.taotaoapi.service;

import com.taotaoapi.exception.BusinessException;
import com.taotaoapi.home.*;
import com.taotaoapi.mapper.HomeMapper;
import com.taotaoapi.mapper.UserMapper;
import com.taotaoapi.user.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class HomeService {

    private final HomeMapper homeMapper;
    private final UserMapper userMapper;

    public List<CountryResponse> getCountries() {
        return homeMapper.selectAllCountry();
    }

    public List<CategoryResponse> getCategories() {
        return homeMapper.selectAllCategory();
    }

    public List<TagsResponse> getTags(){
        return homeMapper.selectAllTags();
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
        homeMapper.insertArticle(article);
        Long articleId = article.getId();


        // 存 tags
        if (req.getTags() != null) {
            for (Long tagId : req.getTags()) {
                homeMapper.insertArticleTag(articleId, tagId);
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

                homeMapper.insertArticleImage(image);
            }
        }
    }
}