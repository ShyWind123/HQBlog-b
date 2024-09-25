package com.shywind.hqblog.Entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("blogs_count")
public class BlogsCount {
    private Integer id;
    private Integer uid;
    private String date;
    private Integer cnt;
}
