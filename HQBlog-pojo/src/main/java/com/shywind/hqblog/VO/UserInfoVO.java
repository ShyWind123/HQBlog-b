package com.shywind.hqblog.VO;

import com.shywind.hqblog.Entity.UserInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserInfoVO {
    private Integer uid;

    private String username;
    private String email;
    private String avatar;

    private int likes;
    private int views;
    private int blogs;

    public UserInfoVO(UserInfo userInfo) {
        uid = userInfo.getUid();
        username = userInfo.getUsername();
        email = userInfo.getEmail();
        avatar = userInfo.getAvatar();
    }
}
