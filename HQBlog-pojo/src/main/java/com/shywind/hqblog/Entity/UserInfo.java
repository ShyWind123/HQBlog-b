package com.shywind.hqblog.Entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@TableName("user")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserInfo {
    @TableId(value = "uid")
    private Integer uid;

    private String username;
    private String email;
    private String password;
    private String avatar;
}
