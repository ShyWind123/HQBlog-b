package com.shywind.hqblog.VO;

import com.shywind.hqblog.Entity.Blog;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ViewBlogVO {
    private Integer id;
    private Integer uid;
    private String author;
    private String title;
    private String summary;
    private String content;
    private String date;
    private String[] tags;
    private Integer likes;
    private Integer views;
    private Boolean isILike;

    public ViewBlogVO(Blog blog){
        id = blog.getId();
        uid = blog.getUid();
        title = blog.getTitle();
        summary = blog.getSummary();
        content = blog.getContent();
        date = blog.getSubmitTime().split(" ")[0];
    }
}
