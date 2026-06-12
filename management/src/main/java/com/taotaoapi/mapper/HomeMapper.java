package com.taotaoapi.mapper;

import com.taotaoapi.home.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface HomeMapper {
    List<CountryResponse> selectAllCountry();

    List<CategoryResponse> selectAllCategory();

    List<TagsResponse> selectAllTags();
    void insertArticle(Article article);

    void insertArticleTag(
            @Param("articleId") Long articleId,
            @Param("tagId") Long tagId
    );

    void insertArticleImage(Image image);
}
