package com.taotaoapi.service;

import com.taotaoapi.home.ArticleRequest;
import com.taotaoapi.home.CategoryResponse;
import com.taotaoapi.home.CountryResponse;
import com.taotaoapi.mapper.HomeMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class HomeService {

    private final HomeMapper homeMapper;

    public List<CountryResponse> getCountries() {
        return homeMapper.selectAllCountry();
    }

    public List<CategoryResponse> getCategories() {
        return homeMapper.selectAllCategory();
    }

    @Transactional
    public void postArticle(ArticleRequest req) {

        // 1️⃣ 存 article
        ArticleRequest article = new ArticleRequest();
        article.setUserId(req.getUserId());
        article.setCountryId((req.getCountryId()));
        article.setCategoryId(req.getCategoryId());
        article.setTitle(req.getTitle());
        article.setContent(req.getContent());

        article = articleRepository.save(article);

        Long articleId = article.getId();

        // 2️⃣ 存 tags (N:M)
        if (req.getTags() != null) {
            for (Long tagId : req.getTags()) {
                ArticleTag at = new ArticleTag();
                at.setArticleId(articleId);
                at.setTagId(tagId);
                articleTagRepository.save(at);
            }
        }

        // 3️⃣ 存 images (1:N)
        if (req.getImageUrls() != null) {

            int i = 0;

            for (String url : req.getImageUrls()) {

                ArticleImage img = new ArticleImage();
                img.setArticleId(articleId);
                img.setImageUrl(url);
                img.setSortOrder(i);
                img.setIsCover(i == 0);
                img.setCreatedTime(LocalDateTime.now());

                articleImageRepository.save(img);

                i++;
            }
        }
    }
}