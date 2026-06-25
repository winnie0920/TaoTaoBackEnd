package com.taotaoapi.home.user;

import lombok.Data;

@Data
public class UserDTO {
    private Integer id;
    private String nickname;
    private String intro;
    private String imageUrl;
    private Long postCount;
    private Long favoriteCount;
}
