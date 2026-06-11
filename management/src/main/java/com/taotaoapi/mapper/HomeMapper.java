package com.taotaoapi.mapper;

import com.taotaoapi.home.CategoryResponse;
import com.taotaoapi.home.CountryResponse;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface HomeMapper {
    List<CountryResponse> selectAllCountry();

    List<CategoryResponse> selectAllCategory();
}
