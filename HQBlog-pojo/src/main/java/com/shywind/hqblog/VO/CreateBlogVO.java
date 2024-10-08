package com.shywind.hqblog.VO;

import cn.hutool.core.util.ZipUtil;
import com.shywind.hqblog.Entity.Blog;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateBlogVO {
    private Integer id;
    private Integer uid;
    private String author;
    private String title;
    private String summary;
    private String content;
    private String state;
    private String date;
    private String time;
    private String[] tags;
    public CreateBlogVO(Blog blog){
        id = blog.getId();
        uid = blog.getUid();
        title = blog.getTitle();
        summary = blog.getSummary();
        content = ZipUtil.unGzip(blog.getContent(),"utf-8");
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
