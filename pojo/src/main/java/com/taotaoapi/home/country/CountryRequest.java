package com.taotaoapi.home.country;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CountryRequest {
    private Integer id;
    private String countryKey;
    private String name;
    private String icon;
}