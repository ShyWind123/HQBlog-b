package com.shywind.hqblog.service.Impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.shywind.hqblog.DTO.BlogDTO;
import com.shywind.hqblog.Entity.Blog;
import com.shywind.hqblog.Result.Result;
import com.shywind.hqblog.Utils.RestHighLevelClientUtils;
import com.shywind.hqblog.VO.AllBlogListVO;
import com.shywind.hqblog.VO.CreateBlogVO;
import com.shywind.hqblog.VO.ViewBlogVO;
import com.shywind.hqblog.mapper.BlogMapper;
import com.shywind.hqblog.mapper.UserMapper;
import com.shywind.hqblog.service.BlogService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class BlogServieImpl implements BlogService {
    @Autowired
    private BlogMapper blogMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    DateTimeFormatter formatter= DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * @description TODO
     * @params uid
     * @return com.shywind.hqblog.Result.Result
     * @author ShyWind
     * @date 2024/6/7 14:59
     */
    @Override
    public Result getNewBlogId(Integer uid) {
        // 清除该用户之前的空blog
        List<Blog> blogs = blogMapper.getMyBlogs(uid);
        for(Blog blog:blogs){
            if (blog.isAllEmpty()) {
//                System.out.println("删除"+blog.getId());
//                blogMapper.deleteById(blog);
                blog.clear();
                blogMapper.updateById(blog);
                return Result.success("成功获取新博客ID！", blog.getId());
            }
        }

        // 查询最后一个blog的id
        Integer cnt = blogMapper.getBlogMaxId();
        if (cnt == null) {
            cnt = 0;
        }
//        Long cnt = blogMapper.selectCount(new QueryWrapper<Blog>());

        // 获得id
//        Long id = cnt + 1;
        Integer id = cnt + 1;
        System.out.println("获取新ID："+id);

        // 写入数据库
        Blog newBlog = new Blog(Math.toIntExact(id),uid);
//        Blog newBlog = new Blog();
//        newBlog.setId(Math.toIntExact(id));
//        newBlog.setUid(uid);
//        newBlog.setState("创建");
//        newBlog.setCreateTime(formatter.format(LocalDateTime.now()));
        blogMapper.insert(newBlog);

        return Result.success("成功获取新博客ID！", id);
    }

    /**
     * @description TODO
     * @params blogDTO
     * @return com.shywind.hqblog.Result.Result
     * @author ShyWind
     * @date 2024/6/7 14:59
     */
    @Override
    public Result saveBlog(BlogDTO blogDTO) {
        // 获取数据库项
        Integer blogId = blogDTO.getId();
        Blog blog = blogMapper.selectById(blogId);

        // 不存在该id
        if (blog == null) {
            return Result.error("不存在该Id！");
        }

        // 查看是否是修改
        Boolean isUpdate = blog.getState().equals("发布");

        // 写入基本信息
        blog.setTitle(blogDTO.getTitle());
        blog.setSummary(blogDTO.getSummary());
        blog.setContent(blogDTO.getContent());
        blog.setUpdateTime(formatter.format(LocalDateTime.now()));
        if (!blog.getState().equals("发布")) {
            blog.setState(blogDTO.getState());
        }
        if (blogDTO.getState().equals("草稿") && blog.getTitle().equals("")) {
            blog.setTitle(formatter.format(LocalDateTime.now()).toString());
        }
        blogMapper.updateById(blog);

        // 写入标签信息
        for (String tag : blogDTO.getTags()) {
            // blog_tag_relation有这一项
            if (blogMapper.hasBTR(tag, blogDTO.getId(), blogDTO.getUid())) {
                continue;
            }

            // 更新tag表
            // 有则更新
            if (blogMapper.hasTag(tag)) {
                blogMapper.addTagCnt(tag);
            } else { // 无则插入[-
                blogMapper.insertTag(tag);
            }

            // 获取该tag的id
            Integer tagId = blogMapper.getTagId(tag);

            // 插入blog_tags_relation表
            blogMapper.insertBlogTagsRelation(tagId, blogId, blogDTO.getUid());
        }

        // 更新其他信息
        if (!isUpdate && blogDTO.getState().equals("发布")) {
            // 写入发布时间
            blog.setSubmitTime(formatter.format(LocalDateTime.now()));
            blogMapper.updateById(blog);

            // 写入blogs_count
            Integer uid = blogDTO.getUid();
            String today = LocalDate.now().toString();
            // 存在条目 更新
            if (blogMapper.hasUserTodayBlogCnt(uid, today)){
                blogMapper.addUserTodayBlogCnt(uid, today);
            } else {// 插入
                blogMapper.insertUserTodayBlogCnt(uid, today, 1);
            }
        }

        /*// 写入es
        addABlogToES(blog);*/

        // 存入消息队列 存入ES
        rabbitTemplate.convertAndSend("ESInsertBlog", "", JSON.toJSONString(blog));

        // 删除 redis中 对主页博客和用户排行的缓存
        deleteHomeRankListFromRedis();
        deleteHomeBlogListFromRedis();

        // 返回结果
        return Result.success("成功" + ((blogDTO.getState().equals("草稿")?"保存！":"发布!")));
    }

    /**
     * @description TODO
     * @params id
     * @return com.shywind.hqblog.Result.Result
     * @author ShyWind
     * @date 2024/6/11 15:59
     */
    @Override
    public Result getCreateBlog(Integer id) {
        // 获取blog
        Blog blog = blogMapper.selectById(id);

        // 返回错误
        if (blog == null) {
            return Result.error("获取博客信息失败！");
        }

        // 转换为返回VO
        CreateBlogVO createBlogVO = new CreateBlogVO(blog);
        // 设置tags
        createBlogVO.setTags(blogMapper.getTagsByBlogId(id));

        return Result.success("获取博客信息成功！", createBlogVO);
    }

    /**
     * @description TODO
     * @params uid
     * @return com.shywind.hqblog.Result.Result
     * @author ShyWind
     * @date 2024/6/12 9:43
     */
    @Override
    public Result getMyBlogs(Integer uid, String state) {
        // 获取原始blog数据
        List<Blog> blogs = blogMapper.getMyBlogs(uid);

        // 处理blog数据
        List<CreateBlogVO> myBlogs = new ArrayList<>();
        for (Blog blog : blogs) {
            if (blog.getState().equals(state)) {
                CreateBlogVO myBlog = new CreateBlogVO(blog);
                myBlog.setTags(blogMapper.getTagsByBlogId(blog.getId()));
                myBlogs.add(myBlog);
            }
        }

        // 返回
        return Result.success("获取个人博客成功！", myBlogs);
    }

    /**
     * @description TODO
     * @params
     * @return com.shywind.hqblog.Result.Result
     * @author ShyWind
     * @date 2024/6/12 9:43
     */
    @Override
    public Result getAllBlogs(Boolean needContent) {
        // redis中有
        String redisKey = "HQBlog:blog:homeBlogList";
        if (redisTemplate.hasKey(redisKey)) {
            List<AllBlogListVO> blogList = JSONArray.parseArray((String) redisTemplate.opsForValue().get(redisKey),AllBlogListVO.class);
            return Result.success("获取所有博客成功！", blogList);
        }

        // 获取原始blog数据
        List<Blog> blogs = blogMapper.getAllBlogs("发布",needContent);

        // 处理blog数据
        List<AllBlogListVO> allBlogs = new ArrayList<>();
        for (Blog blog : blogs) {
            AllBlogListVO allBlog = new AllBlogListVO(blog);
            allBlog.setTags(blogMapper.getTagsByBlogId(blog.getId()));
            allBlog.setAuthor(userMapper.getUsernaemByUid(blog.getUid()));
            allBlogs.add(allBlog);
        }

        // 存入redis
        rabbitTemplate.convertAndSend("saveHomeBlogListToRedis","",JSON.toJSONString(allBlogs));

        // 返回
        return Result.success("获取所有博客成功！", allBlogs);
    }

    /**
     * @description TODO
     * @params blogListStr
     * @return void
     * @author ShyWind
     * @date 2024/9/24 15:57
     */
    @Override
    public void saveHomeBlogListToRedis(String blogListStr) throws Exception {
        String redisKey = "HQBlog:blog:homeBlogList";
        try{
            redisTemplate.opsForValue().set(redisKey,blogListStr,1, TimeUnit.DAYS);
        } catch (Exception e){
            throw e;
        }
    }

    /**
     * @description TODO 
     * @params id
    deleteState
     * @return com.shywind.hqblog.Result.Result
     * @author ShyWind
     * @date 2024/6/13 11:12
     */
    @Override
    public Result deleteBlog(Integer id, String deleteState) {
        // 获取blog
        Blog blog = blogMapper.selectById(id);

        // 不存在
        if (blog == null) {
            return Result.error("该ID不存在！");
        }

        // 所有类型，直接删除 或 删除特点类型
        if (deleteState.equals("all") || deleteState.equals(blog.getState())) {
            // 更新标签信息
            // 获取blog对应的tags
            String[] tags = blogMapper.getTagsByBlogId(id);
            // tag表中该标签cnt-1
            for (String tag: tags){
                blogMapper.deleteTagCount(tag);
            }
            // 删除blog_tag_relation表中对应的项
            blogMapper.deleteBlogTagRelation(id);

            if (blog.getState().equals("发布")) {
                // 更新blogs_count
                Integer uid = blog.getUid();
                String submitday = blog.getSubmitTime().split(" ")[0];
                blogMapper.deleteBlogCount(uid, submitday);
            }

            // 删除blog表
            blogMapper.deleteById(id);

            /*// 删除es
            deleteBlogFromES(blog.getId());*/

            // 存入消息队列
            rabbitTemplate.convertAndSend("ESDeleteBlog", "", blog.getId());

            // 删除 redis中 对主页博客和用户排行的缓存
            deleteHomeRankListFromRedis();
            deleteHomeBlogListFromRedis();

            // 返回
            return Result.success("成功删除！");
        }

        // 返回
        return Result.success("删除失败！");
    }

    /**
     * @description TODO
     * @params id
    uid
     * @return com.shywind.hqblog.Result.Result
     * @author ShyWind
     * @date 2024/6/14 10:07
     */
    @Override
    public Result getViewBlog(Integer id, Integer uid) {
        // 获取blog
        Blog blog = blogMapper.selectById(id);

        // 不存在id
        if (blog == null) {
            return Result.error("该ID不存在！");
        }

        // 非作者查看时，增加views
        if (uid != blog.getUid() && !blogMapper.hasView(id, uid)) {
            // 写进数据库
            blogMapper.addView(id, uid);
            // 删除 redis中 对 用户排行的缓存
            deleteHomeRankListFromRedis();
        }

        // 设置ViewBlogVO
        ViewBlogVO viewBlogVO = new ViewBlogVO(blog);
        // 获取该博客的用户名
        viewBlogVO.setAuthor(userMapper.getUsernaemByUid(blog.getUid()));
        // 获取tags
        viewBlogVO.setTags(blogMapper.getTagsByBlogId(id));
        // 获取博客喜欢数
        Integer likes = blogMapper.getBlogLikes(id);
        viewBlogVO.setLikes(likes == null ? 0 : likes);
        // 获取博客浏览量
        Integer views = blogMapper.getBlogViews(id);
        viewBlogVO.setViews(views == null ? 0 : views);
        // 获取我的喜欢状态
        if (uid != 0) {
            viewBlogVO.setIsILike(blogMapper.getIsILike(id, uid));
        }

        return Result.success("获取博客信息成功！", viewBlogVO);
    }

    /**
     * @description TODO
     * @params id
    uid
     * @return com.shywind.hqblog.Result.Result
     * @author ShyWind
     * @date 2024/6/14 12:07
     */
    @Override
    public Result toggleLike(Integer id, Integer uid) {
        // 获取blog
        Blog blog = blogMapper.selectById(id);

        // blog id不存在
        if (blog == null) {
            return Result.error("该ID不存在！");
        }

        //uid不存在
        if (userMapper.selectById(uid) == null) {
            return Result.error("该UID不存在！");
        }

        // 查看数据库，有则删除
        if (blogMapper.getIsILike(id, uid)){
            blogMapper.deleteILike(id, uid);
        } else { // 无则添加
            blogMapper.addILike(id, uid);
        }

        // 删除 redis中 对主页博客和用户排行的缓存
        deleteHomeRankListFromRedis();
        deleteHomeBlogListFromRedis();

        return Result.success("切换博客喜欢成功！");
    }

    /**
     * @description TODO
     * @params
     * @return com.shywind.hqblog.Result.Result
     * @author ShyWind
     * @date 2024/7/12 16:29
     */
    public Result addAllBlogsToES(){
        List<Blog> blogs = blogMapper.selectList(new QueryWrapper<Blog>());

        for (Blog blog : blogs) {
            addABlogToES(blog);
        }

        return Result.success();
    }

    public void addABlogToES(Blog blog) {
        blog.removeContentPic();
        try {
            RestHighLevelClientUtils clientUtils = new RestHighLevelClientUtils();
            clientUtils.postBlog(blog);
            clientUtils.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }


    public void deleteHomeBlogListFromRedis(){
        String redisKey = "HQBlog:blog:homeBlogList";
        redisTemplate.delete(redisKey);
    }
    public void deleteHomeRankListFromRedis(){
        String redisKey = "HQBlog:global:homeRankList";
        redisTemplate.delete(redisKey);
    }
}
