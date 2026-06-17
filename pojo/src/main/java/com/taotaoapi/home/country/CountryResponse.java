package com.taotaoapi.home.country;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CountryResponse {
    private  int id;
    private String key;
    private String name;
    private String icon;
}