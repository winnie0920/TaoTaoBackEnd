package com.taotaoapi.mapper;

import com.taotaoapi.home.*;
import com.taotaoapi.home.article.Article;
import com.taotaoapi.home.article.ArticleList;
import com.taotaoapi.home.article.ArticleQuery;
import com.taotaoapi.home.country.CountryResponse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface HomeMapper {
    List<CountryResponse> selectAllCountry();

    List<CategoryResponse> selectAllCategory();

    List<TagsResponse> selectAllTags();

}
