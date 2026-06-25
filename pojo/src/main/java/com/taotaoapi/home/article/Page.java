package com.taotaoapi.home.article;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class Page<T> {
    private List<T> list;
    private Long total;
    private Boolean hasMore;
    private Integer lastId;
}