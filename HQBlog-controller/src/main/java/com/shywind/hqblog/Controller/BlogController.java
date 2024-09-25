package com.shywind.hqblog.Controller;

import com.shywind.hqblog.DTO.BlogDTO;
import com.shywind.hqblog.Result.Result;
import com.shywind.hqblog.service.BlogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
public class BlogController {
    @Autowired
    private BlogService blogService;

    @GetMapping("/blog/new-id")
    public Result getNewBlogId(Integer uid) {
        return blogService.getNewBlogId(uid);
    }

    @PostMapping("/blog")
    public Result saveBlog(@RequestBody BlogDTO blogDTO) {
        return blogService.saveBlog(blogDTO);
    }

    @GetMapping("/blog/create-blog")
    public Result getCreateBlog(Integer id) {return blogService.getCreateBlog(id);}

    @GetMapping("/blog/my-blogs")
    public Result getMyBlogs(Integer uid, String state){return blogService.getMyBlogs(uid, state);}

    @GetMapping("/blog/all-blogs")
    public Result getAllBlogs(){return blogService.getAllBlogs(false);}

    @DeleteMapping("/blog")
    public Result deleteBlog(Integer id, String deleteState) {return blogService.deleteBlog(id, deleteState);}

    @GetMapping("/blog/view-blog")
    public Result getViewBlog(Integer id, Integer uid) {return blogService.getViewBlog(id, uid);}

    @GetMapping("/blog/toggle-like")
    public Result toggleLike(Integer id, Integer uid){
       return blogService.toggleLike(id, uid);}

    @GetMapping("/blog/add_all_blogs_to_es")
    public Result addAllBlogsToES(){
        return blogService.addAllBlogsToES();
    }
}
