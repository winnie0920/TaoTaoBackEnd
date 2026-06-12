package com.taotaoapi.home;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TagsResponse {
    private Integer id;
    private String key;
    private String name;
    private String icon;
}