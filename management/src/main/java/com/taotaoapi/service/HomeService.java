package com.taotaoapi.service;

import com.taotaoapi.exception.BusinessException;
import com.taotaoapi.home.*;
import com.taotaoapi.home.article.*;
import com.taotaoapi.home.country.CountryResponse;
import com.taotaoapi.mapper.HomeMapper;
import com.taotaoapi.mapper.UserMapper;
import com.taotaoapi.user.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

}