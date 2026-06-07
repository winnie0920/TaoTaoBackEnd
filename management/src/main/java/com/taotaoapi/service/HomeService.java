package com.taotaoapi.service;

import com.taotaoapi.home.CountryResponse;
import com.taotaoapi.mapper.HomeMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class HomeService {

    private final HomeMapper homeMapper;

    public List<CountryResponse> getCountries() {

        return homeMapper.selectAll()
                .stream()
                .map(c -> new CountryResponse(
                        c.getCountryKey(),
                        c.getName(),
                        c.getIcon()
                ))
                .toList();
    }
}