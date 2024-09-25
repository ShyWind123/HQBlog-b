package com.shywind.hqblog.Controller;

import com.shywind.hqblog.Result.Result;
import com.shywind.hqblog.service.GlobalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@CrossOrigin
public class GlobalController {
    @Autowired
    private GlobalService globalService;

    @GetMapping("/global/ranks")
    public Result getRanks(){return globalService.getRanks();}

    @GetMapping("/global/tags")
    public Result getTags(){return globalService.getTags();}

    @GetMapping("/global/search-by-tags")
    public Result searcbBlogsByTags(@RequestParam List<String> tags){
        // 移除传过来数组的空字符串
        System.out.println(tags);
        tags = tags.stream().filter(tag->!tag.equals("")).collect(Collectors.toList());

        return globalService.searchBlogsByTags(tags);
    }

    @GetMapping("/global/search-by-text")
    public Result searchBlogsByText(String text) {
        return globalService.searchBlogsByText(text);
    }
}
