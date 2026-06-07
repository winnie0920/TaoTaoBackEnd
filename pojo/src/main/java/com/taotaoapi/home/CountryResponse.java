package com.taotaoapi.home;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CountryResponse {
    private String key;
    private String name;
    private String icon;
}