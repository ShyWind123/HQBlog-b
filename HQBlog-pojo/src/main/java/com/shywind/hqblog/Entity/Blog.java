package com.shywind.hqblog.Entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("blog")
public class Blog {
    private Integer id;
    private Integer uid;
    private String title;
    private String summary;
    private String content;
    private String createTime;
    private String updateTime;
    private String submitTime;
    private String state;

    public Blog(Integer id, Integer uid) {
        DateTimeFormatter formatter= DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        this.id = id;
        this.uid = uid;
        this.state = "创建";
        this.createTime = formatter.format(LocalDateTime.now());
    }

    public void clear() {
        DateTimeFormatter formatter= DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        title = null;
        summary = null;
        content = null;
        createTime = formatter.format(LocalDateTime.now());
        updateTime = null;
        submitTime = null;
        state = "创建";
    }

    public boolean isAllEmpty() {
        if (content == null || content.replaceAll("\r\n|\r|\n", "").equals("")) {
            return true;
        }
        return false;
    }

    public void removeContentPic() {
        content = content.replaceAll("!\\[[^\\]]*]\\([^)]*\\)","");
    }
}
