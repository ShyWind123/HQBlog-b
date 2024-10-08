package com.shywind.hqblog.VO;

import cn.hutool.core.util.ZipUtil;
import com.shywind.hqblog.Entity.Blog;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ListBlogVO {
    private Integer id;
    private Integer uid;
    private String author;
    private String title;
    private String summary;
    private String state;
    private String date;
    private String time;
    private String[] tags;
    public ListBlogVO(Blog blog){
        id = blog.getId();
        uid = blog.getUid();
        title = blog.getTitle();
        summary = blog.getSummary();
        state = blog.getState();
        if (state.equals("发布")) {
            String dateTime = blog.getSubmitTime();
            date = dateTime.split(" ")[0];
            time = dateTime.split(" ")[1];
        } else if (state.equals("草稿")){
            String dateTime = blog.getUpdateTime();
            date = dateTime.split(" ")[0];
            time = dateTime.split(" ")[1];
        }
    }
}
