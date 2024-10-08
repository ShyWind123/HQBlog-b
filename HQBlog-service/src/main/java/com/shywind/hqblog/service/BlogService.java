package com.shywind.hqblog.service;

import com.shywind.hqblog.DTO.BlogDTO;
import com.shywind.hqblog.Result.Result;
import org.springframework.stereotype.Component;

@Component
public interface BlogService {
    public Result getNewBlogId(Integer uid);

    Result saveBlog(BlogDTO blogDTO);

    Result getCreateBlog(Integer id);

    Result getMyBlogs(Integer uid, String state);

    Result getAllBlogs(Boolean needContent);
    void saveHomeBlogListToRedis(String blogListStr) throws Exception;

    Result deleteBlog(Integer id, String deleteState);

    Result getViewBlog(Integer id, Integer uid);

    Result toggleLike(Integer id, Integer uid);

    Result addAllBlogsToES();

    // void convertContentFromStringToByte();
}
