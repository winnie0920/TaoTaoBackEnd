package com.taotaoapi.mapper;

import com.taotaoapi.home.Country;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface HomeMapper {
    List<Country> selectAll();
}
