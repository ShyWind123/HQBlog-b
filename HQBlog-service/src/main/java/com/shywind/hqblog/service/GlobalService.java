package com.shywind.hqblog.service;

import com.shywind.hqblog.Result.Result;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface GlobalService {
    Result getRanks();
    void saveHomeRankListToRedis(String rankListStr) throws Exception;

    Result getTags();

    Result searchBlogsByTags(List<String> tags);

    Result searchBlogsByText(String text);
}
